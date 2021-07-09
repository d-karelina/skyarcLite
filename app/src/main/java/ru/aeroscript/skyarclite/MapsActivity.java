package ru.aeroscript.skyarclite;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ru.aeroscript.skyarclite.api.GetRequest;
import ru.aeroscript.skyarclite.api.JsonParsers;
import ru.aeroscript.skyarclite.databinding.ActivityMapsBinding;
import ru.aeroscript.skyarclite.zones.Zone;

import static ru.aeroscript.skyarclite.Settings.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private HashSet<Zone> zonesOnMap ;
    private CheckBox displayManagedZones ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CheckboxHandler checkboxHandler = new CheckboxHandler() ;
        binding.setHandler(checkboxHandler) ;


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        zonesOnMap = new HashSet<>() ;
        displayManagedZones = findViewById(R.id.displayManagedZones);

        // Add a marker in SPb and move the camera
        LatLng spb = new LatLng(59.563178, 30.188478);
        //mMap.addMarker(new MarkerOptions().position(spb).title("Marker in SPb").icon(
        //        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(spb));

        //устанавлием слушателя передвижения камеры
        mMap.setOnCameraIdleListener(this) ;
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(14.0f);

    }

    // метод, который отвечает за формирование зон
    private void getZones() {

        // формирование GET запроса
        GetRequest getGeoJson = new GetRequest();

        //получаем координаты видимого прямоугольника карты
        LatLngBounds latLng = mMap.getProjection().getVisibleRegion().latLngBounds ;

        //Log.i("квадрат", String.valueOf(latLng)) ;

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("провал","случился") ;

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)  {
                String geo;
                try {
                    //записываем ответ JSON в строку
                    geo = Objects.requireNonNull(response.body()).string();
                    Log.i("json",geo) ;
                    // с помощью статического метода парсим JSON строку в объекты зон
                    ArrayList<Zone> zones = JsonParsers.parseManagedZones(geo) ;

                    //перебираем получившиеся зоны и выводим их на экран.
                    boolean alreadyExists = false ;
                    for (Zone zone: zones) {
                        for (Zone zoneOnMap: zonesOnMap) {
                            if (((zone.getName().equals(zoneOnMap.getName()))
                                    && zone.getCoordinates().equals(zoneOnMap.getCoordinates()))) {
                                alreadyExists = true ;
                                break ;
                            }
                        }

                        if (!(alreadyExists)) {
                            new GeometryBuilder().buildPolygon(zone.getCoordinates());
                            zonesOnMap.add(zone) ;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        try {
            // вызываем запрос, передаем строку, параметры и перечень инструкций
            getGeoJson.run(MANAGED_ZONES_AND_DISTRICTS, latLng, callback);
        } catch (IOException e) {
            e.printStackTrace() ;
        }


    }

    @Override
    public void onCameraIdle() {
        if (displayManagedZones.isChecked()) {
            getZones();
        } else {
            mMap.clear() ;
            zonesOnMap.clear() ;
        }
    }


    public class GeometryBuilder {
        public void buildPolygon (ArrayList<LatLng> coordinates) {
            PolygonOptions polygonOptions = new PolygonOptions()
                    .fillColor(Color.argb(60, 230, 46, 120)).strokeWidth(1)
                    .strokeColor(Color.DKGRAY) ;

            for (LatLng coordinate: coordinates) {
                polygonOptions.add(coordinate) ;
            }

            // в отдельном потоке выводим зоны на карту
            runOnUiThread(() -> mMap.addPolygon(polygonOptions)) ;

            Log.i("рисование","должно быть выполнено") ;
        }

        //дополнительный метод на случай, если появлятся круговые зоны с радиусом.
       /* public void buildCircle (LatLng center, int radius) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(center).radius(radius)
                    .fillColor(Color.argb(70, 0, 255, 255)).strokeWidth(1)
                    .strokeColor(Color.DKGRAY);

            mMap.addCircle(circleOptions);
        }*/
    }
    public class CheckboxHandler {
        public void onCheck(View view) {
            if (displayManagedZones.isChecked()) {
                getZones();
            } else {
                mMap.clear() ;
                zonesOnMap.clear() ;
            }
        }
    }

}
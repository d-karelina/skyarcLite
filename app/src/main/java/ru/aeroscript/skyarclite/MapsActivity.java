package ru.aeroscript.skyarclite;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ru.aeroscript.skyarclite.api.GetRequest;
import ru.aeroscript.skyarclite.api.JsonParsers;
import ru.aeroscript.skyarclite.databinding.ActivityMapsBinding;
import ru.aeroscript.skyarclite.zones.Zone;

import static ru.aeroscript.skyarclite.Settings.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in SPb and move the camera
        LatLng spb = new LatLng(59.563178, 30.188478);
        //mMap.addMarker(new MarkerOptions().position(spb).title("Marker in SPb").icon(
        //        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(spb));

        getZones() ;
    }

    private void getZones() {

        // формирование GET запроса
        GetRequest getGeoJson = new GetRequest();

        //получаем координаты видимого прямоугольника карты
        LatLngBounds latLng = mMap.getProjection().getVisibleRegion().latLngBounds ;

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)  {
                String geo = String.valueOf(response.body());
                ArrayList<Zone> zones = JsonParsers.parseManagedZones(geo) ;
                for (Zone zone: zones) {
                    new GeometryBuilder().buildPolygon(zone.getCoordinates()) ;
                }

            }
        };
        try {
            getGeoJson.run(MANAGED_ZONES_AND_DISTRICTS, latLng, callback);
        } catch (IOException e) {
            e.printStackTrace() ;
        }


    }

    @Override
    public void onCameraMove() {
        getZones() ;
    }

    public class GeometryBuilder {
        public void buildPolygon (ArrayList<LatLng> coordinates) {
            PolygonOptions polygonOptions = new PolygonOptions()
                    .fillColor(Color.argb(70, 0, 255, 255)).strokeWidth(1)
                    .strokeColor(Color.DKGRAY) ;
            for (LatLng coordinate: coordinates) {
                polygonOptions.add(coordinate) ;
            }

            mMap.addPolygon(polygonOptions) ;
        }

       /* public void buildCircle (LatLng center, int radius) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(center).radius(radius)
                    .fillColor(Color.argb(70, 0, 255, 255)).strokeWidth(1)
                    .strokeColor(Color.DKGRAY);

            mMap.addCircle(circleOptions);
        }*/
    }
}
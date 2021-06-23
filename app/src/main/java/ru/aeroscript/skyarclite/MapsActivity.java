package ru.aeroscript.skyarclite;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ru.aeroscript.skyarclite.api.GetRequest;
import ru.aeroscript.skyarclite.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    //private OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // формирование GET запроса
        GetRequest getGeoJson = new GetRequest();

        try {
            getGeoJson.run("https://skyarc-dev.ru/caes/webapi/data/geojson/all", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String geo = String.valueOf(response.body());
                    //ObjectMapper mapper = new ObjectMapper();

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(response);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in SPb and move the camera
        LatLng spb = new LatLng(59.563178, 30.188478);
        mMap.addMarker(new MarkerOptions().position(spb).title("Marker in SPb").icon(
                BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(spb));
        init() ;
    }

    private void init() {

        ArrayList<LatLng> polygons = new ArrayList<>() ;
        polygons.add(new LatLng(-5, -10)) ;
        polygons.add(new LatLng(-5, 0)) ;
        polygons.add(new LatLng(5, 0)) ;
        polygons.add(new LatLng(5, -10)) ;

        new GeometryBuilder().buildPolygon(polygons) ;
        new GeometryBuilder().buildCircle(new LatLng(0, 15), 500000);


    }

    public class GeometryBuilder {
        public void buildPolygon (ArrayList<LatLng> coordinates) {
            PolygonOptions polygonOptions = new PolygonOptions()
                    .fillColor(Color.argb(70, 0, 255, 255)).strokeWidth(1)
                    .strokeColor(Color.DKGRAY) ;
            for (LatLng coordinate: coordinates) {
                polygonOptions.add(coordinate) ;
            }
            //polygonOptions.fillColor(Color.RED).strokeWidth(1).strokeColor(Color.RED) ;
            mMap.addPolygon(polygonOptions) ;
        }

        public void buildCircle (LatLng center, int radius) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(center).radius(radius)
                    .fillColor(Color.argb(70, 0, 255, 255)).strokeWidth(1)
                    .strokeColor(Color.DKGRAY);

            mMap.addCircle(circleOptions);
        }
    }
}
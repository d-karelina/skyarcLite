package ru.aeroscript.skyarclite.zones;


import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;

public class Zone {
    private String name ;
    private LowerLimit lowerLimit ;
    private UpperLimit upperLimit ;
    private ArrayList<LatLng> coordinates;

    public Zone(String name, ArrayList<LatLng> coordinates) {
        this.name = name ;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name ;
    }

    public ArrayList<LatLng> getCoordinates() {
        return coordinates;
    }


}

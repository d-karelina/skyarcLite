package ru.aeroscript.skyarclite.api;




import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Objects;

import ru.aeroscript.skyarclite.zones.Zone;

public class JsonParsers {

    public static ArrayList<Zone> parseManagedZones(String jsonStr) {
        ArrayList<Zone> zones = new ArrayList<>() ;
        JSONObject jsonObject;
        JSONArray array;

        try {
            jsonObject = (JSONObject) JSONValue.parseWithException(jsonStr) ;
            array = (JSONArray) jsonObject.get("features");

            assert array != null;
            for (Object o : array) {
                JSONArray coordinates = (JSONArray)((JSONArray) Objects.requireNonNull(((JSONObject) o).get("horizontalProjection"))).get(0) ;

                ArrayList<LatLng> latLngs = new ArrayList<>() ;
                for (Object coordinate : coordinates) {
                    latLngs.add(new LatLng((double) ((JSONArray) coordinate).get(0),
                            (double) ((JSONArray) coordinate).get(1)));
                }
                zones.add(new Zone((String) ((JSONObject) o).get("name"),latLngs)) ;
            }

        } catch (ParseException e) {
            Log.i("ParseException", "не удалось получить JSONObject из строки");
        }




        return zones ;
    }
}

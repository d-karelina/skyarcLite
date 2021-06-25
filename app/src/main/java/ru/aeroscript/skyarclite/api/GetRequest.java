package ru.aeroscript.skyarclite.api;

import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetRequest {
  OkHttpClient client = new OkHttpClient();

  public void run(String urlString, LatLngBounds latLng, Callback callback) throws IOException {
    String latLngStr = "[" + latLng.toString().replaceAll("[^\\d0-9,.]", "") + "]";
    Log.i("метка",latLngStr) ;

    HttpUrl url = HttpUrl.parse(urlString).newBuilder()
            .addQueryParameter("airspaceType", "CTR")
            .addQueryParameter("boundsRect", latLngStr)
            .build() ;

    Log.i("http", url.toString()) ;
    Request request = new Request.Builder()
        .url(url)
        .build();

    client.newCall(request).enqueue(callback) ;


  }
}
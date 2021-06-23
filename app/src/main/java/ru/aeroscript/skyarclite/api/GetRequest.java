package ru.aeroscript.skyarclite.api;

import android.util.Log;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetRequest {
  OkHttpClient client = new OkHttpClient();

  public void run(String url, Callback callback) throws IOException {
    Request request = new Request.Builder()
        .url(url)
        .build();

    client.newCall(request).enqueue(callback) ;
    System.out.println("я здесь");
    Log.i("метка","я здесь") ;

  }
}
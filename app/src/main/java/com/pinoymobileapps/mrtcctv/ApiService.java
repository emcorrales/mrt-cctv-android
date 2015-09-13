package com.pinoymobileapps.mrtcctv;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ApiService {
    @GET("/mrtcctv2/")
    Call<ResponseBody> stream(@Query("stationId") int stationId, @Query("cameraId") int cameraId);
}


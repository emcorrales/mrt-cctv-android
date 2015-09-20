package com.pinoymobileapps.mrtcctv;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ApiService {
    @GET("/mrtcctv2/")
    Call<ResponseBody> streamV2(@Query("stationId") int stationId, @Query("cameraId") int cameraId);

    @GET("/mrtcctv/")
    Call<ResponseBody> streamV1(@Query("stationId") int stationId, @Query("cameraId") int cameraId);
}


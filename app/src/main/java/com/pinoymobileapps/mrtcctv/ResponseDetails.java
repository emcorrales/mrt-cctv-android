package com.pinoymobileapps.mrtcctv;

import android.graphics.Bitmap;

class ResponseDetails {

    private Bitmap image;
    private int stationId;
    private int cameraId;
    private int responseCode;

    public ResponseDetails(Bitmap image, int stationId, int cameraId, int responseCode) {
        this.image = image;
        this.stationId = stationId;
        this.cameraId = cameraId;
        this.responseCode = responseCode;
    }


    public Bitmap getImage() {
        return image;
    }

    public int getStationId() {
        return stationId;
    }

    public int getCameraId() {
        return cameraId;
    }

    public int getResponseCode() {
        return responseCode;
    }
}

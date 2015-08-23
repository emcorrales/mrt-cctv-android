package com.pinoymobileapps.mrtcctv;

import android.graphics.Bitmap;

class ResponseDetails {

    private Bitmap image;
    private int stationId;
    private int cameraId;
    private int responseCode;
    private boolean isConnected = false;

    ResponseDetails(int stationId, int cameraId) {
        this.stationId = stationId;
        this.cameraId = cameraId;
    }

    Bitmap getImage() {
        return image;
    }

    int getStationId() {
        return stationId;
    }

    int getCameraId() {
        return cameraId;
    }

    int getResponseCode() {
        return responseCode;
    }

    void setImage(Bitmap image) {
        this.image = image;
    }

    void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    boolean isConnected() {
        return isConnected;
    }

    void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
}

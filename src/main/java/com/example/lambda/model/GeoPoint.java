package com.example.lambda.model;

public class GeoPoint {
    private double latitude;
    private double longitude;
    public GeoPoint() {}
    public GeoPoint(double lat, double lon) { this.latitude = lat; this.longitude = lon; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}

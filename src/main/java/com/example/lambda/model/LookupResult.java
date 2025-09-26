package com.example.lambda.model;

public class LookupResult {
    private GeoPoint location;
    private String suburb;
    private String stateElectoralDistrict;
    private String error;

    public LookupResult() {}
    public LookupResult(GeoPoint location, String suburb, String sed, String error) {
        this.location = location; this.suburb = suburb; this.stateElectoralDistrict = sed; this.error = error;
    }
    public static LookupResult notFound(String msg) { return new LookupResult(null, null, null, msg); }

    public GeoPoint getLocation() { return location; }
    public String getSuburb() { return suburb; }
    public String getStateElectoralDistrict() { return stateElectoralDistrict; }
    public String getError() { return error; }

    public void setLocation(GeoPoint location) { this.location = location; }
    public void setSuburb(String suburb) { this.suburb = suburb; }
    public void setStateElectoralDistrict(String stateElectoralDistrict) { this.stateElectoralDistrict = stateElectoralDistrict; }
    public void setError(String error) { this.error = error; }
}

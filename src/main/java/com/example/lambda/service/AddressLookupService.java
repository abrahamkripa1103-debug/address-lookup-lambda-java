package com.example.lambda.service;

import com.example.lambda.config.AppConfig;
import com.example.lambda.http.HttpJson;
import com.example.lambda.model.GeoPoint;
import com.example.lambda.model.LookupResult;
import com.example.lambda.util.AddressUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AddressLookupService {

    private final AppConfig.Config cfg;
    private final HttpJson http;
    private final AddressUtils utils;

    public AddressLookupService(AppConfig.Config cfg, HttpJson http, AddressUtils utils) {
        this.cfg = cfg;
        this.http = http;
        this.utils = utils;
    }

    public LookupResult lookup(String rawAddress) throws Exception {
        utils.validateAddress(rawAddress);
        String addr = utils.normalizeForArcGis(rawAddress);

        GeoPoint point = findPoint(addr);
        if (point == null) return LookupResult.notFound("Address not found: " + rawAddress);

        String suburb = reverseAttr(point, cfg.suburbLayerQuery(), "suburbname");
        String sed    = reverseAttr(point, cfg.sedLayerQuery(),    "districtname");

        return new LookupResult(point, suburb, sed, null);
    }

    private GeoPoint findPoint(String addressForWhere) throws Exception {
        String url = cfg.addressLayerQuery()
            + "?where=" + enc("address = '" + addressForWhere + "'")
            + "&outFields=" + enc("address,gurasid,addressstringoid")
            + "&returnGeometry=true&outSR=4326&f=json";

        JsonNode j = http.get(url);
        JsonNode features = j.path("features");
        if (!features.isArray() || features.size() == 0) return null;
        JsonNode geom = features.get(0).path("geometry");
        if (geom.isMissingNode()) return null;
        double lon = geom.path("x").asDouble();
        double lat = geom.path("y").asDouble();
        return new GeoPoint(lat, lon);
    }

    private String reverseAttr(GeoPoint p, String layerUrl, String field) throws Exception {
        String geometryJson = String.format("{\"x\":%s,\"y\":%s,\"spatialReference\":{\"wkid\":4326}}",
                toPlain(p.getLongitude()), toPlain(p.getLatitude()));

        String url = layerUrl
            + "?geometry=" + enc(geometryJson)
            + "&geometryType=esriGeometryPoint&inSR=4326"
            + "&spatialRel=esriSpatialRelIntersects"
            + "&outFields=" + enc(field)
            + "&returnGeometry=false&f=json";

        JsonNode j = http.get(url);
        JsonNode features = j.path("features");
        if (!features.isArray() || features.size() == 0) return null;
        return features.get(0).path("attributes").path(field).asText(null);
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String toPlain(double d) {
        return java.math.BigDecimal.valueOf(d).toPlainString();
    }
}

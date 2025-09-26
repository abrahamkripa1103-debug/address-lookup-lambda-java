package com.example.lambda.controller;

import com.example.lambda.model.GeoPoint;
import com.example.lambda.model.LookupResult;
import com.example.lambda.service.AddressLookupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressLookupService service;

    @Test
    void lookup_returns200_withValidResult() throws Exception {
        GeoPoint point = new GeoPoint(-33.4296842928957, 149.56705027262);
        LookupResult mockResult = new LookupResult(point, "BATHURST", "BATHURST", null);
        Mockito.when(service.lookup(anyString())).thenReturn(mockResult);

        mockMvc.perform(get("/")
                        .param("address", "346 PANORAMA AVENUE BATHURST")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location.latitude").value(-33.4296842928957))
                .andExpect(jsonPath("$.location.longitude").value(149.56705027262))
                .andExpect(jsonPath("$.suburb").value("BATHURST"))
                .andExpect(jsonPath("$.stateElectoralDistrict").value("BATHURST"));
    }

    @Test
    void lookup_returns400_whenMissingAddress() throws Exception {
        mockMvc.perform(get("/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing required query parameter: address"));
    }

    @Test
    void lookup_returns404_whenNotFound() throws Exception {
        Mockito.when(service.lookup(anyString())).thenReturn(LookupResult.notFound("Address not found: TEST"));

        mockMvc.perform(get("/")
                        .param("address", "TEST")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Address not found: TEST"));
    }
}
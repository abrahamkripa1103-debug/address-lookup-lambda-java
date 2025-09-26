package com.example.lambda.controller;

import com.example.lambda.model.ErrorResponse;
import com.example.lambda.model.LookupResult;
import com.example.lambda.service.AddressLookupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressController {
    private final AddressLookupService svc;
    public AddressController(AddressLookupService svc) { this.svc = svc; }

    @GetMapping("/")
    public ResponseEntity<?> lookup(@RequestParam(name = "address", required = false) String address) {
        if (address == null || address.isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Missing required query parameter: address"));
        }
        try {
            LookupResult result = svc.lookup(address);
            if (result.getError() != null) return ResponseEntity.status(404).body(result);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(502).body(new ErrorResponse("Upstream error: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error: " + ex.getMessage()));
        }
    }
}

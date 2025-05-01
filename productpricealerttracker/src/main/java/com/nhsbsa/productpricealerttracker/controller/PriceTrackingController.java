package com.nhsbsa.productpricealerttracker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingRequest;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingResponse;
import com.nhsbsa.productpricealerttracker.service.PriceTrackingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/pricetracker")
public class PriceTrackingController {

    private final PriceTrackingService priceTrackingService;

    public PriceTrackingController(PriceTrackingService priceTrackingService) {
        this.priceTrackingService = priceTrackingService;
    }

    @PostMapping("/createtrackrecord")
    @Operation(summary = "Create a price tracking request", description = "Tracks a product price and notifies the user when it falls below the desired threshold")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tracking request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<PriceTrackingResponse> trackPrice(@RequestBody @Valid PriceTrackingRequest request) {
        PriceTrackingResponse response = priceTrackingService.trackUserProductPrice(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trackallrequest")
    @Operation(summary = "API to get all price tracking request", description = "Get all info about track request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tracking request created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<UserPriceTracker>> getAllTrackingRecords() {
        List<UserPriceTracker> trackingRecords = priceTrackingService.getAllTrackingRecords();
        return ResponseEntity.ok(trackingRecords);
    }

}

package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AggregateResponse {

    private Pricing pricing;
    private Track track;
    private Shipments shipments;

    public Map<String, Object> getPricing() {
        return pricing.getPricing();
    }

    public Map<String, Object> getTrack() {
        return track.getTrack();
    }

    public Map<String, Object> getShipments() {
        return shipments.getShipment();
    }
}

package com.example.service;

import com.example.dto.Shipments;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "shipment-client", url = "${service.url}")
public interface ShipmentClient {

    @GetMapping("/shipments")
    Shipments getShipments(@RequestParam("q") String query);
}

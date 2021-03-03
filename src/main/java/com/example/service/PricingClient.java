package com.example.service;

import com.example.dto.Pricing;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pricing-client", url = "${service.url}")
public interface PricingClient {

    @GetMapping("/pricing")
    Pricing getPricing(@RequestParam("q") String query);

}

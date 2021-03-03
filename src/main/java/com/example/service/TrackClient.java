package com.example.service;

import com.example.dto.Track;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "track-client", url = "${service.url}")
public interface TrackClient {

    @GetMapping("/track")
    Track getTracking(@RequestParam("q") String query);
}

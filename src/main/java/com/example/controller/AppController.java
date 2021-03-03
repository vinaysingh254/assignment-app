package com.example.controller;

import com.example.helper.QueryData;
import com.example.service.AppClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

@Api(description = "Endpoint for getting aggregation response")
@Slf4j
@RestController
public class AppController {

    @Autowired
    private AppClientService appClientService;
    public static AtomicReference<List<QueryData>> requestQueue = new AtomicReference<>();

    @ApiOperation("Get aggregation response from different apis")
    @GetMapping("/aggregation")
    public DeferredResult<ResponseEntity<?>> getAggregation(@ApiParam("pricing query") @RequestParam("pricing") String pricing,
                                                            @ApiParam("Track query") @RequestParam("track") String track,
                                                            @ApiParam("Shipments query") @RequestParam("shipments") String shipments) {
        log.debug("Started processing asynchronous request");
        final DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        addToQueue(new QueryData(deferredResult, pricing, track, shipments));
        log.info("current queue size: {}", requestQueue.get().size());
        if (requestQueue.get().size() == 1) {
            appClientService.schedulePriceApiCall(requestQueue);
        }
        setResultInOtherThread();
        return deferredResult;
    }

    private void setResultInOtherThread() {
        if (requestQueue.get().size() == 5) {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            forkJoinPool.submit(() -> {
                log.info("Processing request in new thread: " + Thread.currentThread().getName());
                appClientService.callApi(requestQueue);
            });
        }
    }

    private void addToQueue(QueryData q) {
        List<QueryData> queryQueues = requestQueue.get();
        if (queryQueues == null || queryQueues.size() == 5) {
            queryQueues = new ArrayList<>();
        }
        queryQueues.add(q);
        requestQueue.set(queryQueues);
    }
}




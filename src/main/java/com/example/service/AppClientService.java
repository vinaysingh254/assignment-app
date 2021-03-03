package com.example.service;

import com.example.dto.AggregateResponse;
import com.example.dto.Pricing;
import com.example.dto.Shipments;
import com.example.dto.Track;
import com.example.helper.QueryData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppClientService {

    private ReentrantLock lock = new ReentrantLock();
    @Value("${service.url}")
    private String baseUrl;
    @Autowired
    private PricingClient pricingClient;
    @Autowired
    private ShipmentClient shipmentClient;
    @Autowired
    private TrackClient trackClient;
    @Autowired
    private ScheduledExecutorService executorService;

    @PostConstruct
    public void init() {
        log.info("service base url: {}", baseUrl);
    }

    public void callApi(AtomicReference<List<QueryData>> requestQueue) {
        try {
            lock.lock();
            List<QueryData> queryQueues = requestQueue.get();
            if (queryQueues.size() > 0) {
                log.info("callApi size: {}  : start time: {}", queryQueues.size(), Instant.now());
                CompletableFuture<Pricing> pcf = getPricing(queryQueues);
                CompletableFuture<Track> tcf = getTracking(queryQueues);
                CompletableFuture<Shipments> scf = getShipments(queryQueues);
                // Wait until they are all done
                AggregateResponse response = CompletableFuture.allOf(pcf, tcf, scf)
                        .thenApply(ignored -> {
                            Pricing p = pcf.join();
                            Track t = tcf.join();
                            Shipments s = scf.join();
                            return new AggregateResponse(p, t, s);
                        }).join();
                log.info("end time :{}\n {}", Instant.now(), getPrettyPrint(response));
                queryQueues.forEach(queryQueue -> {
                    DeferredResult<ResponseEntity<?>> deferredResult = queryQueue.getDeferredResult();
                    AggregateResponse filterResponse = filterResult(queryQueues, deferredResult, response);
                    deferredResult.setResult(ResponseEntity.ok(filterResponse));
                });
                requestQueue.set(new ArrayList<>());
            }
        } finally {
            lock.unlock();
        }
    }

    public ScheduledFuture<?> schedulePriceApiCall(AtomicReference<List<QueryData>> requestQueue) {
        log.info("task scheduling to run after 5 seconds. Time: {}", Instant.now());
        return executorService.schedule(() -> {
            if (requestQueue.get().size() > 0) {
                log.info("executing scheduled task. Time: {}", Instant.now());
                callApi(requestQueue);
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Async("asyncExecutor")
    private CompletableFuture<Pricing> getPricing(List<QueryData> queryData) {
        String query = queryData.stream().map(q -> q.getPricing()).collect(Collectors.joining(","));
        return CompletableFuture.completedFuture(pricingClient.getPricing(query));
    }

    @Async("asyncExecutor")
    private CompletableFuture<Track> getTracking(List<QueryData> queryData) {
        String query = queryData.stream().map(q -> q.getTrack()).collect(Collectors.joining(","));
        return CompletableFuture.completedFuture(trackClient.getTracking(query));
    }

    @Async("asyncExecutor")
    private CompletableFuture<Shipments> getShipments(List<QueryData> queryData) {
        String query = queryData.stream().map(q -> q.getShipments()).collect(Collectors.joining(","));
        return CompletableFuture.completedFuture(shipmentClient.getShipments(query));
    }

    private AggregateResponse filterResult(List<QueryData> queryQueues,
                                           DeferredResult<ResponseEntity<?>> deferredResult,
                                           AggregateResponse response) {
        Pricing p = new Pricing();
        QueryData queue = queryQueues.stream().
                filter(queryQueue -> queryQueue.getDeferredResult().equals(deferredResult))
                .findFirst().get();
        Arrays.stream(queue.getPricing().split(",")).forEach(s1 -> {
            Object o = response.getPricing().get(s1);
            p.setDetail(s1, o);
        });
        Track t = new Track();
        Arrays.stream(queue.getTrack().split(",")).forEach(s1 -> {
            Object o = response.getTrack().get(s1);
            t.setDetail(s1, o);
        });
        Shipments s = new Shipments();
        Arrays.stream(queue.getShipments().split(",")).forEach(s1 -> {
            Object o = response.getShipments().get(s1);
            s.setDetail(s1, o);
        });
        return AggregateResponse.builder()
                .pricing(p)
                .track(t)
                .shipments(s)
                .build();
    }

    private String getPrettyPrint(AggregateResponse response) {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("invalid response!");
        }
    }

}

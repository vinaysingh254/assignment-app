package com.example.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryData {

    DeferredResult<ResponseEntity<?>> deferredResult;
    String pricing;
    String track;
    String shipments;
}

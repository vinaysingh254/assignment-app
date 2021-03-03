package com.example.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ToString
public class Pricing {

    private Map<String, Object> pricing = new LinkedHashMap<>();

    @JsonAnySetter
    public void setDetail(String key,
                          Object value) {
        pricing.put(key, value);
    }
}

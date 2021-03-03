package com.example.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ToString
public class Track {

    private Map<String, Object> track = new LinkedHashMap<>();

    @JsonAnySetter
    public void setDetail(String key,
                          Object value) {
        track.put(key, value);
    }
}

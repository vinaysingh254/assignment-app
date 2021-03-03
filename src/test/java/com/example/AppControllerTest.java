package com.example;

import com.example.dto.AggregateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class AppControllerTest {

    public static final String BASE_PATH = "/aggregation?";
    private ObjectMapper om = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() throws Exception {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"NL", "CN"});
        list.add(new String[]{"AA", "RR"});
        list.add(new String[]{"BB", "TT"});
        list.add(new String[]{"CC", "YY"});
        list.add(new String[]{"DD", "UU"});
        Map<String, MvcResult> map = new HashMap<>();
        for (String[] item : list) {
            String price = "pricing=" + String.join(",", item);
            String track = "track=" + generateRandomDigits(9) + "," + generateRandomDigits(9);
            String shipments = "shipments=" + generateRandomDigits(9) + "," + generateRandomDigits(9);
            String path = price + "&" + track + "&" + shipments;
            String uri = BASE_PATH + path;
            MvcResult result = mockMvc.perform(get(uri)
                                                       .contentType(APPLICATION_JSON))
                    .andReturn();
            map.put(uri, result);
        }
        assertEquals(5, map.size());
        map.forEach((request, mvcResult) -> {
            try {
                AggregateResponse response = om.readValue(mockMvc.perform(asyncDispatch(mvcResult))
                                                                  .andExpect(status().isOk())
                                                                  .andReturn().getResponse().getContentAsString(), AggregateResponse.class);
                assertNotNull(response);
                assertNotNull(response.getPricing());
                assertNotNull(response.getTrack());
                assertNotNull(response.getShipments());
                //   assertPrice params
                assertEquals(request.substring(21, 26), String.join(",", response.getPricing().keySet().toArray(new String[0])));
                System.out.println(
                        "response received for request " + request + " \n " + om.writerWithDefaultPrettyPrinter().writeValueAsString(
                                response));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private int generateRandomDigits(int n) {
        int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m);
    }
}

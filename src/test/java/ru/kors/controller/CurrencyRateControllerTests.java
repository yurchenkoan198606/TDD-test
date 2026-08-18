package ru.kors.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.kors.parser.impl.CurrencyRateDOMParser;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

@WebMvcTest(CurrencyRateController.class)
@Import({CurrencyRateDOMParser.class})
public class CurrencyRateControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldFindRatesByNameAndDate() throws Exception {
        String currency = "USD";
        String date = "01-01-2014";

        String result = mockMvc.perform(get(String.format("/api/v1/currency-rate/%s/%s", currency, date)))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals("{\"numCode\":\"840\",\"charCode\":\"USD\",\"nominal\":\"1\",\"name\":\"Доллар США\",\"value\":\"32,6587\",\"vunitRate\":\"32,6587\"}", result);
    }
}

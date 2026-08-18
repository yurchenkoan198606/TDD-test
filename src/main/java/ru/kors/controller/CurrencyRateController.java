package ru.kors.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kors.model.CurrencyRate;
import ru.kors.parser.CurrencyRateParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CurrencyRateController {
    private static final String CBR_URL = "https://www.cbr.ru/scripts/XML_daily.asp";
    private final CurrencyRateParser parser;

    @GetMapping("/currency-rate/{charCode}/{date}")
    public CurrencyRate getCurrencyRate(@PathVariable String charCode,
                                  @DateTimeFormat(pattern = "dd-MM-yyyy") @PathVariable LocalDate date) {

        String urlWithDate = String.format("%s?date_req=%s", CBR_URL,
                DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date));
        String ratesXml = getRatesAsXml(urlWithDate );

        List<CurrencyRate> currencyRates = parser.parse(ratesXml);
        return currencyRates.stream().filter(rate -> rate.getCharCode().equals(charCode))
                .findFirst()
                .orElse(null);
    }

    private String getRatesAsXml(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Couldn't connect to CBR", e);
        }
    }
}

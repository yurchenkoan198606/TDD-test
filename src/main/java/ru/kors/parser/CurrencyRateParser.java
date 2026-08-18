package ru.kors.parser;

import ru.kors.model.CurrencyRate;

import java.util.List;

public interface CurrencyRateParser {
    List<CurrencyRate> parse(String ratesAsString);
}

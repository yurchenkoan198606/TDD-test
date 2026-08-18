package ru.kors.parser.impl;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.kors.model.CurrencyRate;
import ru.kors.parser.CurrencyRateParser;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CurrencyRateDOMParser implements CurrencyRateParser {
    @Override
    public List<CurrencyRate> parse(String ratesAsString) {
        List<CurrencyRate> rates = new ArrayList<>();

        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            var db = dbf.newDocumentBuilder();

            try (StringReader reader = new StringReader(ratesAsString)) {
                Document document = db.parse(new InputSource(reader));
                document.getDocumentElement().normalize();

                NodeList nodeList = document.getElementsByTagName("Valute");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        CurrencyRate rate = CurrencyRate.builder()
                                .numCode(element.getElementsByTagName("NumCode").item(0).getTextContent())
                                .charCode(element.getElementsByTagName("CharCode").item(0).getTextContent())
                                .nominal(element.getElementsByTagName("Nominal").item(0).getTextContent())
                                .name(element.getElementsByTagName("Name").item(0).getTextContent())
                                .value(element.getElementsByTagName("Value").item(0).getTextContent())
                                .vunitRate(element.getElementsByTagName("VunitRate").item(0).getTextContent())
                                .build();

                        rates.add(rate);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Xml parsing error, xml: " + e);
            throw new RuntimeException();
        }
        return rates;
    }
}

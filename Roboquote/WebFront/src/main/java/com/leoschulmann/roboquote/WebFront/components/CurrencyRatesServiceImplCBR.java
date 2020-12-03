package com.leoschulmann.roboquote.WebFront.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CurrencyRatesServiceImplCBR implements CurrencyRatesService {

    @Value("${cbr.currency.url}")
    private String cbrUrl;

    private LocalDateTime timestamp;
    private BigDecimal rubEurRate = BigDecimal.ZERO;
    private BigDecimal rubUsdRate = BigDecimal.ZERO;
    private BigDecimal rubJpyRate = BigDecimal.ZERO;

    @Override
    public BigDecimal getRubEurRate() {
        updateData();
        return rubEurRate;
    }

    @Override
    public BigDecimal getRubUSDRate() {
        updateData();
        return rubUsdRate;
    }

    @Override
    public BigDecimal getRubJPYRate() {
        updateData();
        return rubJpyRate;
    }

    private synchronized void updateData() {
        if (timestamp != null &&
                timestamp.isAfter(LocalDateTime.now().minus(6, ChronoUnit.HOURS))) return;

        String dateStr = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());
        try (InputStream is = new URL(cbrUrl + dateStr).openStream()) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(is);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getDocumentElement().getElementsByTagName("Valute");

            List<Element> list = IntStream.range(0, nodeList.getLength())
                    .filter(i -> nodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
                    .mapToObj(i -> (Element) nodeList.item(i))
                    .collect(Collectors.toList());

            for (Element e : list) {
                switch (getTagValue(e, "CharCode")) {
                    case "EUR":
                        rubEurRate = getValue(e).divide(getNominal(e), RoundingMode.HALF_UP);
                        break;
                    case "USD":
                        rubUsdRate = getValue(e).divide(getNominal(e), RoundingMode.HALF_UP);
                        break;
                    case "JPY":
                        rubJpyRate = getValue(e).divide(getNominal(e), RoundingMode.HALF_UP);
                        break;
                }
            }

            timestamp = LocalDateTime.now();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    private BigDecimal getNominal(Element e) {
        return new BigDecimal(getTagValue(e, "Nominal"));
    }

    private BigDecimal getValue(Element e) {
        return new BigDecimal(getTagValue(e, "Value").replace(',', '.'));
    }

    private String getTagValue(Element e, String tag) {
        return e.getElementsByTagName(tag).item(0).getTextContent();
    }
}

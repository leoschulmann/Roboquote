package com.leoschulmann.roboquote.itemservice.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import org.javamoney.moneta.Money;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ItemDeserializer extends JsonDeserializer<Item> {
    @Override
    public Item deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Item item = new Item();
        item.setPartno(node.get("part-number").asText());
        item.setBrand(node.get("brand").asText());
        item.setNameRus(node.get("name-russian").asText());
        item.setNameEng(node.get("name-english").asText());
        item.setMargin(node.get("selling-margin").doubleValue());
        String buyingCur = node.get("currency-buying").asText();
        BigDecimal buyingAmt = node.get("amount-buying").decimalValue();
        item.setBuyingPrice(Money.of(buyingAmt, buyingCur));
        String sellingCur = node.get("currency-selling").asText();
        BigDecimal sellingAmt = node.get("amount-selling").decimalValue();
        item.setSellingPrice(Money.of(sellingAmt, sellingCur));

        if (node.has("id")) item.setId(node.get("id").intValue());
        if (node.has("date-created")) {
            item.setCreated(LocalDate.parse(node.get("date-created").textValue(), DateTimeFormatter.ISO_DATE));
        }
        if (node.has("date-modified")) {
            item.setCreated(LocalDate.parse(node.get("date-modified").textValue(), DateTimeFormatter.ISO_DATE));
        }
        return item;
    }
}

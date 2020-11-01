package com.leoschulmann.roboquote.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.leoschulmann.roboquote.entities.Item;
import org.javamoney.moneta.Money;

import java.io.IOException;
import java.math.BigDecimal;

public class ItemDeserializer extends JsonDeserializer<Item> {
    @Override
    public Item deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Item item = new Item();
        item.setNameRus(node.get("name-russian").textValue());
        item.setNameEng(node.get("name-english").asText());
        item.setMargin(node.get("selling-margin").doubleValue());
        item.setPartno(node.get("part-number").asText());
        String cur = node.get("currency-buying").asText();
        BigDecimal amt = node.get("amount-buying").decimalValue();
        item.setBuyingPrice(Money.of(amt, cur));

        String curSelling = node.get("currency-selling").asText();
        BigDecimal amtSelling = node.get("amount-selling").decimalValue();
        item.setSellingPrice(Money.of(amtSelling, curSelling));

        return item;
    }
}

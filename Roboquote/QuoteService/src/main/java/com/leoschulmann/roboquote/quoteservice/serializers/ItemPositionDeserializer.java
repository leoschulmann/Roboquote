package com.leoschulmann.roboquote.quoteservice.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import org.javamoney.moneta.Money;

import java.io.IOException;
import java.math.BigDecimal;

public class ItemPositionDeserializer extends JsonDeserializer<ItemPosition> {
    @Override
    public ItemPosition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ItemPosition item = new ItemPosition();
//        item.setId(node.get("id").intValue());
        item.setName(node.get("name").asText());
        String sellingCur = node.get("currency-selling").asText();
        BigDecimal sellingAmt = node.get("amount-selling").decimalValue();
        item.setSellingPrice(Money.of(sellingAmt, sellingCur));
        item.setQty(node.get("quantity").intValue());
        item.setItemId(node.get("inventory_id").intValue());
        return item;
    }
}

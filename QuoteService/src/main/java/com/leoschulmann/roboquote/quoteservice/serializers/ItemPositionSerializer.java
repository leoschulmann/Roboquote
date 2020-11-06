package com.leoschulmann.roboquote.quoteservice.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;

import java.io.IOException;

public class ItemPositionSerializer extends JsonSerializer<ItemPosition> {

    @Override
    public void serialize(ItemPosition item, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
//        jsonGenerator.writeNumberField("id", item.getId());
        jsonGenerator.writeStringField("name", item.getName());
        jsonGenerator.writeStringField("currency-selling", item.getSellingPrice().getCurrency().getCurrencyCode());
        jsonGenerator.writeNumberField("amount-selling", item.getSellingPrice().getNumberStripped());
        jsonGenerator.writeNumberField("quantity", item.getQty());
        jsonGenerator.writeNumberField("inventory_id", item.getItemId());
        jsonGenerator.writeEndObject();
    }
}


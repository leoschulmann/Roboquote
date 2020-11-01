package com.leoschulmann.roboquote.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.leoschulmann.roboquote.entities.Item;

import java.io.IOException;

public class ItemSerializer extends JsonSerializer<Item> {

    @Override
    public void serialize(Item item, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", item.getId());
        jsonGenerator.writeStringField("part-number", item.getPartno());
        jsonGenerator.writeStringField("name-russian", item.getNameRus());
        jsonGenerator.writeStringField("name-english", item.getNameEng());
        jsonGenerator.writeNumberField("amount-selling", item.getSellingPrice().getNumberStripped());
        jsonGenerator.writeStringField("currency-selling", item.getSellingPrice().getCurrency().getCurrencyCode());
        jsonGenerator.writeEndObject();
    }
}
package com.leoschulmann.roboquote.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.leoschulmann.roboquote.model.Item;

import java.io.IOException;

public class ItemSerializer extends JsonSerializer<Item> {

    @Override
    public void serialize(Item item, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("part-number", item.getPartno());
        jsonGenerator.writeStringField("name-russian", item.getNameRus());
        jsonGenerator.writeStringField("name-english", item.getNameEng());
        jsonGenerator.writeNumberField("price", item.getSellingPrice().getNumberStripped());
        jsonGenerator.writeStringField("currency", item.getSellingPrice().getCurrency().getCurrencyCode());
        jsonGenerator.writeEndObject();
    }
}

package com.leoschulmann.roboquote.itemservice.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.leoschulmann.roboquote.itemservice.entities.Item;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ItemSerializer extends JsonSerializer<Item> {

    @Override
    public void serialize(Item item, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", item.getId());
        jsonGenerator.writeStringField("part-number", item.getPartno());
        jsonGenerator.writeStringField("brand", item.getBrand());
        jsonGenerator.writeStringField("name-russian", item.getNameRus());
        jsonGenerator.writeStringField("name-english", item.getNameEng());
        jsonGenerator.writeNumberField("selling-margin", item.getMargin());
        jsonGenerator.writeStringField("currency-buying", item.getBuyingPrice().getCurrency().getCurrencyCode());
        jsonGenerator.writeNumberField("amount-buying", item.getBuyingPrice().getNumberStripped());
        jsonGenerator.writeStringField("currency-selling", item.getSellingPrice().getCurrency().getCurrencyCode());
        jsonGenerator.writeNumberField("amount-selling", item.getSellingPrice().getNumberStripped());
        jsonGenerator.writeStringField("date-created", item.getCreated().format(DateTimeFormatter.ISO_DATE));
        jsonGenerator.writeStringField("date-modified", item.getModified().format(DateTimeFormatter.ISO_DATE));
        jsonGenerator.writeBooleanField("overridden-sell-price", item.isOverridden());
        jsonGenerator.writeEndObject();
    }
}
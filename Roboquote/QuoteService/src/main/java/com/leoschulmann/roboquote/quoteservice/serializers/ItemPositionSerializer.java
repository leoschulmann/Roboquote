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
        jsonGenerator.writeNumberField("id", item.getId() == null ? -1 : item.getId());
        jsonGenerator.writeStringField("part-number", item.getPartNo());
        jsonGenerator.writeStringField("name", item.getName());
        jsonGenerator.writeStringField("currency-selling", item.getSellingPrice().getCurrency().getCurrencyCode());
        jsonGenerator.writeNumberField("amount-selling", item.getSellingPrice().getNumberStripped());
        jsonGenerator.writeNumberField("quantity", item.getQty());
        jsonGenerator.writeStringField("currency-selling-sum", item.getSellingSum().getCurrency().getCurrencyCode());
        jsonGenerator.writeNumberField("amount-selling-sum", item.getSellingSum().getNumberStripped());
        jsonGenerator.writeNumberField("inventory_id", item.getItemId());
        jsonGenerator.writeNumberField("q-section-id", item.getSection() == null ? -1 : item.getSection().getId());

        jsonGenerator.writeEndObject();
    }
}


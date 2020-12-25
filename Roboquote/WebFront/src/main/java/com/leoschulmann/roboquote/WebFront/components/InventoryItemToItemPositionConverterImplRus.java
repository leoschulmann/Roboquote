package com.leoschulmann.roboquote.WebFront.components;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.exceptions.NoInventoryItemFound;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryItemToItemPositionConverterImplRus implements InventoryItemToItemPositionConverter {

    @Value("${itemservice.url}")
    String url;

    @Autowired
    AuthService authService;

    @Autowired
    public void setRestTemplate(RestTemplate rt) {
        this.restTemplate = rt;
    }

    private RestTemplate restTemplate;

    static ObjectMapper om = new ObjectMapper(new JsonFactory());

    @Override
    public ItemPosition convert(Item item) {
        return new ItemPosition(item.getNameRus(),
                item.getPartno(),
                item.getSellingPrice(),
                1,
                item.getId());
    }

    @Override
    public ItemPosition createItemPositionByItemId(Integer inventoryId, Integer qty) {
        HttpEntity<String> entity = authService.provideHttpEntityWithCredentials();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url+inventoryId, HttpMethod.GET, entity, String.class);

        try {
            JsonNode node = om.readTree(responseEntity.getBody());

            ItemPosition ip = new ItemPosition();
            ip.setName(node.get("name-russian").asText());
            ip.setSellingPrice(Money.of(
                    node.get("amount-selling").decimalValue(),
                    node.get("currency-selling").asText()));
            ip.setQty(qty);
            ip.setSellingSum(ip.getSellingPrice().multiply(ip.getQty()));
            ip.setPartNo(node.get("part-number").asText());
            ip.setItemId(node.get("id").intValue());
            return ip;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new NoInventoryItemFound(inventoryId);
        }
    }
}

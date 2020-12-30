package com.leoschulmann.roboquote.WebFront.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
class InventoryItemToItemPositionConverterImplRusTest {

    private static InventoryItemToItemPositionConverterImplRus converter;


    @BeforeAll
    public static void init() {
        AuthService mockAuthService = Mockito.mock(AuthServiceImplBasic.class);
        Mockito.when(mockAuthService.provideHttpEntityWithCredentials()).thenReturn(null);
        converter = new InventoryItemToItemPositionConverterImplRus(mockAuthService);
    }

    @Test
    void convert() {
        Item item = new Item();
        item.setCreated(LocalDate.now());
        item.setNameRus("qwerty");
        item.setBuyingPrice(Money.of(100, "RUB"));
        item.setMargin(20.);
        item.setId(1);

        ItemPosition ip = converter.convert(item);
        assertEquals(1, ip.getItemId());
        assertEquals("qwerty", ip.getName());
        assertEquals(125, ip.getSellingPrice().getNumber().intValueExact());
    }

    @Test
    void createItemPositionByItemId() throws JsonProcessingException {
        Item item = new Item();
        item.setCreated(LocalDate.now());
        item.setModified(LocalDate.now());
        item.setNameRus("qwerty");
        item.setBuyingPrice(Money.of(100, "RUB"));
        item.setMargin(20.);
        item.setId(1234);
        item.setPartno("999-000");

        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(item);

        ResponseEntity<String> mockResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(mockResponse.getBody()).thenReturn(json);
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(mockRestTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any()
        )).thenReturn(mockResponse);

        converter.setRestTemplate(mockRestTemplate);  //@InjectMocks somehow does not work :-/

        ItemPosition ip = converter.createItemPositionByItemId(1234, 3);

        assertEquals(375, ip.getSellingSum().getNumber().intValueExact());

    }
}

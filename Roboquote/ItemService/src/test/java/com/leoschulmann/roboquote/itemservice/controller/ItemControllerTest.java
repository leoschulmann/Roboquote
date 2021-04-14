package com.leoschulmann.roboquote.itemservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.services.ItemService;
import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemService itemService;
    private MockMvc mockMvc;

    private Item item1;
    private Item item2;
    private ObjectMapper objectMapper;

    public ItemControllerTest() {
        itemService = Mockito.mock(ItemService.class);
        itemController = new ItemController(itemService);
    }

    @Before
    public void prepare() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        item1 = new Item(1, "a", "01", "item1", "item1", Money.of(1, "USD"), 10.,
                Money.of(100, "USD"), LocalDate.now(), LocalDate.now(), true);
        item2 = new Item(2, "b", "02", "item2", "item2", Money.of(1, "USD"), 10.,
                Money.of(100, "USD"), LocalDate.now(), LocalDate.now(), true);
        objectMapper = new ObjectMapper();

    }

    @Test
    public void testGetAll() throws Exception {
        List<Item> list = List.of(item1, item2);
        Mockito.doReturn(list).when(itemService).getAllDtos();
        mockMvc.perform(MockMvcRequestBuilders.get("/item/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("item2")));
    }

    @Test
    public void testPost() throws Exception {
        String itemAsString = objectMapper.writeValueAsString(item1);
        mockMvc.perform(MockMvcRequestBuilders.post("/item/").content(itemAsString).contentType("application/json"))
                .andExpect(status().isCreated());
    }


    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/item/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void putTest() throws Exception {
        String itemAsString = objectMapper.writeValueAsString(item1);
        mockMvc.perform(MockMvcRequestBuilders.put("/item/").content(itemAsString).contentType("application/json"))
                .andExpect(status().isOk());
    }
}

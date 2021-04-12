package com.leoschulmann.roboquote.itemservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoschulmann.roboquote.itemservice.config.TestJpaConfig;
import com.leoschulmann.roboquote.itemservice.dto.ItemDto;
import com.leoschulmann.roboquote.itemservice.dto.ItemId;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.exceptions.ExceptionProcessor;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import com.leoschulmann.roboquote.itemservice.services.ItemBundleDtoConverter;
import com.leoschulmann.roboquote.itemservice.services.NewItemService;
import com.leoschulmann.roboquote.itemservice.util.TestFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TestJpaConfig.class})
public class ItemTest {

    @Autowired
    ItemRepository itemRepository;

    NewItemController itemController;
    NewItemService itemService;
    ItemBundleDtoConverter dtoConverter;
    MockMvc mockMvc;
    ObjectMapper om;

    @BeforeAll
    void prepare() {
        dtoConverter = new ItemBundleDtoConverter();
        itemService = new NewItemService(itemRepository, dtoConverter);
        itemController = new NewItemController(itemService);

        mockMvc = MockMvcBuilders.standaloneSetup(itemController).setControllerAdvice(new ExceptionProcessor()).build();
        om = new ObjectMapper();
        List<Item> items = TestFactory.itemFactory("coffee", "beer", "cocoa", "cola", "juice");
        itemRepository.saveAll(items);
    }

    @Test
    void testGetById() throws Exception {
        int id = 1;
        MvcResult res = mockMvc.perform(get("/item/new/" + id))
                .andExpect(status().isOk()).andReturn();
        ItemDto dto = convertMvcResultToDto(res);
        assertEquals("coffee", dto.getNameEng());
    }

    @Test
    void testGetSeveralByIds() throws Exception {
        List<ItemId> ids = List.of(new ItemId(1), new ItemId(3), new ItemId(5));
        MvcResult res = mockMvc.perform(post("/item/new/multiple")
                .contentType("application/json").characterEncoding("UTF-8").content(convertToJson(ids)))
                .andReturn();
        List<ItemDto> list = convertMvcResultToDtos(res);
        assertEquals(3, list.size());
        assertEquals("juice", list.get(2).getNameEng());
    }

    @Test
    void testGetAll() throws Exception {
        MvcResult res = mockMvc.perform(get("/item/new")).andReturn();
        List<ItemDto> list = convertMvcResultToDtos(res);
        assertEquals(5, list.size());
    }

    @Test
    void testEdit() throws Exception {
        ItemDto itemDto = dtoConverter.convertToItemDto(TestFactory.itemFactory("potato").get(0));
        int id = 4;
        mockMvc.perform(put("/item/new/" + id).content(convertToJson(itemDto))
                .contentType("application/json").characterEncoding("UTF-8"));

        MvcResult res = mockMvc.perform(get("/item/new/" + id)).andReturn();
        ItemDto dto = convertMvcResultToDto(res);
        assertEquals("potato", dto.getNameEng());
    }

     @Test
     void testAddNew() throws Exception {
         ItemDto itemDto = dtoConverter.convertToItemDto(TestFactory.itemFactory("sweet pie").get(0));
         mockMvc.perform(post("/item/new").content(convertToJson(itemDto))
                 .contentType("application/json").characterEncoding("UTF-8"))
                 .andExpect(MockMvcResultMatchers.status().isCreated());

         MvcResult res = mockMvc.perform(get("/item/new")).andReturn();
         List<ItemDto> list = convertMvcResultToDtos(res);
         assertEquals(6, list.size());
         assertEquals("sweet pie", list.get(5).getNameEng());
     }

     @Test
     void testDelete() throws Exception {
         int id = 2;
         mockMvc.perform(delete("/item/new/" + id)).andExpect(MockMvcResultMatchers.status().isNoContent());

         MvcResult res = mockMvc.perform(get("/item/new")).andReturn();
         List<ItemDto> list = convertMvcResultToDtos(res);
         assertEquals(4, list.size());
     }

    @Test
    void testSearch() throws Exception {
        String q = "co";
        MvcResult res = mockMvc.perform(get("/item/new/search/" + q)).andReturn();
        List<ItemDto> list = convertMvcResultToDtos(res);
        assertEquals(3, list.size());
        assertEquals("coffee", list.get(0).getNameEng());
        assertEquals("cocoa", list.get(1).getNameEng());
        assertEquals("cola", list.get(2).getNameEng());
    }


    String convertToJson(Object o) throws JsonProcessingException {
        return om.writeValueAsString(o);
    }

    ItemDto convertMvcResultToDto(MvcResult res) throws Exception {
        String json = res.getResponse().getContentAsString();
        return om.readValue(json, ItemDto.class);
    }

    List<ItemDto> convertMvcResultToDtos(MvcResult res) throws Exception {
        String json = res.getResponse().getContentAsString();
        return asList(om.readValue(json, ItemDto[].class));
    }
}

package com.leoschulmann.roboquote.itemservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoschulmann.roboquote.itemservice.config.TestJpaConfig;
import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.BundleItemDto;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.exceptions.ExceptionProcessor;
import com.leoschulmann.roboquote.itemservice.repositories.BundleRepository;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import com.leoschulmann.roboquote.itemservice.services.BundleService;
import com.leoschulmann.roboquote.itemservice.services.ItemBundleDtoConverter;
import com.leoschulmann.roboquote.itemservice.util.TestFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.transaction.Transactional;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TestJpaConfig.class})
public class BundleTest {
    @Autowired
    BundleRepository bundleRepository;

    @Autowired
    ItemRepository itemRepository;

    BundleService bundleService;
    BundleController bundleController;
    ItemBundleDtoConverter dtoConverter;
    MockMvc mockMvc;
    ObjectMapper om;

    @Test
    void sanityCheck() {
        Assertions.assertNotNull(bundleRepository);
        Assertions.assertNotNull(itemRepository);
    }

    @BeforeAll
    void prepare() {
        Validator validator = Mockito.mock(Validator.class);
        Mockito.when(validator.validate(any(BundleItemDto.class))).thenReturn(new HashSet<>());
        dtoConverter = new ItemBundleDtoConverter();
        bundleService = new BundleService(bundleRepository, dtoConverter, itemRepository);
        bundleController = new BundleController(bundleService, validator);

        mockMvc = MockMvcBuilders.standaloneSetup(bundleController).setControllerAdvice(new ExceptionProcessor()).build();
        om = new ObjectMapper();
        List<Item> items = TestFactory.itemFactory("coffee", "beer", "cocoa", "cola", "juice");
        itemRepository.saveAll(items);

        List<Bundle> bundles = List.of(
                TestFactory.bundleFactory("bundle1", items.get(0), items.get(1), items.get(2)), //id=6
                TestFactory.bundleFactory("bundle2", items.get(3), items.get(4)),               //id=10
                TestFactory.bundleFactory("bundle3", items.get(0), items.get(2), items.get(4))  //id=13
        );
        bundleRepository.saveAll(bundles);
    }

    @Test
    void testGetNamesIds() throws Exception {
        MvcResult res = mockMvc.perform(get("/bundle")).andReturn();
        String str = res.getResponse().getContentAsString();
        List<BundleDto> dtos = Arrays.asList(om.readValue(str, BundleDto[].class));
        assertEquals(3, dtos.size());
        assertTrue(dtos.stream().anyMatch(d -> d.getName().equals("bundle1")));
    }

    @Test
    void testGetSingle() throws Exception {
        int bundleid = 6;
        MvcResult res = mockMvc.perform(get("/bundle/" + bundleid)).andReturn();
        String json = res.getResponse().getContentAsString();
        BundleDto dto = om.readValue(json, BundleDto.class);
        assertEquals("bundle1", dto.getName());
        assertEquals(3, dto.getItems().size());
        assertTrue(dto.getItems().stream().anyMatch(i -> i.getName().equals("beer")));
    }

    @Test
    void addNewBundle() throws Exception {
        List<BundleItemDto> list = List.of(new BundleItemDto(1, 10), new BundleItemDto(2, 20));
        BundleDto dto = new BundleDto("my new bundle", list);
        String json = om.writeValueAsString(dto);
        mockMvc.perform(post("/bundle").contentType("application/json").characterEncoding("UTF-8")
                .content(json)).andDo(print()).andExpect(status().isOk());

        MvcResult res = mockMvc.perform(get("/bundle")).andReturn();
        String str = res.getResponse().getContentAsString();
        List<BundleDto> dtos = Arrays.asList(om.readValue(str, BundleDto[].class));
        assertEquals(4, dtos.size());
    }

    @Test
    void deleteBundle() throws Exception {
        int bundleid = 6;
        mockMvc.perform(delete("/bundle/" + bundleid));

        MvcResult res = mockMvc.perform(get("/bundle")).andReturn();
        String str = res.getResponse().getContentAsString();
        List<BundleDto> dtos = Arrays.asList(om.readValue(str, BundleDto[].class));
        assertEquals(2, dtos.size());
    }


    @Test
    void editBundle() throws Exception {
        int bundleid = 6;
        int itemid = 5;
        int qty = 99;
        List<BundleItemDto> items = List.of(new BundleItemDto(itemid, qty));
        BundleDto dto = new BundleDto("new name", items);
        String json = om.writeValueAsString(dto);
        mockMvc.perform(put("/bundle/" + bundleid).contentType("application/json").characterEncoding("UTF-8")
                .content(json)).andDo(print()).andExpect(status().isOk());

        MvcResult res = mockMvc.perform(get("/bundle/" + bundleid)).andReturn();
        BundleDto newdto = om.readValue(res.getResponse().getContentAsString(), BundleDto.class);
        assertEquals("new name", newdto.getName());

        List<BundleItemDto> newitems = newdto.getItems();
        assertEquals(1, newitems.size());
        assertEquals("juice", newitems.get(0).getName());
        assertEquals(99, newitems.get(0).getQty());
    }
}
////
//        mockMvc.perform(MockMvcRequestBuilders.put("/bundle/" + id)
//                .contentType("application/json").content(json)).andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());

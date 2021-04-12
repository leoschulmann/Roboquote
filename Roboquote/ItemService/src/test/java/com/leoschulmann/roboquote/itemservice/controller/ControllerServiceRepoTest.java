package com.leoschulmann.roboquote.itemservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoschulmann.roboquote.itemservice.config.TestJpaConfig;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.exceptions.ExceptionProcessor;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import com.leoschulmann.roboquote.itemservice.services.ItemBundleDtoConverter;
import com.leoschulmann.roboquote.itemservice.services.ItemService;
import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
@Transactional
public class ControllerServiceRepoTest {

    @Autowired
    private ItemRepository itemRepository;

    private ItemService itemService;
    private ItemController itemController;
    private MockMvc mockMvc;
    private ObjectMapper om;

    @Before
    public void prepare() {
        itemService = new ItemService(itemRepository, new ItemBundleDtoConverter());
        itemController = new ItemController(itemService);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).setControllerAdvice(new ExceptionProcessor()).build();
        om = new ObjectMapper();
    }

    @Test
    public void saveAndRetrieveTest() {
        Item i = itemFactory("yabba-dabba-doo!").get(0);
        int id = itemRepository.save(i).getId();
        Optional<Item> itemFromDb = itemRepository.findById(id);
        assertEquals("yabba-dabba-doo!", itemFromDb.get().getNameRus());
    }

    @Test
    public void postTest() throws Exception {
        Item item = itemFactory("one").get(0);
        String itemAsString = toJsonString(item);
        mockMvc.perform(post("/item/").content(itemAsString).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("one")));
    }

    @Test
    public void prohibitPostingIncompleteData() throws Exception {
        Item corruptItem = itemFactory("corr").get(0);
        corruptItem.setNameRus(null);
        String itemAsString = toJsonString(corruptItem);
        System.out.println(itemAsString);
        mockMvc.perform(post("/item/").content(itemAsString).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void multipleAdditionsAndGet() throws Exception {
        List<Item> items = itemFactory("one", "two", "three");
        items.forEach(item -> {
            try {
                mockMvc.perform(post("/item/").content(toJsonString(item))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mockMvc.perform(get("/item/")).andDo(print()).andExpect(status().isOk());

        ResultActions resultActions = mockMvc.perform(get("/item/"))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String json = result.getResponse().getContentAsString();
        List<Item> myObjects = Arrays.asList(om.readValue(json, Item[].class));
        assertEquals(3, myObjects.size());
    }

    @Test
    public void getNonexistingId() throws Exception {
        int id = new Random().nextInt(100_000);
        mockMvc.perform(get("/item/" + id)).andExpect(status().isNotFound());
    }


    @Test
    public void editItem() throws Exception {
        Item i = itemFactory("stop laughing at my code!..").get(0);
        MvcResult res = mockMvc.perform(post("/item/").content(toJsonString(i))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String json = res.getResponse().getContentAsString();
        int id = om.readValue(json, Item.class).getId();

        i.setId(id);
        i.setNameRus("nobody will see this");
        mockMvc.perform(put("/item/").content(toJsonString(i))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteNonExisting() throws Exception{
        int id = new Random().nextInt(1000);
        mockMvc.perform(delete("/item/" + id)).andExpect(status().isNotFound());
    }


    private List<Item> itemFactory(String... names) {
        Random random = new Random();
        List<Item> list = new ArrayList<>();
        for (String name : names) {
            Item item = new Item();
            item.setNameRus(name);
            item.setNameEng(name);
            item.setModified(LocalDate.now());
            item.setCreated(LocalDate.now());
            item.setPartno(String.valueOf(random.nextInt(1000000)));
            item.setMargin(random.nextInt(99));
            item.setBrand(String.valueOf(random.nextInt(100000)));
            item.setBuyingPrice(Money.of(random.nextInt(10000), "USD"));
            item.setSellingPrice(Money.of(random.nextInt(10000), "USD"));
            item.setOverridden(random.nextBoolean());
            list.add(item);
        }
        return list;
    }

    public String toJsonString(Item obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
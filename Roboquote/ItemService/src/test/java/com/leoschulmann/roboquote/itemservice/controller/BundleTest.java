package com.leoschulmann.roboquote.itemservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.leoschulmann.roboquote.itemservice.config.TestJpaConfig;
import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.PostitionDto;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.exceptions.ExceptionProcessor;
import com.leoschulmann.roboquote.itemservice.repositories.BundleRepository;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import com.leoschulmann.roboquote.itemservice.services.BundleService;
import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
@Transactional
public class BundleTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BundleRepository bundleRepository;
    private BundleController bundleController;
    private BundleService bundleService;

    private MockMvc mockMvc;
    private ObjectMapper om;

    @Before
    public void prepare() {
        bundleService = new BundleService(bundleRepository, itemRepository);
        bundleController = new BundleController(bundleService);
        mockMvc = MockMvcBuilders.standaloneSetup(bundleController).setControllerAdvice(new ExceptionProcessor()).build();

        om = new ObjectMapper();

        List<Item> items = itemFactory("coffee", "beer", "cocoa", "cola", "juice");
        itemRepository.saveAll(items);
    }

    @Test
    public void testSaveNew() throws Exception {
        BundleDto requestDto = new BundleDto("test", List.of(
                new PostitionDto(1, 1), new PostitionDto(2, 15)));
        String json = om.writeValueAsString(requestDto);
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.post("/bundle").contentType("application/json")
                .content(json)).andDo(MockMvcResultHandlers.print()).andReturn();

        BundleDto resDto = om.readValue(res.getResponse().getContentAsString(StandardCharsets.UTF_8), BundleDto.class);

        assertEquals(resDto.getName(), requestDto.getName());
        assertEquals("coffee", resDto.getItems().get(0).getName());
        assertEquals(15, resDto.getItems().get(1).getQty());
    }

    @Test
    public void getAllTest() throws Exception {
        BundleDto dto1 = new BundleDto("bundle1", List.of(new PostitionDto(4, 2)));
        BundleDto dto2 = new BundleDto("bundle2", List.of(new PostitionDto(5, 10)));
        BundleDto dto3 = new BundleDto("bundle3", List.of(new PostitionDto(2, 1)));

        bundleService.addNewBundle(dto1);
        bundleService.addNewBundle(dto2);
        bundleService.addNewBundle(dto3);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/bundle"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andReturn();
        CollectionType type = om.getTypeFactory().constructCollectionType(List.class, BundleDto.class);
        List<BundleDto> dtos = om.readValue(res.getResponse().getContentAsString(StandardCharsets.UTF_8), type);

        assertEquals(3, dtos.size());
        assertEquals("bundle3", dtos.get(2).getName());
    }

    @Test
    public void PutTest() throws Exception {

        List<PostitionDto> list = List.of(new PostitionDto(2, 2), new PostitionDto(3, 3));
        BundleDto initialDto = new BundleDto();
        initialDto.setName("test bundle");
        initialDto.setItems(list);
        int id = (bundleService.addNewBundle(initialDto)).getId();

        List<PostitionDto> newItems = List.of(new PostitionDto(4, 10), new PostitionDto(5, 3));
        BundleDto modifiedDto = new BundleDto();
        modifiedDto.setName("modified test bundle");
        modifiedDto.setItems(newItems);

        String json = om.writeValueAsString(modifiedDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/bundle/" + id)
                .contentType("application/json").content(json)).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/bundle/" + id))
                .andExpect(status().isOk()).andReturn();

        BundleDto resDto = om.readValue(res.getResponse().getContentAsString(StandardCharsets.UTF_8), BundleDto.class);

        assertEquals("modified test bundle", resDto.getName());
        assertEquals(2, resDto.getItems().size());
        assertEquals("cola", resDto.getItems().get(0).getName());
        assertEquals(10, resDto.getItems().get(0).getQty());
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
}

package com.leoschulmann.roboquote.quoteservice.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoschulmann.roboquote.quoteservice.dto.ItemPositionDto;
import com.leoschulmann.roboquote.quoteservice.dto.QuoteDto;
import com.leoschulmann.roboquote.quoteservice.dto.QuoteSectionDto;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.leoschulmann.roboquote.quoteservice.repositories.QuoteRepo;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NewQuoteControllerTest {

    @Autowired
    QuoteRepo quoteRepo;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @BeforeAll
    void prepare() {
        Quote quote1 = new Quote("001", 1, "customer", "", "dealer", "", "", "", "", "", 20, 0,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
        quote1.setFinalPrice(Money.of(1, "EUR"));
        Quote quote2 = new Quote("002", 1, "customer", "", "dealer", "", "", "", "", "", 20, 0,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
        quote2.setFinalPrice(Money.of(100, "USD"));

        quoteRepo.save(quote1);
        quoteRepo.save(quote2);

        Quote q = new Quote("999", 1, "customer", "", "dealer", "", "", "", "", "", 20, 0,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
        q.setFinalPrice(Money.of(100, "USD"));
        q.setValidThru(LocalDate.now());

        QuoteSection qs = new QuoteSection("xyz");
        QuoteSection qs2 = new QuoteSection("abc");
        ItemPosition ip = new ItemPosition("name", "000", Money.of(5, "USD"), 2, 99);
        ItemPosition ip99 = new ItemPosition("name99", "000", Money.of(5, "USD"), 2, 99);
        qs.addItemPositions(ip, ip99);
        ItemPosition ip2 = new ItemPosition("name2", "000", Money.of(5, "USD"), 2, 99);
        ItemPosition ip3 = new ItemPosition("name3", "000", Money.of(5, "USD"), 2, 99);
        qs2.addItemPositions(ip2, ip3);
        q.addSections(qs, qs2);
        quoteRepo.save(q);
    }

    @Test
    @WithMockUser
    void testGetAll() throws Exception {
        MvcResult res = mockMvc.perform(get("/new")).andExpect(status().isOk()).andReturn();
        String json = res.getResponse().getContentAsString();

        List<QuoteDto> dtos = Arrays.asList(om.readValue(json, QuoteDto[].class));

        assertEquals(2, dtos.size());
        assertEquals("001", dtos.get(0).getNumber());
        assertEquals("USD", dtos.get(1).getFinalPriceCurrency());
    }

    @Test
    @WithMockUser
    void testGetOne() throws Exception {
        MvcResult res = mockMvc.perform(get("/new/3")).andExpect(status().isOk()).andReturn();
        String json = res.getResponse().getContentAsString();

        QuoteDto dto = om.readValue(json, QuoteDto.class);

        assertEquals("999", dto.getNumber());
        assertEquals(2, dto.getSections().size());
        assertEquals(2, dto.getSections().get(0).getPositions().size());
        assertEquals("abc", dto.getSections().get(1).getName());
    }

    @Test
    @WithMockUser
    void testSave() throws Exception {
        ItemPositionDto ipd = new ItemPositionDto(-1, "item", "000", 100., "USD", 5, 42);
        QuoteSectionDto qsd = new QuoteSectionDto(-1, new ArrayList<>(), "section", 5, 42., "JPY");
        QuoteDto qd = new QuoteDto(-1, "123", "2021-10-10", "2021-10-10", 1, "", new ArrayList<QuoteSectionDto>(),
                3, "", "", "", "", "", "", "", 20, 1., 1., 1., 1., 500., "RUB", "comment");
        qsd.getPositions().add(ipd);
        qd.getSections().add(qsd);

        String json = om.writeValueAsString(qd);

        String id = mockMvc.perform(post("/new").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(json))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String string = mockMvc.perform(get("/new/" + id)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        QuoteDto returned = om.readValue(string, QuoteDto.class);
        assertEquals(qd.getNumber(), returned.getNumber());
        assertEquals(qd.getSections().get(0).getName(), returned.getSections().get(0).getName());
        assertEquals(qd.getSections().get(0).getPositions().get(0).getName(), returned.getSections().get(0).getPositions().get(0).getName());
    }
}
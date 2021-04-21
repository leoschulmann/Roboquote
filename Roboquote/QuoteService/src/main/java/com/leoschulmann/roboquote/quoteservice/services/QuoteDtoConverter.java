package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.dto.ItemPositionDto;
import com.leoschulmann.roboquote.quoteservice.dto.QuoteDto;
import com.leoschulmann.roboquote.quoteservice.dto.QuoteSectionDto;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.leoschulmann.roboquote.quoteservice.entities.projections.QuoteWithoutSections;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Service
public class QuoteDtoConverter {
    public QuoteDto convertProjectionToDto(QuoteWithoutSections proj) {
        double priceAmount = proj.getFinalPrice().getNumberStripped().doubleValue();
        String priceCurrency = proj.getFinalPrice().getCurrency().getCurrencyCode();
        String date = getCreatedTimeAsString(proj.getCreatedDate(), proj.getCreatedDateTime());
        return new QuoteDto(proj.getId(), proj.getSerialNumber(), date, proj.getVersion(), proj.getCustomer(),
                proj.getDealer(), priceAmount, priceCurrency, proj.getComment(),
                Objects.requireNonNullElse(proj.getCancelled(), false));
    }

    public QuoteDto convertQuoteToDto(Quote q) {
        String created = getCreatedTimeAsString(q.getCreated(), q.getCreatedTimestamp());
        String validThru = q.getValidThru().format(ISO_DATE);
        double finalPriceAmount = q.getFinalPrice().getNumberStripped().doubleValue();
        String finalPriceCurrency = q.getFinalPrice().getCurrency().getCurrencyCode();

        List<QuoteSectionDto> sectionDtos =
                q.getSections().stream().map(this::convertSectionToDto).collect(Collectors.toList());

        return new QuoteDto(q.getId(), q.getNumber(), created, validThru, q.getVersion(), q.getCustomer(),
                sectionDtos, q.getDiscount(), q.getDealer(), q.getCustomerInfo(), q.getDealerInfo(), q.getPaymentTerms(),
                q.getShippingTerms(), q.getWarranty(), q.getInstallation(), q.getVat(), q.getEurRate().doubleValue(),
                q.getUsdRate().doubleValue(), q.getJpyRate().doubleValue(), q.getConversionRate().doubleValue(),
                finalPriceAmount, finalPriceCurrency, q.getComment(), Objects.requireNonNullElse(q.getCancelled(), false));

    }

    public QuoteSectionDto convertSectionToDto(QuoteSection qs) {
        List<ItemPositionDto> positionDtos =
                qs.getPositions().stream().map(this::convertPositionToDto).collect(Collectors.toList());
        double amount = qs.getTotal().getNumberStripped().doubleValue();
        String currency = qs.getTotal().getCurrency().getCurrencyCode();

        return new QuoteSectionDto(qs.getId(), positionDtos, qs.getName(), qs.getDiscount(),
                amount, currency);
    }

    public ItemPositionDto convertPositionToDto(ItemPosition ip) {
        double amount = ip.getSellingPrice().getNumberStripped().doubleValue();
        String currency = ip.getSellingPrice().getCurrency().getCurrencyCode();

        return new ItemPositionDto(ip.getId(), ip.getName(), ip.getPartNo(), amount, currency,
                ip.getQty(), ip.getItemId());
    }

    public Quote convertDtoToQuote(QuoteDto dto) {
        LocalDateTime createdDateTime = LocalDateTime.parse(dto.getCreated(), ISO_LOCAL_DATE_TIME);
        LocalDate validThr = LocalDate.parse(dto.getValidThru(), ISO_DATE);
        int id = Objects.requireNonNullElse(dto.getId(), 0);
        Quote q = new Quote(id, dto.getNumber(), createdDateTime, validThr, dto.getVersion(), dto.getCustomer(),
                new ArrayList<>(), dto.getDiscount(), dto.getDealer(), dto.getCustomerInfo(),
                dto.getDealerInfo(), dto.getPaymentTerms(), dto.getShippingTerms(), dto.getWarranty(),
                dto.getInstallation(), dto.getVat(), BigDecimal.valueOf(dto.getEurRate()),
                BigDecimal.valueOf(dto.getUsdRate()), BigDecimal.valueOf(dto.getJpyRate()),
                BigDecimal.valueOf(dto.getConversionRate()),
                Money.of(dto.getFinalPriceAmount(), dto.getFinalPriceCurrency()), dto.getComment(), dto.isCancelled());
        q.addSections(dto.getSections().stream().map(this::convertDtoToSection).toArray(QuoteSection[]::new));
        return q;
    }

    public QuoteSection convertDtoToSection(QuoteSectionDto dto) {
        QuoteSection qs = new QuoteSection(new ArrayList<>(), dto.getName(), dto.getDiscount(),
                Money.of(dto.getTotalAmount(), dto.getTotalCurrency()));
        qs.addItemPositions(dto.getPositions().stream().map(this::convertDtoToItemPosition).toArray(ItemPosition[]::new));
        return qs;
    }

    public ItemPosition convertDtoToItemPosition(ItemPositionDto dto) {
        return new ItemPosition(dto.getName(), dto.getPartNo(),
                Money.of(dto.getSellingAmount(), dto.getSellingCurrency()), dto.getQty(), dto.getItemId());
    }

    public Quote convertDtoToMinimalQuote(QuoteDto dto) {
        LocalDateTime createdDateTime = LocalDateTime.parse(dto.getCreated(), ISO_LOCAL_DATE_TIME);
        return new Quote(dto.getId(), dto.getNumber(), createdDateTime, dto.getVersion(), dto.getDealer(), dto.getCustomer(),
                Money.of(dto.getFinalPriceAmount(), dto.getFinalPriceCurrency()), dto.getComment(), dto.isCancelled());
    }

    private String getCreatedTimeAsString(LocalDate date, LocalDateTime dateTime) {
        LocalDateTime ldt = dateTime == null ? date.atStartOfDay() : dateTime;
        return ldt.format(ISO_LOCAL_DATE_TIME);
    }
}

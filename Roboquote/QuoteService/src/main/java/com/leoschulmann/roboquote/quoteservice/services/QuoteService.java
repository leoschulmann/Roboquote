package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.dto.DistinctTermsDto;
import com.leoschulmann.roboquote.quoteservice.dto.QuoteDto;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.projections.*;
import com.leoschulmann.roboquote.quoteservice.repositories.QuoteRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteService {
    private final QuoteRepo quoteRepo;
    private final QuoteDtoConverter dtoService;

    public List<QuoteDto> findAll() {
        List<QuoteWithoutSections> projections = quoteRepo.getAllQuoteProjections();
        return projections.stream().map(dtoService::convertProjectionToDto).collect(Collectors.toList());
    }

    public List<QuoteDto> findAllUncancelled() {
        List<QuoteWithoutSections> projections = quoteRepo.getAllUncancelledQuoteProjections();
        return projections.stream().map(dtoService::convertProjectionToDto).collect(Collectors.toList());
    }

    public QuoteDto getById(int id) {
        Quote quote = getQuote(id);
        return dtoService.convertQuoteToDto(quote);
    }

    public Quote getQuote(int id) {
        return quoteRepo.findDistinctById(id).get();
    }

    public Integer saveQuote(QuoteDto quoteDto) {
        return quoteRepo.save(dtoService.convertDtoToQuote(quoteDto)).getId();
    }

    public String getQuoteFullName(int id) {
        QuoteSerialAndVersion projection = quoteRepo.getSerialAndVersionForId(id);
        return projection.getSerialNumber() + "-" + projection.getVersion();
    }

    public void addComment(int id, String comment) {
        quoteRepo.addComment(id, comment);
    }

    public void setQuoteCancelled(int id, boolean action) {
        quoteRepo.setCancelled(id, action);
    }

    public DistinctTermsDto getDistinctQuoteTerms() {

        List<String> installations = quoteRepo.getDistinctInstallations().stream()
                .filter(Objects::nonNull).map(InstallationProjection::getInstallation)
                .filter(i -> !i.isBlank())
                .collect(Collectors.toList());

        List<String> payments = quoteRepo.getDistinctPayments().stream()
                .filter(Objects::nonNull).map(PaymentProjection::getPayment)
                .filter(i -> !i.isBlank())
                .collect(Collectors.toList());

        List<String> shippings = quoteRepo.getDistinctShipping().stream()
                .filter(Objects::nonNull).map(ShippingProjection::getShipping)
                .filter(i -> !i.isBlank())
                .collect(Collectors.toList());

        List<String> warranties = quoteRepo.getDistinctWarranties().stream()
                .filter(Objects::nonNull).map(WarrantyProjection::getWarranty)
                .filter(i -> !i.isBlank())
                .collect(Collectors.toList());

        return new DistinctTermsDto(installations, payments, shippings, warranties);
    }

    public List<QuoteDto> findAllForItemId(int id) {
        List<QuoteWithoutSections> projections = quoteRepo.getAllQuoteProjectionsForItemId(id);
        return projections.stream().map(dtoService::convertProjectionToDto).collect(Collectors.toList());
    }
}

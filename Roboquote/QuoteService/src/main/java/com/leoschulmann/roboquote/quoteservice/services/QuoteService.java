package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.dto.QuoteDto;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.projections.QuoteSerialAndVersion;
import com.leoschulmann.roboquote.quoteservice.entities.projections.QuoteWithoutSections;
import com.leoschulmann.roboquote.quoteservice.repositories.QuoteRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
}

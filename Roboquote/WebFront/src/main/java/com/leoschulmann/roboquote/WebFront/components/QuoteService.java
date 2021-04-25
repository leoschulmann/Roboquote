package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final HttpRestService httpRestService;

    public Quote createNewVersion(Quote source) {
        //everything except ipos's, version and dates
        Quote quote = new Quote(source.getNumber(), null, source.getCustomer(), source.getCustomerInfo(),
                source.getDealer(), source.getDealerInfo(), source.getPaymentTerms(), source.getShippingTerms(),
                source.getWarranty(), source.getInstallation(), source.getVat(), source.getDiscount(),
                source.getEurRate(), source.getUsdRate(), source.getJpyRate(), source.getConversionRate());

        getSectionsCopy(source, quote);
        return quote;
    }

    public Quote createNewFromTemplate(Quote source) {
        Quote quote = new Quote(null, null, null, null, null, null, source.getPaymentTerms(), source.getShippingTerms(),
                source.getWarranty(), source.getInstallation(), source.getVat(), source.getDiscount(),
                source.getEurRate(), source.getUsdRate(), source.getJpyRate(), source.getConversionRate());

        getSectionsCopy(source, quote);
        return quote;
    }

    private void getSectionsCopy(Quote source, Quote quote) {
        for (QuoteSection sourceSection : source.getSections()) {
            QuoteSection section = new QuoteSection(sourceSection.getName());
            section.setDiscount(sourceSection.getDiscount());
            List<ItemPosition> itemPositions = httpRestService.batchCopyPositions(sourceSection.getPositions());
            section.setPositions(itemPositions);
            quote.addSections(section);
        }
    }

}

package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuoteSectionHandlerSimpleImpl implements QuoteSectionHandler {
    @Override
    public void putToSection(QuoteSection section, ItemPosition ip) {
        Optional<ItemPosition> optional = section.getPositions().stream()
                .filter(pos -> pos.getItemId().equals(ip.getItemId()))
                .findAny();

        if (optional.isPresent()) {
            optional.get().incrementQty();
        } else section.getPositions().add(ip);
    }

    @Override
    public void deletePosition(QuoteSection quoteSection, ItemPosition itemPosition) {
        getOptionalItemPosition(quoteSection, itemPosition).ifPresent(p -> quoteSection.getPositions().remove(p));
    }

    @Override
    public void setQty(QuoteSection quoteSection, ItemPosition itemPosition, Integer value) {
        getOptionalItemPosition(quoteSection, itemPosition).ifPresent(p -> {
            p.setQty(value);
            p.setSellingSum(p.getSellingPrice().multiply(value));
        });
    }

    private Optional<ItemPosition> getOptionalItemPosition(QuoteSection quoteSection, ItemPosition itemPosition) {
        return quoteSection.getPositions().stream().filter(ip -> ip == itemPosition).findFirst();
    }
}

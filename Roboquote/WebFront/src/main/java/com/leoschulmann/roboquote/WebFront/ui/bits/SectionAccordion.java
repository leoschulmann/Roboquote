package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.components.StringFormattingService;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;

@Getter
public class SectionAccordion extends Accordion {
    private final QuoteSection quoteSection;
    private final SectionGrid grid;
    private final SectionButtons control;


    public SectionAccordion(QuoteSection quoteSection, StringFormattingService stringFormattingService) {
        this.quoteSection = quoteSection;

        setWidthFull();
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        grid = new SectionGrid(quoteSection, stringFormattingService);
        control = new SectionButtons(grid, quoteSection.getDiscount());
//
//        grid.addListener(ComposeDeleteItemPositionEvent.class, main::itemPositionDeleted);
//        grid.addListener(ComposeItemPositionQuantityEvent.class, main::itemPositionQuantityChanged);
//        main.addListener(UniversalSectionChangedEvent.class, grid::sectionChangedEvent);
        content.add(control, grid, grid.getFooter());

        add(quoteSection.getName(), content);
    }

    public void refreshName() {
        getOpenedPanel().ifPresent(panel -> panel.setSummary(new Span(quoteSection.getName())));
        grid.getFooter().update();
    }
}

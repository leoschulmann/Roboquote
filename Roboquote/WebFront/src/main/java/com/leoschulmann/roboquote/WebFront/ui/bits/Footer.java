package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.components.StringFormattingService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import java.math.BigDecimal;

public class Footer extends VerticalLayout {
    private final Span total;
    private final Span totalDiscounted;

    Footer(SectionGrid grid, StringFormattingService stringFormattingService) {
        this.grid = grid;
        this.stringFormattingService = stringFormattingService;

        total = new Span();
        total.getElement().getStyle()
                .set("margin-left", "auto");
        totalDiscounted = new Span();
        totalDiscounted.getElement().getStyle()
                .set("margin-left", "auto")
                .set("font-weight", "bold");
        add(total, totalDiscounted);
    }

    private final SectionGrid grid;
    private StringFormattingService stringFormattingService;

    void update() {

        BigDecimal disc = grid.getQuoteSection().getDiscount();
        total.setText(stringFormattingService.getSubtotal(grid.getName(), grid.getQuoteSection().getTotal()));
        totalDiscounted.setText(stringFormattingService.getSubtotalDisc(grid.getName(),
                grid.getQuoteSection().getTotal(), disc));

        if (!disc.equals(BigDecimal.ZERO)) {
            totalDiscounted.setVisible(true);
            total.getElement().getStyle().remove("font-weight").set("text-decoration", "line-through");
        } else {
            totalDiscounted.setVisible(false);
            total.getElement().getStyle().set("font-weight", "bold").remove("text-decoration");
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.components.StringFormattingService;
import com.leoschulmann.roboquote.WebFront.events.OverridePriceClicked;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.math.BigDecimal;

public class Footer extends VerticalLayout {
    private final Span total;
    private final Span totalDiscounted;

    @Getter
    private final Icon overridePrice;

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

        overridePrice = VaadinIcon.MAGIC.create();
        overridePrice.setSize("1em");
        overridePrice.setColor("purple");

        overridePrice.addClickListener(c -> fireEvent(new OverridePriceClicked(grid)));
    }

    private final SectionGrid grid;
    private StringFormattingService stringFormattingService;

    void update() {

        BigDecimal disc = grid.getQuoteSection().getDiscount();
        total.setText(stringFormattingService.getSubtotal(grid.getName(), grid.getQuoteSection().getTotal()) + " ");
        totalDiscounted.setText(stringFormattingService.getSubtotalDisc(grid.getName(),
                grid.getQuoteSection().getTotal(), disc) + " ");

        if (!disc.equals(BigDecimal.ZERO)) {
            totalDiscounted.setVisible(true);
            total.getElement().getStyle().remove("font-weight").set("text-decoration", "line-through");
            totalDiscounted.add(overridePrice);
        } else {
            totalDiscounted.setVisible(false);
            total.getElement().getStyle().set("font-weight", "bold").remove("text-decoration");
            total.add(overridePrice);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

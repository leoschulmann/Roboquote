package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.events.*;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.vaadin.flow.component.grid.GridVariant.*;

public class SectionGrid extends Grid<ItemPosition> {
    private final QuoteSection quoteSection;
    private final Footer footer;
    private IntegerField field;
    private List<HasEnabled> clickables = new ArrayList<>();

    SectionGrid(QuoteSection qs, CurrencyFormatService currencyFormatter) {
        super(ItemPosition.class);
        this.quoteSection = qs;
        setItems(quoteSection.getPositions());

        removeAllColumns();
        addThemeVariants(LUMO_ROW_STRIPES, LUMO_WRAP_CELL_CONTENT, LUMO_COLUMN_BORDERS);
        addColumn(ItemPosition::getName).setHeader("Item name").setSortable(false).setFlexGrow(1);
        addComponentColumn(this::getQuantityField).setHeader("Quantity").setSortable(false).setAutoWidth(true).setFlexGrow(0);
        addColumn(ItemPosition::getPartNo).setHeader("Part No").setSortable(false).setWidth("8em").setFlexGrow(0);
        addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingPrice())).setHeader("Price").setWidth("8em").setFlexGrow(0);
        addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingSum())).setHeader("Sum").setWidth("8em").setFlexGrow(0);
        addComponentColumn(this::createDeleteButton).setSortable(false).setAutoWidth(true).setFlexGrow(0);
        setHeightByRows(true);
        footer = new Footer(this, currencyFormatter);
    }

    private Component getQuantityField(ItemPosition itemPosition) {
        field = new IntegerField();
        field.setMax(99);
        field.setMin(1);
        field.setHasControls(true);
        field.setValue(itemPosition.getQty());
        field.addValueChangeListener(event -> {
            fireEvent(new ComposeItemPositionQuantityEvent(this, itemPosition, event.getValue()));
            gridResetItems();
            redrawFooter();
        });
        clickables.add(field);
        return field;
    }

    private Component createDeleteButton(ItemPosition itemPosition) {
        Button deleteItemPositionBtn = new Button(VaadinIcon.CLOSE_SMALL.create());

        deleteItemPositionBtn.addClickListener(c -> {
            fireEvent(new ComposeDeleteItemPositionEvent(this, itemPosition));
            gridResetItems();
            redrawFooter();
        });
        clickables.add(deleteItemPositionBtn);
        return deleteItemPositionBtn;
    }

    public void disableClickables() {
        clickables.stream().filter(Objects::nonNull).forEach(c -> c.setEnabled(false));
    }

    private void gridResetItems() {
        setItems(quoteSection.getPositions());
    }

    public QuoteSection getQuoteSection() {
        return quoteSection;
    }

    @Override
    public String toString() {
        return quoteSection.getName();
    }

    String getName() {
        return quoteSection.getName();
    }

    Footer getFooter() {
        return footer;
    }

    private void redrawFooter() {
        footer.update();
    }

    protected <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    void sectionChangedEvent(UniversalSectionChangedEvent event) {
        gridResetItems();
        redrawFooter();
    }
}

class Footer extends Div {
    private final Paragraph total;
    private final Paragraph totalDiscounted;
    private final CurrencyFormatService currencyFormatter;

    Footer(SectionGrid grid, CurrencyFormatService currencyFormatter) {
        this.currencyFormatter = currencyFormatter;
        this.grid = grid;
        total = new Paragraph();
        totalDiscounted = new Paragraph();
        add(total, totalDiscounted);
    }

    private final SectionGrid grid;

    void update() {
        total.setText("Subotal " + grid.getName() + " " + currencyFormatter.formatMoney(grid.getQuoteSection().getTotal()));
        totalDiscounted.setText(grid.getQuoteSection().getDiscount() <= 0 ? "" :
                "Subtotal " + grid.getName() + " (disc. " + grid.getQuoteSection().getDiscount() + "%) " +
                        currencyFormatter.formatMoney(grid.getQuoteSection().getTotalDiscounted()));
    }
}

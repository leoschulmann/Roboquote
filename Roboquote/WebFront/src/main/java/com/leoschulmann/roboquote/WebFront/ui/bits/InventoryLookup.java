package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.events.DisableClickableComponents;
import com.leoschulmann.roboquote.WebFront.events.InventoryLookupAddClickedEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryLookupRefreshButtonEvent;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.format.AmountFormatParams;
import org.javamoney.moneta.format.CurrencyStyle;

import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.List;
import java.util.Locale;

@Getter
public class InventoryLookup extends HorizontalLayout {
    private final ComboBox<Item> searchBox;
    private final ComboBox<SectionGrid> grids;
    private final Button addToGridBtn;
    private final Button refreshItems;
    private List<SectionGrid> sectionGrids;

    @Setter
    private List<Item> items;

    private static final MonetaryAmountFormat FORMATTER;

    static {
        FORMATTER = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder
                        .of(Locale.FRANCE)
                        .set(CurrencyStyle.SYMBOL)
                        .set(AmountFormatParams.PATTERN, "#,###,###.## ¤")
                        .build());
    }


    public InventoryLookup(List<SectionGrid> sectionGrids, List<Item> items) {
        addListener(DisableClickableComponents.class, this::disable);

        this.sectionGrids = sectionGrids;
        this.items = items;
        setWidthFull();

        searchBox = getItemComboBox(items);
        grids = new ComboBox<>();
        grids.setItems(sectionGrids);
        addToGridBtn = new Button("ADD");
        refreshItems = new Button(VaadinIcon.REFRESH.create());
        refreshItems.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        addToGridBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addToGridBtn.addClickListener(click -> fireEvent(
                new InventoryLookupAddClickedEvent(this, searchBox.getValue(), grids.getValue())));
        searchBox.setWidthFull();
        searchBox.setPlaceholder("Inventory lookup");
        grids.setWidthFull();
        grids.setPlaceholder("Quote section");
        addToGridBtn.setWidth("15%");
        refreshItems.addClickListener(click -> fireEvent(new InventoryLookupRefreshButtonEvent(this)));
        add(refreshItems, searchBox, grids, addToGridBtn);
    }

    private ComboBox<Item> getItemComboBox(List<Item> items) {
        ComboBox<Item> combo = new ComboBox<>();

        ComboBox.ItemFilter<Item> filter = (ComboBox.ItemFilter<Item>)
                (element, filterString) -> element.getNameRus().toLowerCase().contains(filterString.toLowerCase()) ||
                        element.getNameEng().toLowerCase().contains(filterString.toLowerCase()) ||
                        element.getPartno().toLowerCase().contains(filterString.toLowerCase());

        combo.setItems(filter, items);
        combo.setItemLabelGenerator((ItemLabelGenerator<Item>) item ->
                FORMATTER.format(item.getSellingPrice()) + " (" + item.getPartno() + ") "
                        + item.getNameRus().substring(0, Math.min(item.getNameRus().length(), 35)) + " / "
                        + item.getNameEng().substring(0, Math.min(item.getNameEng().length(), 35))
        );

        combo.setClearButtonVisible(true);
        return combo;
    }


    public void disable(DisableClickableComponents e) {
        setEnabled(false);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public void updateGrids(List<SectionGrid> gridsAsList) {
        this.sectionGrids = gridsAsList;
        grids.setItems(sectionGrids);
    }
}

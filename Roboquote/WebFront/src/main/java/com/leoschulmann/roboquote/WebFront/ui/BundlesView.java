package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.BundleService;
import com.leoschulmann.roboquote.WebFront.components.ItemCachingService;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Route(value = "bundles", layout = MainLayout.class)
public class BundlesView extends VerticalLayout {
    private final TextField nameField;
    private final Grid<Item> availableItemsGrid;
    private Button addBtn;
    private Button saveBundle;
    private final ListDataProvider<Item> availableItemsDataProvider;
    private ListDataProvider<BundledPosition> selectedItemsDataProvider;
    private final Bundle bundle;
    private final BundleService bundleService;


    public BundlesView(ItemCachingService cachingService, BundleService bundleService) {
        bundle = new Bundle();
        availableItemsDataProvider = new ListDataProvider<>(cachingService.getItemsFromCache());
        this.bundleService = bundleService;
        availableItemsGrid = createAvailableGrid();
        Grid<BundledPosition> bundleItemsGrid = createSelectedGrid();
        addBtn = createAddButton();
        saveBundle = createSaveBundle();

        nameField = new TextField("Bundle name");
        nameField.setWidthFull();

        add(new HorizontalLayout(nameField, saveBundle));
        add(new HorizontalLayout(availableItemsGrid, addBtn, bundleItemsGrid));
    }

    private Button createSaveBundle() {
        saveBundle = new Button("SAVE");
        saveBundle.addClickListener(c -> {
            bundle.setNameRus(nameField.getValue());
            bundle.setNameEng(nameField.getValue());
            bundleService.saveBundle(bundle);
        });
        return saveBundle;
    }

    private Button createAddButton() {
        addBtn = new Button(">>");
        addBtn.addClickListener(c -> {
            Item item = availableItemsGrid.asSingleSelect().getValue();
            Optional<BundledPosition> opt = selectedItemsContainsItem(item);

            if (opt.isPresent()) {
                BundledPosition bp = opt.get();
                bp.setQty(bp.getQty() + 1);
            } else {
                BundledPosition bp = bundleService.convertToBundlePostion(item);
                bundle.addPosition(bp);
            }
            selectedItemsDataProvider.refreshAll();
        });
        return addBtn;
    }

    private Grid<BundledPosition> createSelectedGrid() {
        final Grid<BundledPosition> bundleItemsGrid;
        bundleItemsGrid = new Grid<>(BundledPosition.class);
        selectedItemsDataProvider = new ListDataProvider<>(bundle.getPositions());
        bundleItemsGrid.setDataProvider(selectedItemsDataProvider);
        bundleItemsGrid.removeAllColumns();
        bundleItemsGrid.addColumn(bp -> bp.getItem().getNameRus());
        bundleItemsGrid.addColumn(BundledPosition::getQty);
        bundleItemsGrid.setWidth("30em");
        return bundleItemsGrid;
    }

    private Grid<Item> createAvailableGrid() {
        final Grid<Item> availableItemsGrid;
        availableItemsGrid = new Grid<>(Item.class);
        availableItemsGrid.removeAllColumns();
        Grid.Column<Item> col = availableItemsGrid.addColumn(Item::getNameRus);
        availableItemsGrid.setDataProvider(availableItemsDataProvider);
        availableItemsGrid.setHeightByRows(true);
        availableItemsGrid.setHeight("100em");
        availableItemsGrid.setWidth("30em");

        TextField filter = new TextField();
        filter.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        filter.setClearButtonVisible(true);
        filter.addValueChangeListener(event -> availableItemsDataProvider.addFilter(
                item -> StringUtils.containsIgnoreCase(item.getNameRus(), filter.getValue())));
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        col.setHeader(filter);
        return availableItemsGrid;
    }

    Optional<BundledPosition> selectedItemsContainsItem(Item item) {
        return bundle.getPositions().stream().filter(bp -> bp.getItem().equals(item)).findAny();
    }
}

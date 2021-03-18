package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.BundleService;
import com.leoschulmann.roboquote.WebFront.components.ItemCachingService;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
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
    private final Grid<BundledPosition> bundleItemsGrid;
    private Button addBtn;
    private Button wrapBtn;
    private Button saveBundleButton;
    private final ListDataProvider<Item> availableItemsDataProvider;
    private ListDataProvider<BundledPosition> selectedItemsDataProvider;
    private final Bundle bundle;
    private final BundleService bundleService;
    private final Span counter = new Span("Total: ");
    private boolean wrap = false;


    public BundlesView(ItemCachingService cachingService, BundleService bundleService) {
        bundle = new Bundle();
        availableItemsDataProvider = new ListDataProvider<>(cachingService.getItemsFromCache());
        this.bundleService = bundleService;
        availableItemsGrid = createAvailableGrid();
        bundleItemsGrid = createSelectedGrid();
        addBtn = createAddButton();
        wrapBtn = createWrapButton();
        VerticalLayout middleButtons = new VerticalLayout(addBtn, wrapBtn);
        middleButtons.setWidth("5%");
        nameField = new TextField("Bundle name");
        saveBundleButton = createSaveBundle();
        nameField.setWidth("90%");
        saveBundleButton.setWidth("10%");
        HorizontalLayout nameAndButton = new HorizontalLayout(nameField, saveBundleButton);
        nameAndButton.setWidthFull();
        nameAndButton.setAlignItems(Alignment.BASELINE);
        add(nameAndButton);
        HorizontalLayout mailPanel = new HorizontalLayout(availableItemsGrid, middleButtons, bundleItemsGrid);
        mailPanel.setWidthFull();
        mailPanel.setAlignItems(Alignment.CENTER);
        add(mailPanel);
    }

    private Button createWrapButton() {
        wrapBtn = new Button(VaadinIcon.LINES.create());
        wrapBtn.addClickListener(c -> {
            if (!wrap) {
                bundleItemsGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
                availableItemsGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
                wrap = true;
            } else {
                bundleItemsGrid.removeThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
                availableItemsGrid.removeThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
                wrap = false;
            }
        });
        return wrapBtn;
    }

    private Button createSaveBundle() {
        saveBundleButton = new Button("SAVE");
        saveBundleButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        saveBundleButton.addClickListener(c -> {
            bundle.setNameRus(nameField.getValue());
            bundle.setNameEng(nameField.getValue());
            bundleService.saveBundle(bundle);
        });
        return saveBundleButton;
    }

    private Grid<BundledPosition> createSelectedGrid() {
        final Grid<BundledPosition> bundleItemsGrid = new Grid<>(BundledPosition.class);
        bundleItemsGrid.getStyle().set("font-size", "12px");

        selectedItemsDataProvider = new ListDataProvider<>(bundle.getPositions());
        bundleItemsGrid.setDataProvider(selectedItemsDataProvider);
        bundleItemsGrid.removeAllColumns();

        Grid.Column<BundledPosition> namecol = bundleItemsGrid.addColumn(bp -> bp.getItem().getNameRus());
        Grid.Column<BundledPosition> qcol = bundleItemsGrid.addColumn(BundledPosition::getQty);
        namecol.setHeader("Item name").setFlexGrow(1).setResizable(true);
        qcol.setHeader("Qty").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER).setFooter(counter);
        bundleItemsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);

        return bundleItemsGrid;
    }

    private Grid<Item> createAvailableGrid() {
        final Grid<Item> availableItemsGrid = new Grid<>(Item.class);
        availableItemsGrid.getStyle().set("font-size", "12px");

        availableItemsGrid.removeAllColumns();
        Grid.Column<Item> col = availableItemsGrid.addColumn(Item::getNameRus);

        availableItemsGrid.setDataProvider(availableItemsDataProvider);

        TextField filter = new TextField();
        filter.setWidthFull();
        filter.setPlaceholder("filter text");
        filter.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        filter.setClearButtonVisible(true);
        filter.addValueChangeListener(event -> availableItemsDataProvider.addFilter(
                item -> StringUtils.containsIgnoreCase(item.getNameRus(), filter.getValue())));
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        col.setHeader(filter);
        return availableItemsGrid;
    }

    private Button createAddButton() {
        addBtn = new Button(VaadinIcon.FORWARD.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
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
            counter.setText("Total : " + countItems());
        });
        return addBtn;
    }

    private int countItems() {
        return bundle.getPositions().stream().mapToInt(BundledPosition::getQty).sum();
    }

    Optional<BundledPosition> selectedItemsContainsItem(Item item) {
        return bundle.getPositions().stream().filter(bp -> bp.getItem().equals(item)).findAny();
    }
}

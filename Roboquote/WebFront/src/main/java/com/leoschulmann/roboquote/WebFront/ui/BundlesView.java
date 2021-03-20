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
    private Button removeFromSelected;
    private Button removeLine;
    private final ListDataProvider<Item> availableItemsDataProvider;
    private ListDataProvider<BundledPosition> selectedItemsDataProvider;
    private final Bundle bundle;
    private final BundleService bundleService;
    private final Span counter = new Span("Total: ");
    private boolean wrap = false;


    public BundlesView(ItemCachingService cachingService, BundleService bundleService) {
        this.bundleService = bundleService;
        bundle = new Bundle();
        nameField = new TextField("Bundle name");
        saveBundleButton = createSaveBundle();
        nameField.setWidth("90%");
        saveBundleButton.setWidth("10%");
        HorizontalLayout nameAndButton = new HorizontalLayout(nameField, saveBundleButton);
        nameAndButton.setWidthFull();
        nameAndButton.setAlignItems(Alignment.BASELINE);
        add(nameAndButton);

        HorizontalLayout mailPanel = new HorizontalLayout();
        mailPanel.setWidthFull();
        mailPanel.setAlignItems(Alignment.START);
        availableItemsDataProvider = new ListDataProvider<>(cachingService.getItemsFromCache());
        availableItemsGrid = createAvailableGrid();
        bundleItemsGrid = createSelectedGrid();
        availableItemsGrid.setWidth("45%");
        bundleItemsGrid.setWidth("45%");
        addBtn = createAddButton();
        wrapBtn = createWrapButton();
        removeFromSelected = createRemoveButton();
        removeLine = createRemovLineButton();
        VerticalLayout panelButtons = new VerticalLayout(addBtn, removeFromSelected, removeLine, wrapBtn);
        panelButtons.setWidth("5%");
        mailPanel.add(availableItemsGrid, panelButtons, bundleItemsGrid);
        add(mailPanel);
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
        bundleItemsGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS);

        return bundleItemsGrid;
    }

    private Grid<Item> createAvailableGrid() {
        final Grid<Item> availableItemsGrid = new Grid<>(Item.class);
        availableItemsGrid.getStyle().set("font-size", "12px");
        availableItemsGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS);

        availableItemsGrid.removeAllColumns();
        Grid.Column<Item> nameCol = availableItemsGrid.addColumn(Item::getNameRus).setFlexGrow(1);
        availableItemsGrid.addColumn(Item::getSellingPrice).setFlexGrow(0);

        availableItemsGrid.setDataProvider(availableItemsDataProvider);

        TextField filter = new TextField();
        filter.setWidthFull();
        filter.setPlaceholder("filter text");
        filter.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        filter.setClearButtonVisible(true);
        filter.addValueChangeListener(event -> availableItemsDataProvider.addFilter(
                item -> StringUtils.containsIgnoreCase(item.getNameRus(), filter.getValue())));
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        nameCol.setHeader(filter);
        return availableItemsGrid;
    }

    private Button createAddButton() {
        addBtn = new Button(VaadinIcon.ANGLE_RIGHT.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        addBtn.addClickListener(c -> {
            Optional<Item> optItem = availableItemsGrid.asSingleSelect().getOptionalValue();
            if (optItem.isPresent()) {
                Optional<BundledPosition> optBP = selectedItemsContainsItem(optItem.get());
                if (optBP.isPresent()) {
                    BundledPosition bp = optBP.get();
                    bp.setQty(bp.getQty() + 1);
                } else {
                    BundledPosition bp = bundleService.convertToBundlePostion(optItem.get());
                    bundle.addPosition(bp);
                }
                selectedItemsDataProvider.refreshAll();
                counter.setText("Total : " + countItems());
            }
        });
        return addBtn;
    }

    private Button createWrapButton() {
        wrapBtn = new Button(VaadinIcon.LINES.create());
        wrapBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
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

    private Button createRemoveButton() {
        removeFromSelected = new Button(VaadinIcon.ANGLE_LEFT.create());
        removeFromSelected.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        removeFromSelected.addClickListener(c-> {
            BundledPosition bp = bundleItemsGrid.asSingleSelect().getValue();
            if (bp.getQty() > 1) {
                bp.setQty(bp.getQty() - 1);
            } else {
                bundle.removePosition(bp);
            }
            selectedItemsDataProvider.refreshAll();
            counter.setText("Total : " + countItems());
        });
        return removeFromSelected;
    }

    private Button createRemovLineButton() {
        removeLine = new Button(VaadinIcon.ANGLE_DOUBLE_LEFT.create());
        removeLine.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        removeLine.addClickListener(c-> {
            BundledPosition bp = bundleItemsGrid.asSingleSelect().getValue();
                bundle.removePosition(bp);
            selectedItemsDataProvider.refreshAll();
            counter.setText("Total : " + countItems());
        });
        return removeLine;
}

    private Button createSaveBundle() {
        saveBundleButton = new Button("Save");
        saveBundleButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        saveBundleButton.addClickListener(c -> {
            bundle.setNameRus(nameField.getValue());
            bundle.setNameEng(nameField.getValue());
            bundleService.saveBundle(bundle);
        });
        return saveBundleButton;
    }

    private int countItems() {
        return bundle.getPositions().stream().mapToInt(BundledPosition::getQty).sum();
    }

    Optional<BundledPosition> selectedItemsContainsItem(Item item) {
        return bundle.getPositions().stream().filter(bp -> bp.getItem().equals(item)).findAny();
    }
}

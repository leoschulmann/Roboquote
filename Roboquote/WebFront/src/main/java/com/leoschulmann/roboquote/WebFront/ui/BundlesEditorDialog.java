package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.BundleService;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.BundledPosition;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public class BundlesEditorDialog extends Dialog {
    private TextField nameField;
    private Grid<Item> availableItemsGrid;
    private Grid<BundledPosition> bundleItemsGrid;
    private Button addBtn;
    private Button wrapBtn;
    private Button saveBundleButton;
    private Button removeFromSelected;
    private Button removeLine;
    private Button deleteBundle;
    private ListDataProvider<Item> availableItemsDataProvider;
    private ListDataProvider<BundledPosition> selectedItemsDataProvider;
    private final Bundle bundle;
    private final BundleService bundleService;
    private final List<Item> itemsCache;
    private final boolean createNew;
    private final Span counter = new Span();
    private boolean wrap = false;
    private final BundlesView bundlesView;


    public BundlesEditorDialog(List<Item> itemscache, BundleService bundleService, Bundle bundle, BundlesView bundlesView) {
        this.itemsCache = itemscache;
        this.bundleService = bundleService;
        this.bundle = bundle;
        this.bundlesView = bundlesView;
        createNew = false;
        build();
    }

    public BundlesEditorDialog(List<Item> itemscache, BundleService bundleService, BundlesView bundlesView) {
        this.itemsCache = itemscache;
        this.bundleService = bundleService;
        this.bundle = new Bundle();
        this.bundlesView = bundlesView;
        createNew = true;
        build();
    }

    private void build() {
        HorizontalLayout nameAndButton = getNameAndButtonPanel();
        nameAndButton.setWidthFull();
        nameAndButton.setAlignItems(FlexComponent.Alignment.BASELINE);

        HorizontalLayout mailPanel = getMainPanelPanel();
        mailPanel.setWidthFull();
        mailPanel.setAlignItems(FlexComponent.Alignment.START);

        add(new VerticalLayout(nameAndButton, mailPanel));
    }

    private HorizontalLayout getMainPanelPanel() {
        HorizontalLayout mailPanel = new HorizontalLayout();
        availableItemsDataProvider = new ListDataProvider<>(itemsCache);
        availableItemsGrid = createAvailableGrid();
        bundleItemsGrid = createSelectedGrid();
        availableItemsGrid.setWidth("45%");
        bundleItemsGrid.setWidth("45%");
        addBtn = createAddButton();
        wrapBtn = createWrapButton();
        removeFromSelected = createRemoveButton();
        removeLine = createRemoveLineButton();
        deleteBundle = createDeleteBundleButton();
        VerticalLayout panelButtons = new VerticalLayout(addBtn, removeFromSelected, removeLine, wrapBtn, deleteBundle);
        panelButtons.setWidth("5%");
        mailPanel.add(availableItemsGrid, panelButtons, bundleItemsGrid);
        return mailPanel;
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

    private Grid<BundledPosition> createSelectedGrid() {
        final Grid<BundledPosition> bundleItemsGrid = new Grid<>(BundledPosition.class);
        bundleItemsGrid.getStyle().set("font-size", "12px");

        selectedItemsDataProvider = new ListDataProvider<>(bundle.getPositions());
        bundleItemsGrid.setDataProvider(selectedItemsDataProvider);
        bundleItemsGrid.removeAllColumns();

        Grid.Column<BundledPosition> namecol = bundleItemsGrid.addColumn(bp -> bp.getItem().getNameRus());
        Grid.Column<BundledPosition> qcol = bundleItemsGrid.addColumn(BundledPosition::getQty);
        namecol.setHeader("Item name").setFlexGrow(1).setResizable(true);
        qcol.setHeader("Qty").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER).setFooter(refreshCounter());
        bundleItemsGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS);

        return bundleItemsGrid;
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
                    BundledPosition bp = bundleService.convertToBundlePosition(optItem.get());
                    bundle.addPosition(bp);
                }
                selectedItemsDataProvider.refreshAll();
                refreshCounter();
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
        removeFromSelected.addClickListener(c -> {
            BundledPosition bp = bundleItemsGrid.asSingleSelect().getValue();
            if (bp.getQty() > 1) {
                bp.setQty(bp.getQty() - 1);
            } else {
                bundle.removePosition(bp);
            }
            selectedItemsDataProvider.refreshAll();
            refreshCounter();
        });
        return removeFromSelected;
    }

    private Button createRemoveLineButton() {
        removeLine = new Button(VaadinIcon.ANGLE_DOUBLE_LEFT.create());
        removeLine.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        removeLine.addClickListener(c -> {
            BundledPosition bp = bundleItemsGrid.asSingleSelect().getValue();
            bundle.removePosition(bp);
            selectedItemsDataProvider.refreshAll();
            refreshCounter();
        });
        return removeLine;
    }

    private Button createDeleteBundleButton() {
        deleteBundle = new Button(VaadinIcon.TRASH.create());
        if (createNew) deleteBundle.setEnabled(false);
        deleteBundle.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        deleteBundle.addClickListener(c -> {
            bundleService.deleteBundle(bundle.getId());
            bundlesView.updateList();
            this.close();
        });
        return deleteBundle;
    }

    private HorizontalLayout getNameAndButtonPanel() {
        nameField = getNameField();
        saveBundleButton = createSaveBundle();
        nameField.setWidth("90%");
        saveBundleButton.setWidth("10%");
        return new HorizontalLayout(nameField, saveBundleButton);
    }

    private TextField getNameField() {
        TextField nameField = new TextField("Bundle name");
        nameField.setValue(createNew ? "" : bundle.getNameRus()); //todo i8n violation
        return nameField;
    }

    private Button createSaveBundle() {
        saveBundleButton = new Button(createNew ? "Save" : "Update");
        saveBundleButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        saveBundleButton.addClickListener(c -> {
            if (createNew) {
                bundle.setNameRus(nameField.getValue());
                bundle.setNameEng(nameField.getValue());
                bundleService.saveBundle(bundle);
            } else {
                bundleService.updateBundle(bundle);
            }
            bundlesView.updateList();
            this.close();
        });
        return saveBundleButton;
    }

    private int countItems() {
        return bundle.getPositions().stream().mapToInt(BundledPosition::getQty).sum();
    }

    Optional<BundledPosition> selectedItemsContainsItem(Item item) {
        return bundle.getPositions().stream().filter(bp -> bp.getItem().getId() == item.getId()).findAny();
    }

    private Span refreshCounter() {
        counter.setText("Total : " + countItems());
        return counter;
    }
}

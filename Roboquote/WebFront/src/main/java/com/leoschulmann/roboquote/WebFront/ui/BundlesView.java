package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.BundleService;
import com.leoschulmann.roboquote.WebFront.components.ItemCachingService;
import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.leoschulmann.roboquote.itemservice.dto.BundleItemDto;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;

@Route(value = "bundles", layout = MainLayout.class)
public class BundlesView extends VerticalLayout {
    private final Grid<BundleDto> bundleGrid;
    private final BundleService bundleService;
    private final ItemCachingService itemCachingService;


    public BundlesView(BundleService bundleService, ItemCachingService itemCachingService) {
        this.bundleService = bundleService;
        this.itemCachingService = itemCachingService;
        Button createButton = getCreateButton();
        bundleGrid = getGrid();
        add(createButton, bundleGrid);
    }

    private Grid<BundleDto> getGrid() {
        Grid<BundleDto> bundleGrid = new Grid<>(BundleDto.class);
        bundleGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        bundleGrid.setDataProvider(new ListDataProvider<>(bundleService.getBundlesList()));
        bundleGrid.removeAllColumns();
        bundleGrid.addColumn(BundleDto::getName).setHeader("Name").setFlexGrow(1);
        bundleGrid.addComponentColumn(dto -> new Label(String.valueOf(
                dto.getItems().stream().mapToInt(BundleItemDto::getQty).sum()))).setHeader("# items")
                .setFlexGrow(0);
        bundleGrid.setWidth("65%");
        bundleGrid.addItemClickListener(c -> {
            Bundle bundle = bundleService.convertToBundle(c.getItem());
            BundlesEditorDialog edDial = new BundlesEditorDialog(itemCachingService.getItemsFromCache(), bundleService,
                    bundle, this);
            edDial.setWidth("85%");
            edDial.open();
        });
        return bundleGrid;
    }

    private Button getCreateButton() {
        final Button createButton = new Button("Create new bundle");
        createButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        createButton.setWidth("65%");
        createButton.addClickListener(c -> {
            BundlesEditorDialog editorDialog = new BundlesEditorDialog(itemCachingService.getItemsFromCache(),
                    bundleService, this);
            editorDialog.setWidth("85%");
            editorDialog.open();
        });
        return createButton;
    }

    public void updateList() {
        bundleGrid.setDataProvider(new ListDataProvider<>(bundleService.getBundlesList()));
    }
}

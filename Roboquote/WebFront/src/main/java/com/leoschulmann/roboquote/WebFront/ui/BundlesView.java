package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.ConverterService;
import com.leoschulmann.roboquote.WebFront.components.HttpRestService;
import com.leoschulmann.roboquote.WebFront.components.CachingService;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;

@Route(value = "bundles", layout = MainLayout.class)
public class BundlesView extends VerticalLayout {
    private final Grid<Bundle> bundleGrid;
    private final HttpRestService httpRestService;

    private final CachingService cachingService;
    private final ConverterService converterService;


    public BundlesView(HttpRestService httpRestService, CachingService cachingService, ConverterService converterService) {
        this.httpRestService = httpRestService;
        this.cachingService = cachingService;
        this.converterService = converterService;
        Button createButton = getCreateButton();
        bundleGrid = getGrid();
        add(createButton, bundleGrid);
    }

    private Grid<Bundle> getGrid() {
        Grid<Bundle> bundleGrid = new Grid<>(Bundle.class);
        bundleGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        bundleGrid.setDataProvider(new ListDataProvider<>(httpRestService.getAllBundlesNamesAndIds()));
        bundleGrid.removeAllColumns();
        bundleGrid.addColumn(Bundle::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        bundleGrid.addColumn(Bundle::getNameRus).setHeader("Name").setFlexGrow(1); //todo i8n
        bundleGrid.setWidth("65%");
        bundleGrid.addItemClickListener(c -> {
            int bunId = c.getItem().getId();
            Bundle bundle = httpRestService.getBundleById(bunId);
            BundlesEditorDialog edDial = new BundlesEditorDialog(cachingService.getItemsFromCache(), httpRestService,
                    converterService, bundle, this);
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
            BundlesEditorDialog editorDialog = new BundlesEditorDialog(cachingService.getItemsFromCache(),
                    httpRestService, converterService, this);
            editorDialog.setWidth("85%");
            editorDialog.open();
        });
        return createButton;
    }

    public void updateList() {
        bundleGrid.setDataProvider(new ListDataProvider<>(httpRestService.getAllBundlesNamesAndIds()));
    }
}

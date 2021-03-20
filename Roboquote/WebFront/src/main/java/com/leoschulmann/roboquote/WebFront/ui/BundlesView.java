package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.BundleService;
import com.leoschulmann.roboquote.WebFront.components.ItemCachingService;
import com.leoschulmann.roboquote.itemservice.dto.BundleDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;

@Route(value = "bundles", layout = MainLayout.class)
public class BundlesView extends VerticalLayout {
    private final BundlesEditor editor;
    private final Grid<BundleDto> bundleGrid;
    private final Button createButton;
    private ListDataProvider<BundleDto> listDataProvider;


    public BundlesView(BundleService bundleService, ItemCachingService itemCachingService) {
        editor = new BundlesEditor(itemCachingService, bundleService);
        listDataProvider = new ListDataProvider<>(bundleService.getBundlesList());
        createButton = getCreateButton();
        bundleGrid = getGrid();
        add(createButton, bundleGrid);
    }

    private Grid<BundleDto> getGrid() {
        Grid<BundleDto> bundleGrid = new Grid<>(BundleDto.class);
        bundleGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS);

        bundleGrid.setDataProvider(listDataProvider);
        bundleGrid.removeAllColumns();
        bundleGrid.addColumn(BundleDto::getName).setHeader("Name").setFlexGrow(1);
        bundleGrid.addComponentColumn(dto -> new Label(String.valueOf(dto.getItems().size()))).setHeader("# items")
                 .setFlexGrow(0);
        bundleGrid.setWidth("65%");
        return bundleGrid;
    }

    private Button getCreateButton() {
        final Button createButton;
        createButton = new Button(VaadinIcon.FILE_ADD.create());
        createButton.addClickListener(c -> {
            Dialog dialog = new Dialog(editor);
            dialog.setWidth("85%");
            dialog.open();
        });
        return createButton;
    }
}

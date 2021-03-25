package com.leoschulmann.roboquote.WebFront.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

@CssImport("./styles/styles.css")
public class MainLayout extends AppLayout {
    public MainLayout() {
        H1 logo = new H1("Roboquote");
        logo.addClassName("logo");
        Anchor logout = new Anchor("logout", "Log out");
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);
        header.expand(logo);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("header");
        addToNavbar(header);

        RouterLink linkToCompose = new RouterLink("Compose", Compose.class);
        RouterLink lingToInventory = new RouterLink("Inventory", InventoryView.class);
        RouterLink linkToQuotes = new RouterLink("Quotes", QuotesView.class);
        RouterLink linkToBundlesEditor = new RouterLink("Bundles", BundlesView.class);
        RouterLink linkToInvite = new RouterLink("Invite user", RegisterUser.class);
        linkToCompose.setHighlightCondition(HighlightConditions.sameLocation());
        lingToInventory.setHighlightCondition(HighlightConditions.sameLocation());
        linkToQuotes.setHighlightCondition(HighlightConditions.sameLocation());
        linkToBundlesEditor.setHighlightCondition(HighlightConditions.sameLocation());
        linkToInvite.setHighlightCondition(HighlightConditions.sameLocation());
        addToDrawer(new VerticalLayout(linkToCompose, lingToInventory, linkToQuotes, linkToBundlesEditor, linkToInvite));
    }
}

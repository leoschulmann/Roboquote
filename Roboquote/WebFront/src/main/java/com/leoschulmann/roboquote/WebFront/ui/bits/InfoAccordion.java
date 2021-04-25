package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.events.DisableClickableComponents;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

@Getter
public class InfoAccordion extends Accordion {
    private final FormLayout columnLayout;
    private final TextField customer; //todo lookup in DB
    private final TextField customerInfo;
    private final TextField dealer;
    private final TextField dealerInfo;
    private final TextField paymentTerms;
    private final TextField shippingTerms;
    private final TextField warranty;
    private final TextField installation;
    private final TextField comment;
    private final DatePicker validThru;

    public InfoAccordion() {
        setWidthFull();
        columnLayout = new FormLayout();
        columnLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        customer = new TextField("Customer");
        customerInfo = new TextField("Customer info");
        dealer = new TextField("Dealer");
        dealerInfo = new TextField("Dealer info");
        paymentTerms = new TextField("Payment Terms");
        shippingTerms = new TextField("Shipping Terms");
        warranty = new TextField("Warranty");
        installation = new TextField("Installation");
        comment = new TextField("Comment");
        validThru = new DatePicker("Valid through date");

        columnLayout.add(customer);
        columnLayout.add(customerInfo, 2);
        columnLayout.add(dealer);
        columnLayout.add(dealerInfo, 2);
        columnLayout.add(paymentTerms, shippingTerms, warranty, installation, validThru, comment);
        add("Quote details", columnLayout);
    }

    public void addRatesBlock(RatesPanel ratesPanel) {
        columnLayout.add(ratesPanel, 3);
    }

    public void disable(DisableClickableComponents e) {
        columnLayout.setEnabled(false);
    }
}

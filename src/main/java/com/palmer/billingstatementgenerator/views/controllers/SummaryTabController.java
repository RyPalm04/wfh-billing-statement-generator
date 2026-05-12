package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;
import com.palmer.billingstatementgenerator.models.statement.StatementCalculator;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SummaryTabController extends BaseController {
    private static final NumberFormat DOLLAR_FORMATTER = NumberFormat.getCurrencyInstance();

    private VBox root;
    private Consumer<Integer> onJumpToTab;

    public ScrollPane buildView(Consumer<Integer> onJumpToTab) {
        this.onJumpToTab = onJumpToTab;

        root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("summary-scroll");

        refresh();
        return scrollPane;
    }

    public void refresh() {
        root.getChildren().clear();

        var stmt = StatementContext.current();

        // Service Information header
        addSectionHeader("Service Information", 1);
        addInfoRow("Control Number", String.valueOf(stmt.getControlNumber()), 1);
        addInfoRow("Services For", stmt.getServicesForName(), 1);
        addInfoRow("Date of Death", stmt.getDateOfDeath() != null ? stmt.getDateOfDeath().toString() : "", 1);
        addInfoRow("Place of Death", stmt.getPlaceOfDeath(), 1);
        addInfoRow("Service Date", stmt.getServiceDate() != null ? stmt.getServiceDate().toString() : "", 1);

        // Services
        List<ServiceLineItem> selectedServices = stmt.getServices().stream()
            .filter(ServiceLineItem::isSelected)
            .collect(Collectors.toList());

        addSectionHeader("Services, Facilities & Transportation", 2);
        if (stmt.getSelectedPackage() != null) {
            addLineItem(stmt.getSelectedPackage().getName(),
                stmt.getSelectedPackage().getDefaultCost(), 2);
        }
        selectedServices.forEach(s ->
            addLineItem(s.getCatalog().getName(),
                s.getCatalog().getDefaultCost(), 2));
        addTotalRow("Services Total", StatementCalculator.servicesTotal(stmt));

        // Merchandise
        List<MerchandiseLineItem> selectedMerch = stmt.getMerchandise().stream()
            .filter(MerchandiseLineItem::isSelected)
            .collect(Collectors.toList());

        addSectionHeader("Merchandise", 3);
        selectedMerch.forEach(m ->
            addLineItem(m.getCatalog().getName(), m.getPrice(), 3));
        addTotalRow("Merchandise Total", StatementCalculator.merchandiseTotal(stmt));

        // Special Charges
        List<SpecialChargeLineItem> selectedCharges = stmt.getSpecialCharges().stream()
            .filter(SpecialChargeLineItem::isSelected)
            .collect(Collectors.toList());

        addSectionHeader("Special Charges", 4);
        selectedCharges.forEach(sc ->
            addLineItem(sc.getCatalog().getName(), sc.getPrice(), 4));
        addTotalRow("Special Charges Total", StatementCalculator.specialChargesTotal(stmt));

        // Cash Advances
        List<CashAdvanceLineItem> selectedCash = stmt.getCashAdvances().stream()
            .filter(CashAdvanceLineItem::isSelected)
            .collect(Collectors.toList());

        addSectionHeader("Cash Advance Items", 5);
        selectedCash.forEach(ca ->
            addLineItem(ca.getCatalog().getName(), ca.getAmount(), 5));
        addTotalRow("Cash Advances Total", StatementCalculator.cashAdvancesTotal(stmt));

        // Totals
        addSeparator();
        addTotalRow("Sales Tax", StatementCalculator.salesTax(stmt));
        addTotalRow("Subtotal", StatementCalculator.subtotal(stmt));
        addTotalRow("Down Payment", stmt.getPayment());
        addGrandTotalRow("Total", StatementCalculator.finalTotal(stmt));
    }

    private void addSectionHeader(String title, int tabIndex) {
        Label header = new Label(title);
        header.getStyleClass().add("summary-section-header");
        header.setOnMouseClicked(e -> onJumpToTab.accept(tabIndex));
        header.getStyleClass().add("summary-clickable");
        root.getChildren().add(header);
    }

    private void addInfoRow(String label, String value, int tabIndex) {
        if (value == null || value.isEmpty()) return;
        GridPane row = buildRow();
        Label lbl = clickableLabel(label, tabIndex);
        lbl.getStyleClass().add("summary-label");
        Label val = new Label(value);
        val.getStyleClass().add("summary-value");
        GridPane.setConstraints(lbl, 0, 0);
        GridPane.setConstraints(val, 1, 0);
        GridPane.setHalignment(val, HPos.RIGHT);
        row.getChildren().addAll(lbl, val);
        root.getChildren().add(row);
    }

    private void addLineItem(String name, BigDecimal price, int tabIndex) {
        GridPane row = buildRow();
        Label lbl = clickableLabel(name, tabIndex);
        lbl.getStyleClass().add("summary-label");
        Label val = new Label(price != null ? DOLLAR_FORMATTER.format(price) : "");
        val.getStyleClass().add("price-label");
        GridPane.setConstraints(lbl, 0, 0);
        GridPane.setConstraints(val, 1, 0);
        GridPane.setHalignment(val, HPos.RIGHT);
        row.getChildren().addAll(lbl, val);
        root.getChildren().add(row);
    }

    private void addTotalRow(String label, BigDecimal amount) {
        GridPane row = buildRow();
        Label lbl = new Label(label);
        lbl.getStyleClass().add("summary-total-label");
        Label val = new Label(amount != null ? DOLLAR_FORMATTER.format(amount) : "");
        val.getStyleClass().add("summary-total-value");
        GridPane.setConstraints(lbl, 0, 0);
        GridPane.setConstraints(val, 1, 0);
        GridPane.setHalignment(val, HPos.RIGHT);
        row.getChildren().addAll(lbl, val);
        root.getChildren().add(row);
    }

    private void addGrandTotalRow(String label, BigDecimal amount) {
        addSeparator();
        GridPane row = buildRow();
        Label lbl = new Label(label);
        lbl.getStyleClass().add("summary-grand-total-label");
        Label val = new Label(amount != null ? DOLLAR_FORMATTER.format(amount) : "");
        val.getStyleClass().add("summary-grand-total-value");
        GridPane.setConstraints(lbl, 0, 0);
        GridPane.setConstraints(val, 1, 0);
        GridPane.setHalignment(val, HPos.RIGHT);
        row.getChildren().addAll(lbl, val);
        root.getChildren().add(row);
    }

    private void addSeparator() {
        Separator sep = new Separator();
        sep.getStyleClass().add("summary-separator");
        root.getChildren().add(sep);
    }

    private GridPane buildRow() {
        GridPane row = new GridPane();
        row.setMaxWidth(600);
        row.setMinWidth(600);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.ALWAYS);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        col1.setHalignment(HPos.RIGHT);

        row.getColumnConstraints().addAll(col0, col1);
        return row;
    }

    private Label clickableLabel(String text, int tabIndex) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("summary-clickable");
        lbl.setOnMouseClicked(e -> onJumpToTab.accept(tabIndex));
        return lbl;
    }

    @Override
    public void onShow() {
        refresh();
    }
}
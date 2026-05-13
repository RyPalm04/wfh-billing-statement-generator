package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;
import com.palmer.billingstatementgenerator.models.statement.StatementCalculator;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
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
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Controller for the Summary tab, displayed as the final tab before PDF generation.
 * Presents a read-only overview of all selected line items grouped by category,
 * along with calculated totals. Each section header and line item is clickable,
 * navigating the user back to the corresponding data entry tab.
 * Refreshes automatically when the tab is shown.
 */
public class SummaryTabController extends BaseController {

    /**
     * The root container for all summary content.
     */
    private VBox root;

    /**
     * Callback invoked when the user clicks a section header or line item,
     * receiving the tab index to navigate to.
     */
    private Consumer<Integer> onJumpToTab;

    /**
     * Builds the summary tab view and returns it wrapped in a {@link ScrollPane}.
     *
     * @param onJumpToTab
     *         a {@link Consumer} accepting a tab index, invoked when
     *         the user clicks a clickable label to jump to that tab
     *
     * @return a {@link ScrollPane} containing the full summary layout
     */
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

    /**
     * Rebuilds the summary content from the current {@link StatementContext}.
     * Called on tab activation via {@link #onShow()} and after a reset.
     */
    public void refresh() {
        root.getChildren().clear();

        var stmt = StatementContext.current();

        addSectionHeader("Service Information", 1);
        addInfoRow("Control Number", String.valueOf(stmt.getControlNumber()), 1);
        addInfoRow("Services For", stmt.getServicesForName(), 1);
        addInfoRow("Date of Death", stmt.getDateOfDeath() != null ? stmt.getDateOfDeath().toString() : "", 1);
        addInfoRow("Place of Death", stmt.getPlaceOfDeath(), 1);
        addInfoRow("Service Date", stmt.getServiceDate() != null ? stmt.getServiceDate().toString() : "", 1);

        List<ServiceLineItem> selectedServices = stmt.getServices().stream()
                .filter(ServiceLineItem::isSelected)
                .collect(Collectors.toList());

        addSectionHeader("Services, Facilities & Transportation", 2);
        if (stmt.getSelectedPackage() != null) {
            addLineItem(stmt.getSelectedPackage().getName(),
                    stmt.getSelectedPackage().getDefaultCost(), 2);
        }
        selectedServices.forEach(s ->
                addLineItem(s.getCatalog().getName(), s.getCatalog().getDefaultCost(), 2));
        addTotalRow("Services Total", StatementCalculator.servicesTotal(stmt));

        List<MerchandiseLineItem> selectedMerch = stmt.getMerchandise().stream()
                .filter(MerchandiseLineItem::isSelected)
                .collect(Collectors.toList());

        addSectionHeader("Merchandise", 3);
        selectedMerch.forEach(m -> addLineItem(m.getCatalog().getName(),
                m.getCatalog().isDescriptionRequired() ? m.getDescription() : null,
                m.getPrice(), 3));
        addTotalRow("Merchandise Total", StatementCalculator.merchandiseTotal(stmt));

        List<SpecialChargeLineItem> selectedCharges = stmt.getSpecialCharges().stream()
                .filter(SpecialChargeLineItem::isSelected)
                .collect(Collectors.toList());

        addSectionHeader("Special Charges", 4);
        selectedCharges.forEach(sc -> addLineItem(sc.getCatalog().getName(),
                sc.getCatalog().isDescriptionRequired() ? sc.getDescription() : null,
                sc.getPrice(), 4));
        addTotalRow("Special Charges Total", StatementCalculator.specialChargesTotal(stmt));

        List<CashAdvanceLineItem> selectedCash = stmt.getCashAdvances().stream()
                .filter(CashAdvanceLineItem::isSelected)
                .collect(Collectors.toList());

        addSectionHeader("Cash Advance Items", 5);
        selectedCash.forEach(ca -> addLineItem(ca.getCatalog().getName(),
                ca.getProvider().isEmpty() ? null : ca.getProvider(),
                ca.getAmount(), 5));
        addTotalRow("Cash Advances Total", StatementCalculator.cashAdvancesTotal(stmt));

        addSeparator();
        addTotalRow("Sales Tax", StatementCalculator.salesTax(stmt));
        addTotalRow("Subtotal", StatementCalculator.subtotal(stmt));
        addTotalRow("Down Payment", stmt.getPayment());
        addGrandTotalRow("Total", StatementCalculator.finalTotal(stmt));
    }

    /**
     * Adds a clickable section header to the summary.
     *
     * @param title
     *         the section title
     * @param tabIndex
     *         the tab index to navigate to when clicked
     */
    private void addSectionHeader(String title, int tabIndex) {
        Label header = new Label(title);
        header.getStyleClass().addAll("summary-section-header", "summary-clickable");
        header.setOnMouseClicked(e -> onJumpToTab.accept(tabIndex));
        root.getChildren().add(header);
    }

    /**
     * Adds a clickable key-value info row to the summary. Rows with null or empty
     * values are omitted.
     *
     * @param label
     *         the field label
     * @param value
     *         the field value
     * @param tabIndex
     *         the tab index to navigate to when clicked
     */
    private void addInfoRow(String label, String value, int tabIndex) {
        if (value == null || value.isEmpty()) {
            return;
        }
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

    /**
     * Adds a clickable line item row showing the item name and its price.
     *
     * @param name
     *         the line item name
     * @param price
     *         the line item price, or null for a blank price
     * @param tabIndex
     *         the tab index to navigate to when clicked
     */
    private void addLineItem(String name, BigDecimal price, int tabIndex) {
        addLineItem(name, null, price, tabIndex);
    }

    /**
     * Adds a clickable line item row showing the item name, an optional subtitle
     * (description or provider), and its price.
     *
     * @param name
     *         the line item name
     * @param subtitle
     *         an optional description or provider shown below the name, or null to omit
     * @param price
     *         the line item price, or null for a blank price
     * @param tabIndex
     *         the tab index to navigate to when clicked
     */
    private void addLineItem(String name, String subtitle, BigDecimal price, int tabIndex) {
        GridPane row = buildRow();

        Label nameLbl = clickableLabel(name, tabIndex);
        nameLbl.getStyleClass().add("summary-label");

        javafx.scene.Node nameNode;
        if (subtitle != null && !subtitle.isBlank()) {
            Label subLbl = new Label(subtitle);
            subLbl.getStyleClass().add("summary-subtitle");
            VBox nameBox = new VBox(2, nameLbl, subLbl);
            nameNode = nameBox;
        } else {
            nameNode = nameLbl;
        }

        Label val = new Label(price != null ? DOLLAR_FORMATTER.format(price) : "");
        val.getStyleClass().add("price-label");
        GridPane.setConstraints(nameNode, 0, 0);
        GridPane.setConstraints(val, 1, 0);
        GridPane.setHalignment(val, HPos.RIGHT);
        GridPane.setValignment(val, javafx.geometry.VPos.TOP);
        row.getChildren().addAll(nameNode, val);
        root.getChildren().add(row);
    }

    /**
     * Adds a subtotal row showing a label and formatted amount.
     *
     * @param label
     *         the total label
     * @param amount
     *         the total amount, or null for a blank value
     */
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

    /**
     * Adds a separator followed by the grand total row in a larger, more prominent style.
     *
     * @param label
     *         the grand total label
     * @param amount
     *         the grand total amount, or null for a blank value
     */
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

    /**
     * Adds a horizontal separator line to the summary.
     */
    private void addSeparator() {
        Separator sep = new Separator();
        sep.getStyleClass().add("summary-separator");
        root.getChildren().add(sep);
    }

    /**
     * Builds a two-column {@link GridPane} row for displaying label-value pairs.
     * The label column grows to fill available space; the value column is right-aligned.
     *
     * @return a configured {@link GridPane} row
     */
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

    /**
     * Creates a {@link Label} that navigates to the specified tab when clicked.
     *
     * @param text
     *         the label text
     * @param tabIndex
     *         the tab index to navigate to on click
     *
     * @return a clickable {@link Label}
     */
    private Label clickableLabel(String text, int tabIndex) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("summary-clickable");
        lbl.setOnMouseClicked(e -> onJumpToTab.accept(tabIndex));
        return lbl;
    }

    /**
     * Refreshes the summary content when the tab becomes active.
     */
    @Override
    public void onShow() {
        refresh();
    }
}
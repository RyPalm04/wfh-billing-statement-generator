package com.palmer.billingstatementgenerator.pdf;

import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;
import com.palmer.billingstatementgenerator.models.statement.Statement;
import com.palmer.billingstatementgenerator.models.statement.StatementCalculator;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generates PDF billing statements from a {@link Statement} using a JasperReports template.
 * The compiled report is cached after the first call to {@link #generate}.
 * Use {@link #export} to open a save dialog and write the PDF for the current statement.
 */
public final class PdfGenerator {
    private static final Logger log = LoggerFactory.getLogger(PdfGenerator.class);
    private static final String JRXML_RESOURCE = "/com/palmer/billingstatementgenerator/pdf/pdfTemplate.jrxml";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("M/d/yyyy");

    private static JasperReport compiledReport;

    private PdfGenerator() {
    }

    /**
     * Fills the Jasper report template from the given statement and writes the result
     * to the specified file.
     *
     * @param stmt
     *         the statement to export
     * @param outputFile
     *         the destination file to write the PDF to
     *
     * @throws IOException
     *         if the template cannot be loaded or PDF generation fails
     */
    public static void generate(Statement stmt, File outputFile) throws IOException {
        try {
            JasperReport report = compiledReport();
            Map<String, Object> row = buildFieldMap(stmt);
            JRMapArrayDataSource ds = new JRMapArrayDataSource(new Map[]{row});
            JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), ds);
            JasperExportManager.exportReportToPdfFile(print, outputFile.getAbsolutePath());
        } catch (JRException e) {
            throw new IOException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private static synchronized JasperReport compiledReport() throws JRException, IOException {
        if (compiledReport == null) {
            log.debug("Compiling Jasper report template");
            try (InputStream in = PdfGenerator.class.getResourceAsStream(JRXML_RESOURCE)) {
                if (in == null) {
                    throw new IOException("Missing template resource: " + JRXML_RESOURCE);
                }
                compiledReport = JasperCompileManager.compileReport(in);
            }
        }
        return compiledReport;
    }

    private static Map<String, Object> buildFieldMap(Statement stmt) {
        Map<String, Object> m = new HashMap<>();

        m.put("controlNumber", stmt.getControlNumber());
        m.put("servicesFor", nullSafe(stmt.getServicesForName()));
        m.put("dateOfDeath", formatDate(stmt.getDateOfDeath()));
        m.put("placeOfDeath", nullSafe(stmt.getPlaceOfDeath()));
        m.put("serviceDate", formatDate(stmt.getServiceDate()));
        m.put("embalmingReason", nullSafe(stmt.getReasonForEmbalming()));

        ServicePackage pkg = stmt.getSelectedPackage();
        m.put("packagePrice", pkg != null ? toDouble(pkg.getDefaultCost()) : null);

        String[] serviceFields = {
                "basicServicesPrice", "embalmingPrice", "otherPreparationPrice",
                "useForVisitationPrice", "useForFuneralPrice", "useForMemorialPrice",
                "useForGravesidePrice", "funeralCoachPrice", "pallbearerCarPrice",
                "serviceCarPrice", "transferOfRemainsPrice", "otherAPrice", "otherBPrice"
        };
        putBySort(m, serviceFields, stmt.getServices(),
                ServiceLineItem::isSelected,
                s -> toDouble(s.getCatalog().getDefaultCost()));

        String[] merchPriceFields = {
                "casketPrice", "urnPrice", "vaultPrice", "serviceAccessoryPrice",
                "registerBookPrice", "thankYouCardPrice", "memorialFolderPrice",
                "memorialVideoPrice", "jewelryPrice", "burialSupervisionPrice",
                "graveMarkerPrice", "otherMerchAPrice", "otherMerchBPrice"
        };
        String[] merchDescFields = {
                "casketDescription", "cremationDescription", "vaultDescription", null,
                "registerBookDescription", null, "folderDescription",
                "videoDescription", "jewelryDescription", null,
                null, "otherMerchADescription", "otherMerchBDescription"
        };
        putBySort(m, merchPriceFields, stmt.getMerchandise(),
                MerchandiseLineItem::isSelected,
                merch -> toDouble(merch.getPrice()));
        putDescriptionsBySort(m, merchDescFields, stmt.getMerchandise(),
                MerchandiseLineItem::isSelected,
                MerchandiseLineItem::getDescription);

        String[] specialPriceFields = {
                "graveSetupPrice", "cremationPrice", "mileagePrice",
                "remainsForwardingPrice", "remainsReceivingPrice", "vaultWeekendPrice",
                "immediateBurialPrice"
        };
        String[] specialDescFields = {
                "graveDescription", null, "mileageDescription",
                null, null, null, null
        };
        putBySort(m, specialPriceFields, stmt.getSpecialCharges(),
                SpecialChargeLineItem::isSelected,
                sc -> toDouble(sc.getPrice()));
        putDescriptionsBySort(m, specialDescFields, stmt.getSpecialCharges(),
                SpecialChargeLineItem::isSelected,
                SpecialChargeLineItem::getDescription);

        String[] cashDetailFields = {
                "graveOpeningDetail", "weekendHolidayDetail", "newspaperADetail",
                "newspaperBDetail", "newspaperCDetail", "newspaperDDetail",
                "radioNoticeDetail", "ministerADetail", "ministerBDetail",
                "organistDetail", "singerADetail", "singerBDetail", "singerCDetail",
                "hairdresserDetail", "deathCertDetail", "outOfTownDetail",
                "markerDateDetail", "flowerDetail", "cashAdvOtherADetail", "cashAdvOtherBDetail"
        };
        String[] cashPriceFields = {
                "graveOpeningPrice", "weekendHolidayPrice", "newspaperAPrice",
                "newspaperBPrice", "newspaperCPrice", "newspaperDPrice",
                "radioNoticePrice", "ministerAPrice", "ministerBPrice",
                "organistPrice", "singerAPrice", "singerBPrice", "singerCPrice",
                "hairdresserPrice", "deathCertPrice", "outOfTownPrice",
                "markerDatePrice", "flowerPrice", "cashAdvOtherAPrice", "cashAdvOtherBPrice"
        };

        List<CashAdvanceLineItem> cashAdvances = stmt.getCashAdvances();

        int cashCount = Math.min(cashDetailFields.length, cashAdvances.size());

        for (int i = 0; i < cashCount; i++) {
            CashAdvanceLineItem item = cashAdvances.get(i);
            m.put(cashDetailFields[i], item.isSelected() ? nullSafe(item.getProvider()) : "");
            m.put(cashPriceFields[i], item.isSelected() ? toDouble(item.getAmount()) : null);
        }

        m.put("totalServices", StatementCalculator.servicesTotal(stmt).doubleValue());
        m.put("totalMerchandise", StatementCalculator.merchandiseTotal(stmt).doubleValue());
        m.put("totalSpecialCharges", StatementCalculator.specialChargesTotal(stmt).doubleValue());
        m.put("totalCashAdv", StatementCalculator.cashAdvancesTotal(stmt).doubleValue());
        m.put("salesTax", StatementCalculator.salesTax(stmt).doubleValue());
        m.put("subTotal", StatementCalculator.subtotal(stmt).doubleValue());
        m.put("downPayment", toDouble(stmt.getPayment()));
        m.put("finalTotal", StatementCalculator.finalTotal(stmt).doubleValue());

        return m;
    }

    private static <T> void putBySort(Map<String, Object> m, String[] fields, List<T> items,
                                      Predicate<T> selectedFn, Function<T, Double> valueFn) {
        int n = Math.min(fields.length, items.size());
        for (int i = 0; i < n; i++) {
            T item = items.get(i);
            m.put(fields[i], selectedFn.test(item) ? valueFn.apply(item) : null);
        }
    }

    private static <T> void putDescriptionsBySort(Map<String, Object> m, String[] fields, List<T> items,
                                                  Predicate<T> selectedFn, Function<T, String> descFn) {
        int n = Math.min(fields.length, items.size());
        for (int i = 0; i < n; i++) {
            String field = fields[i];
            if (field == null) {
                continue;
            }
            T item = items.get(i);
            m.put(field, selectedFn.test(item) ? nullSafe(descFn.apply(item)) : "");
        }
    }

    private static Double toDouble(BigDecimal bd) {
        return bd == null ? null : bd.doubleValue();
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static String formatDate(LocalDate d) {
        return d == null ? "" : d.format(DATE_FMT);
    }

    /**
     * Opens a save dialog and exports the current statement as a PDF.
     * Shows a success or error alert when the operation completes.
     *
     * @param ownerWindow
     *         the owner window for the save dialog and any alerts shown
     */
    public static void export(Window ownerWindow) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Statement as PDF");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
        fc.setInitialFileName("statement-" + StatementContext.current().getControlNumber() + ".pdf");

        File output = fc.showSaveDialog(ownerWindow);
        if (output == null) {
            return;
        }

        log.info("Exporting PDF for control number {} to {}", StatementContext.current().getControlNumber(), output.getAbsolutePath());
        try {
            generate(StatementContext.current(), output);
            log.info("PDF export successful: {}", output.getAbsolutePath());
            new Alert(Alert.AlertType.INFORMATION, "PDF saved to:\n" + output.getAbsolutePath()).showAndWait();
        } catch (Throwable t) {
            log.error("PDF export failed", t);
            new Alert(Alert.AlertType.ERROR, "Failed to generate PDF: " + t.getMessage()).showAndWait();
        }
    }
}

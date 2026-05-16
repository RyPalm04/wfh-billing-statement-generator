package com.palmer.billingstatementgenerator.pdf;

import com.palmer.billingstatementgenerator.client.StatementClient;
import com.palmer.billingstatementgenerator.models.statement.PdfResult;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.views.dialogs.MessageDialog;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles PDF output for the current billing statement by fetching rendered PDF bytes
 * from the API. Use {@link #export} to save the PDF to disk or {@link #print} to send
 * it to the system print dialog.
 */
public final class PdfGenerator {
    private static final Logger log = LoggerFactory.getLogger(PdfGenerator.class);

    private PdfGenerator() {
    }

    /**
     * Fetches the current statement as a PDF from the API, writes it to a temp file,
     * and opens the system print dialog. Shows an error dialog if the operation fails.
     *
     * @param ownerWindow
     *         the owner window for any alerts shown
     */
    public static void print(Window ownerWindow) {
        log.info("Printing statement for control number {}", StatementContext.current().getControlNumber());
        try {
            PdfResult pdf = new StatementClient().getPdf(StatementContext.getSavedId());
            Path temp = Files.createTempFile(pdf.fileName().split("\\.")[0], ".pdf");
            Files.write(temp, pdf.bytes());
            Desktop.getDesktop().print(temp.toFile());
        } catch (IOException e) {
            log.error("Error printing statement for control number {}", StatementContext.current().getControlNumber(), e);
            new MessageDialog("Printing Error", "Failed to print PDF: " + e.getMessage()).open();
        }
    }

    /**
     * Opens a save dialog and exports the current statement as a PDF.
     * Shows a success or error alert when the operation completes.
     *
     * @param ownerWindow
     *         the owner window for the save dialog and any alerts shown
     */
    public static void export(Window ownerWindow) {
        try {
            PdfResult pdf = new StatementClient().getPdf(StatementContext.getSavedId());
            FileChooser fc = new FileChooser();
            fc.setTitle("Save Statement as PDF");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
            fc.setInitialFileName(pdf.fileName());

            File output = fc.showSaveDialog(ownerWindow);
            if (output == null) {
                return;
            }

            log.info("Exporting PDF for control number {} to {}", StatementContext.current().getControlNumber(), output.getAbsolutePath());

            Files.write(output.toPath(), pdf.bytes());
            log.info("PDF export successful: {}", output.getAbsolutePath());
            new MessageDialog("PDF Saved", "PDF saved to:\n" + output.getAbsolutePath()).open();
        } catch (Throwable t) {
            log.error("PDF export failed", t);
            new MessageDialog("Export Error", "Failed to generate PDF: " + t.getMessage()).open();
        }
    }
}

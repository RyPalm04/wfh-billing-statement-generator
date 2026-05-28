package com.palmer.billingstatementgenerator.services;

import com.palmer.billingstatementgenerator.client.StatementClient;
import com.palmer.billingstatementgenerator.models.statement.PdfResult;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.views.dialogs.MessageDialog;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PdfService {
      private static PdfService INSTANCE;
      private final StatementClient client = new StatementClient();
      private PdfResult pendingPdf;
      private File exportTarget;

      private PdfService() {
      }

      public static PdfService getInstance() {
          if (INSTANCE == null) {
              INSTANCE = new PdfService();
          }
          return INSTANCE;
      }

      private final Service<PdfResult> fetchService = new Service<>() {
          @Override protected Task<PdfResult> createTask() {
              return new Task<>() {
                  @Override protected PdfResult call() {
                      return client.getPdf(StatementContext.getSavedId());
                  }
              };
          }
      };

      private final Service<Void> writeService = new Service<>() {
          @Override protected Task<Void> createTask() {
              PdfResult pdf = pendingPdf;
              File target = exportTarget;
              return new Task<>() {
                  @Override protected Void call() throws Exception {
                      Files.write(target.toPath(), pdf.bytes());
                      return null;
                  }
              };
          }
      };

      private final Service<Void> printService = new Service<>() {
          @Override protected Task<Void> createTask() {
              return new Task<>() {
                  @Override protected Void call() throws Exception {
                      PdfResult pdf = client.getPdf(StatementContext.getSavedId());
                      Path temp = Files.createTempFile(pdf.fileName().split("\\.")[0], ".pdf");
                      Files.write(temp, pdf.bytes());
                      Desktop.getDesktop().print(temp.toFile());
                      return null;
                  }
              };
          }
      };

      public void export(Window ownerWindow) {
          fetchService.setOnSucceeded(e -> {
              pendingPdf = fetchService.getValue();
              FileChooser fc = new FileChooser();
              fc.setTitle("Save Statement as PDF");
              fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
              fc.setInitialFileName(pendingPdf.fileName());
              exportTarget = fc.showSaveDialog(ownerWindow);
              if (exportTarget == null) {
                  return;
              }
              writeService.setOnSucceeded(ev ->
                  new MessageDialog("PDF Saved", "PDF saved to:\n" + exportTarget.getAbsolutePath()).show());
              writeService.setOnFailed(ev ->
                  new MessageDialog("Export Error", "Failed to save PDF.").show());
              writeService.restart();
          });
          fetchService.setOnFailed(e ->
              new MessageDialog("Export Error", "Failed to generate PDF.").show());
          fetchService.restart();
      }

      public void print(Window ownerWindow) {
          printService.setOnFailed(e ->
              new MessageDialog("Print Error", "Failed to print PDF.").show());
          printService.restart();
      }

      public BooleanBinding runningProperty() {
          return fetchService.runningProperty()
                  .or(writeService.runningProperty())
                  .or(printService.runningProperty());
      }
  }
package com.palmer.billingstatementgenerator.services;

import com.palmer.billingstatementgenerator.client.StatementClient;
import com.palmer.billingstatementgenerator.models.statement.SavedStatementSummary;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.views.dialogs.MessageDialog;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Consumer;

public final class StatementService {
    private static StatementService INSTANCE;
    private final StatementClient client = new StatementClient();
    private int loadId;

    private StatementService() {
    }

    public static StatementService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatementService();
        }

        return INSTANCE;
    }

    private final Service<Integer> saveService = new Service<>() {
        @Override
        protected Task<Integer> createTask() {
            return new Task<>() {
                @Override
                protected Integer call() {
                    Integer savedId = StatementContext.getSavedId();
                    if (savedId == null) {
                        return client.save(StatementContext.current());
                    }
                    client.update(savedId, StatementContext.current());
                    return savedId;
                }
            };
        }
    };

    private final Service<List<SavedStatementSummary>> fetchService = new Service<>() {
        @Override
        protected Task<List<SavedStatementSummary>> createTask() {
            return new Task<>() {
                @Override
                protected List<SavedStatementSummary> call() {
                    return client.getAllStatements();
                }
            };
        }
    };

    private final Service<Void> loadService = new Service<>() {
        @Override
        protected Task<Void> createTask() {
            int id = loadId;
            return new Task<>() {
                @Override
                protected Void call() {
                    StatementContext.load(id);
                    return null;
                }
            };
        }
    };

    public void save(Runnable onComplete) {
        saveService.setOnSucceeded(e -> {
            StatementContext.markSaved(saveService.getValue());
            if (onComplete != null) {
                onComplete.run();
            }
        });
        saveService.setOnFailed(e -> new MessageDialog("Save Error", "Unable to save the statement. Please try again.").show());
        saveService.restart();
    }

    public void fetchAll(Consumer<List<SavedStatementSummary>> onSuccess) {
        fetchService.setOnSucceeded(e -> onSuccess.accept(fetchService.getValue()));
        fetchService.setOnFailed(e -> new MessageDialog("Connection Error", "Unable to retrieve saved statements.").show());
        fetchService.restart();
    }

    public void load(int id, Runnable onComplete) {
        loadId = id;
        loadService.setOnSucceeded(e -> {
            if (onComplete != null) {
                onComplete.run();
            }
        });
        loadService.setOnFailed(e -> new MessageDialog("Load Error", "Unable to load the selected statement.").show());
        loadService.restart();
    }

    public BooleanBinding runningProperty() {
        return saveService.runningProperty()
                .or(fetchService.runningProperty())
                .or(loadService.runningProperty());
    }
}
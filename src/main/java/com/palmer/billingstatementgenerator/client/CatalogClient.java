package com.palmer.billingstatementgenerator.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.palmer.billingstatementgenerator.models.catalog.CashAdvance;
import com.palmer.billingstatementgenerator.models.catalog.Merchandise;
import com.palmer.billingstatementgenerator.models.catalog.Service;
import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
import com.palmer.billingstatementgenerator.models.catalog.SpecialCharge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CatalogClient {
    private static final Logger log = LoggerFactory.getLogger(CatalogClient.class);

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public List<Service> findAllServices() {
        try {
            HttpResponse<String> response = getResponse("/services");

            List<Service> result = new ArrayList<>();

            for (JsonNode node : mapper.readTree(response.body())) {
                result.add(new Service(
                        node.get("id").asInt(),
                        node.get("sortOrder").asInt(),
                        node.get("name").asText(),
                        node.get("defaultCost").decimalValue(),
                        node.get("includedInPackage").asBoolean()));
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to load services", e);
            throw new RuntimeException("Failed to load services", e);
        }
    }

    public List<CashAdvance> findAllCashAdvances() {
        try {
            HttpResponse<String> response = getResponse("/cash-advances");

            List<CashAdvance> result = new ArrayList<>();

            for (JsonNode node : mapper.readTree(response.body())) {
                result.add(new CashAdvance(
                        node.get("id").asInt(),
                        node.get("sortOrder").asInt(),
                        node.get("name").asText()));
            }

            return result;
        } catch (Exception e) {
            log.error("Failed to load cash-advances", e);
            throw new RuntimeException("Failed to load cash-advances", e);
        }
    }

    public List<Merchandise> findAllMerchandise() {
        try {
            HttpResponse<String> response = getResponse("/merchandise");

            List<Merchandise> result = new ArrayList<>();

            for (JsonNode node : mapper.readTree(response.body())) {
                result.add(new Merchandise(
                        node.get("id").asInt(),
                        node.get("sortOrder").asInt(),
                        node.get("name").asText(),
                        node.get("defaultCost").decimalValue(),
                        node.get("requiresDescription").asBoolean(),
                        node.get("salesTaxable").asBoolean(),
                        Merchandise.PricingMode.valueOf(node.get("pricingMode").asText())));
            }

            return result;
        } catch (Exception e) {
            log.error("Failed to load merchandise", e);
            throw new RuntimeException("Failed to load merchandise", e);
        }
    }

    public List<ServicePackage> findAllServicePackages() {
        try {
            HttpResponse<String> response = getResponse("/packages");

            List<ServicePackage> result = new ArrayList<>();

            for (JsonNode node : mapper.readTree(response.body())) {
                result.add(new ServicePackage(
                        node.get("id").asInt(),
                        node.get("sortOrder").asInt(),
                        node.get("name").asText(),
                        node.get("defaultCost").decimalValue()));
            }

            return result;
        } catch (Exception e) {
            log.error("Failed to load service packages", e);
            throw new RuntimeException("Failed to load service packages", e);
        }
    }

    public List<SpecialCharge> findAllSpecialCharges() {
        try {
            HttpResponse<String> response = getResponse("/special-charges");

            List<SpecialCharge> result = new ArrayList<>();

            for (JsonNode node : mapper.readTree(response.body())) {
                result.add(new SpecialCharge(
                        node.get("id").asInt(),
                        node.get("sortOrder").asInt(),
                        node.get("name").asText(),
                        node.get("defaultCost").decimalValue(),
                        node.get("requiresDescription").asBoolean()));
            }

            return result;
        } catch (Exception e) {
            log.error("Failed to load special charges", e);
            throw new RuntimeException("Failed to load special charges", e);
        }
    }

    public List<Integer> findServiceIdsForPackage(int packageId) {
        try {
            HttpResponse<String> response = getResponse("/packages/" + packageId);

            List<Integer> result = new ArrayList<>();

            JsonNode root = mapper.readTree(response.body());

            for (JsonNode id : root.get("serviceIds")) {
                result.add(id.asInt());
            }

            return result;
        } catch (Exception e) {
            log.error("Failed to load service ids", e);
            throw new RuntimeException("Failed to load service ids", e);
        }
    }

    private static HttpResponse<String> getResponse(String endPoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.getCatalogUrl() + endPoint))
                .GET()
                .build();

        return ApiConfig
                .getHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }
}

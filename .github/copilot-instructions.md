# Copilot instructions for wfh-billing-statement-generator

Quick reference for Copilot sessions interacting with this repository.

## Build, test, and run (commands)
- Build (full): ./gradlew build
- Run app: ./gradlew run
- Run tests (all): ./gradlew test
- Clean: ./gradlew clean
- Run a single test class (example):
  - ./gradlew test --tests "com.palmer.billingstatementgenerator.db.DatabaseSpec"
  - or use a wildcard: ./gradlew test --tests "*DatabaseSpec"
- Run a single test method (example):
  - ./gradlew test --tests "com.palmer.billingstatementgenerator.db.DatabaseSpec.someMethod"

Notes:
- The project uses the Gradle wrapper (gradlew). Prefer the wrapper to match CI.
- No dedicated lint/checkstyle tasks are configured in build.gradle.

## High-level architecture
- JVM app written in Java 11 with JavaFX 17 (org.openjfx.javafxplugin configured).
- Entry points:
  - Launcher.java — packaging/launch helper (Gradle application mainClass)
  - MainApp.java — JavaFX Application with UI lifecycle
- Packages:
  - dao/ — data access objects interacting with H2 (classes named *Dao.java)
  - db/ — Database initialization and SQL scripts (src/main/resources/db/schema.sql, seed.sql)
  - models/ — domain models. Two sub-patterns: catalog (Merchandise, Service, etc.) and lineitems (ServiceLineItem, etc.)
  - pdf/ — PdfGenerator.java and PDF-related logic; templates live in src/main/resources/pdf/*.jrxml
  - views/ — JavaFX UI components and tabs (multi-tab generator UI)
- Resources:
  - src/main/resources/pdf/pdfTemplate.jrxml — JasperReports template used by PdfGenerator
  - src/main/resources/css/style.css — UI styling
  - src/main/resources/img/ — splash/logo images
- Persistence: H2 in-memory DB initialized at startup from SQL scripts in resources/db.
- Tests: Groovy/Spock specs under src/test/groovy; Gradle test uses JUnit Platform with Spock.

## Key conventions and patterns
- DAO suffix: classes that interact with the DB are suffixed with Dao (e.g., ServiceDao, MerchandiseDao).
- Models are split into catalog vs. lineitems to separate configurable catalog entries from statement line items.
- PDF generation centralizes in PdfGenerator; templates (.jrxml) and styling must match what's referenced by that class.
- UI is organized into tabs under views/tabs; add new UI panels as new Tab* files to follow existing pattern.
- Database initialization: update src/main/resources/db/schema.sql and seed.sql when adding tables or sample data. The app expects these to be present in resources.
- Gradle wrapper present: always use ./gradlew to run builds/tests to ensure consistent toolchain.
- Java toolchain set to Java 11 in build.gradle — ensure IDE run configurations use Java 11.

## Useful file locations
- build.gradle — project configuration, dependencies (Spock, DynamicReports, H2, JavaFX)
- settings.gradle — Gradle settings
- src/main/java/com/palmer/billingstatementgenerator — application source
- src/main/resources — jrxml templates, SQL, images, CSS
- src/test/groovy — Spock tests

## When suggesting code changes
- Keep changes localized to the package boundaries above. Changing PDF templates requires validating PdfGenerator's template paths.
- When touching the DB schema, update schema.sql and seed.sql together.
- For UI changes, prefer adding new Tab* classes under views/tabs and wire them into GeneratorTabs rather than modifying existing large classes.

---

If you'd like this adapted (more detail, examples for running single Spock tests, or adding CI/formatting rules), say which areas to expand.
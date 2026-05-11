# Wright Funeral Home Billing Statement Generator

A desktop JavaFX application to generate billing statements. Users select packages, services, merchandise, special charges, and cash advances and export a PDF statement.

## Overview

- Stack: Java 11, JavaFX 17, Gradle
- Data: H2 in-memory DB initialized from SQL scripts
- PDF: JasperReports / DynamicReports

## Requirements

- JDK 11 or higher
- Use the included Gradle wrapper (`./gradlew`) to match CI

## Quick start

Clone and enter the repo:

```bash
git clone <repository-url>
cd wfh-billing-statement-generator
```

Build and run:

```bash
./gradlew build
./gradlew run
```

If you encounter UnsupportedClassVersionError, ensure JAVA_HOME points to JDK 11+.

## Useful commands

- Build: `./gradlew build`
- Run: `./gradlew run`
- Test: `./gradlew test`
- Clean: `./gradlew clean`

Run a single test class:

```bash
./gradlew test --tests "com.palmer.billingstatementgenerator.db.DatabaseSpec"
```

Run a single test method:

```bash
./gradlew test --tests "com.palmer.billingstatementgenerator.db.DatabaseSpec.someMethod"
```

## UI notes

The UI follows an FXML + controller pattern for TabTwo (the only FXML-backed tab); other tabs are built programmatically. Key points:

- GeneratorTabs handles tab wiring: it merges FXML GridPane children when an FXML exists, wires the shared Previous/Next/Clear buttons, and calls controller lifecycle hooks (onShow/onHide).
- Controllers extend `views.controllers.BaseController` which provides:
  - addCheckboxRowsWithPrices / addCheckboxRows helpers (stream-based builders)
  - extractCheckboxesFromGrid convenience method
  - configTextFieldForInts and bindIntegerTextField helpers
  - Lifecycle hooks: `onShow()` and `onHide()`
- Controllers use streams and AtomicInteger to declaratively build rows. Description/provider fields auto-select their row's checkbox when non-empty.

## Tests

Tests use Groovy/Spock. Run with `./gradlew test`. Tests are located under `src/test/groovy` and `src/test/java`.

## Project layout

- `build.gradle`, `gradlew`
- `src/main/java` — application code (dao, db, models, pdf, views)
- `src/main/resources` — SQL, FXML, PDF templates, images, CSS
- `src/test` — tests

## TODOs

- Complete Totals calculation in `PdfGenerator.java`.
- Add more unit tests for UI controllers.
- Add a lightweight UI smoke test harness for manual verification.
- Add a project license.

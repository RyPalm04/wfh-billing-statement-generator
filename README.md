# Wright Funeral Home Billing Statement Generator

A desktop application built with JavaFX for generating billing statements for Wright Funeral Home. The application allows users to select service packages, individual services, merchandise, special charges, and cash advances to generate a professional PDF billing statement.

## Overview

- **Stack:** Java 11, JavaFX 17, Gradle.
- **Data Storage:** H2 in-memory database (initialized from SQL scripts).
- **PDF Generation:** JasperReports / DynamicReports.
- **Key Features:**
  - Multi-tab interface for step-by-step billing.
  - PDF export using JasperReports templates.
  - FXML + controller pattern for UI: controllers live under `views/controllers` and are backed by FXML in `resources/views`.
  - Shared BaseController helpers (checkbox builders, formatters) and lifecycle hooks (onShow/onHide).
  - Stream-based UI builders for concise, maintainable code.

## Requirements

- **Java Development Kit (JDK):** Version 11 or higher (required for JavaFX Gradle plugin).
- **Gradle:** 7.x or higher (or use the provided `gradlew` wrapper).

## Setup & Run

### Clone the repository
```bash
git clone <repository-url>
cd wfh-billing-statement-generator
```

### Build the project
```bash
./gradlew build
```

### Run the application
```bash
./gradlew run
```

Notes:
- Use the Gradle wrapper (`./gradlew`) to match CI's toolchain.
- Local compilation and running require JDK 11+; if you see UnsupportedClassVersionErrors, switch your JAVA_HOME to a JDK 11+ installation.

## Commands (quick)

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

## UI architecture notes

- GeneratorTabs is the central tab wrapper that loads FXML content (when present), merges GridPane children, wires the shared clear/next buttons, and registers controller lifecycle hooks.
- Controllers extend `views.controllers.BaseController` which exposes helpers:
  - addCheckboxRowsWithPrices / addCheckboxRows — stream-based helpers that build rows, optional description fields, and price labels.
  - extractCheckboxesFromGrid — convenience to collect CheckBoxes for wiring.
  - configTextFieldForInts, bindIntegerTextField — common binding helpers.
  - Lifecycle hooks: `onShow()` and `onHide()` — called by GeneratorTabs when a tab gains/loses selection.
- Controllers use streams and AtomicInteger to lay out rows while keeping the code declarative and concise.
- Description/provider text fields auto-select their row's checkbox when non-empty.

## Tests

The project uses Groovy/Spock for tests. To run the test suite:
```bash
./gradlew test
```

Tests live under `src/test/groovy` and `src/test/java`.

## Project Structure

```text
.
├── build.gradle            # Gradle build configuration
├── gradlew                 # Gradle wrapper script
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com/palmer/billingstatementgenerator
│   │   │       ├── dao/                # Data Access Objects (H2)
│   │   │       ├── db/                 # Database initialization
│   │   │       ├── models/             # Business logic models
│   │   │       ├── pdf/                # PDF generation logic (PdfGenerator)
│   │   │       └── views/              # JavaFX UI components and controllers
│   │   │           └── controllers     # FXML controllers and BaseController
│   │   └── resources
│   │       ├── db/         # SQL schema and seed data
│   │       └── views/      # FXML files for tabs under com/palmer/.../views
│   └── test                 # Tests
└── settings.gradle         # Gradle project settings
```

## TODOs

- [ ] Complete the "Totals" calculation logic in `PdfGenerator.java`.
- [ ] Add more comprehensive unit tests for UI controllers.
- [ ] Consider a small UI smoke test harness for JavaFX.
- [ ] Define the official license for the project.

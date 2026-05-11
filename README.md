# Wright Funeral Home Billing Statement Generator

A desktop application built with JavaFX for generating billing statements for Wright Funeral Home. The application allows users to select service packages, individual services, merchandise, special charges, and cash advances to generate a professional PDF billing statement.

## 🚀 Overview

- **Stack:** Java 11, JavaFX 17, Gradle.
- **Data Storage:** H2 in-memory database (initialized from SQL scripts).
- **PDF Generation:** JasperReports / DynamicReports.
- **Key Features:** 
  - Multi-tab interface for step-by-step billing.
  - Automatic calculation of totals (TODO: verify final implementation of totals).
  - PDF export using JasperReports templates.

## 📋 Requirements

- **Java Development Kit (JDK):** Version 11 or higher.
- **Gradle:** 7.x or higher (or use the provided `gradlew` wrapper).

## 🛠️ Setup & Run

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

## 📜 Scripts

- `./gradlew run`: Launches the JavaFX application.
- `./gradlew test`: Runs Groovy tests.
- `./gradlew build`: Compiles code and runs tests.
- `./gradlew clean`: Removes the build directory.

## ⚙️ Environment Variables

Currently, the application does not rely on external environment variables. The database is in-memory and initialized at startup.

## 🧪 Tests

The project uses Groovy for testing. 
To run tests:
```bash
./gradlew test
```
Tests are located in `src/test/java`.

## 📂 Project Structure

```text
.
├── build.gradle            # Gradle build configuration
├── gradlew                 # Gradle wrapper script
├── src
│   ├── main
│   │   ├── java            # Source code
│   │   │   └── com/palmer/billingstatementgenerator
│   │   │       ├── Launcher.java       # Application entry point
│   │   │       ├── MainApp.java        # JavaFX application class
│   │   │       ├── dao/                # Data Access Objects (H2)
│   │   │       ├── db/                 # Database initialization
│   │   │       ├── models/             # Business logic models
│   │   │       ├── pdf/                # PDF generation logic
│   │   │       └── views/              # JavaFX UI components
│   │   └── resources
│   │       ├── db/         # SQL schema and seed data
│   │       ├── pdf/        # JasperReports templates (.jrxml)
│   │       └── original/   # Original reference documents
│   └── test/java           # Unit tests
└── settings.gradle         # Gradle project settings
```

## 📄 License

TODO: Add license information.

## 📝 TODOs

- [ ] Complete the "Totals" calculation logic in `PdfGenerator.java`.
- [ ] Add more comprehensive unit tests for UI components.
- [ ] Implement persistence if required (currently using in-memory H2).
- [ ] Define the official license for the project.

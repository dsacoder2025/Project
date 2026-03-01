# Payroll Calculator & Text Analyzer

This repository contains the solution for the Technical Assessment, implemented in pure Java.

## 🛠️ Engineering Principles:

1.  **Strategy Pattern (SOLID - Open/Closed Principle)**: 
    -   Implemented `PayStrategy` interface with `FullTimeStrategy`, `PartTimeStrategy`, and `ContractorStrategy`.
    -   Allows adding new employee types (e.g., *Intern*) without modifying the core `PayrollProcessor` logic.

2.  **Financial Precision (Domain Awareness)**:
    -   Used `BigDecimal` for all monetary calculations instead of `double`.
    -   Prevents IEEE 754 floating-point errors (e.g., missing pennies in tax calculations), which is critical for financial software.

3.  **Builder Pattern**:
    -   Implemented a fluent `Employee.builder()` API.
    -   Ensures clean, readable object creation and eliminates "boolean blindness" in constructors.

4.  **Comprehensive Testing**:
    -   **100% Unit Test Coverage** including edge cases (e.g., tax brackets, deduction caps).

---

## 🚀 Quick Start (GitHub Codespaces)
The easiest way to run this project is using **GitHub Codespaces**, which provides a pre-configured cloud environment.

1.  Click the `<> Code` button at the top of the repository.
2.  Select the **Codespaces** tab.
3.  Click **Create codespace on main**.
4.  Once the terminal loads at the bottom, copy and paste the following commands:

### Compile & Run
```bash
# Compile all Java files
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt

# Run the Demo Application
java -cp out com.assessment.Main
```

### Run Tests
```bash
mvn test
```

---

## 💻 Local Setup
If you prefer running locally on your machine:

### Prerequisites
-   **Java 17** or higher installed.
-   (Optional) **Maven** installed (for running tests).

### Steps
1.  Open your terminal/command prompt.
2.  Navigate to the project root directory.
3.  Run the compile commands shown above.

## Project Structure
The project follows standard Java package structure.
-   `src/main/java/com/assessment/model`: Data models (Employee, PaySlip).
-   `src/main/java/com/assessment/service`: Business logic (PayrollProcessor, Strategies).
-   `src/main/java/com/assessment/Main.java`: Entry point and demo scenarios.

**Note**: If copying files manually, ensure you preserve the folder structure (e.g., `com/assessment/model`) matching the package names.

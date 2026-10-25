# Selenium Java Hybrid Framework

[![Selenium Tests](https://github.com/samramkumarqa/selenium-java-framework/actions/workflows/selenium.yml/badge.svg)](https://github.com/samramkumarqa/selenium-java-framework/actions/workflows/selenium.yml)

End-to-end web test automation with **Selenium 4**, **Java 17** and **TestNG**, against the [AutomationExercise](https://automationexercise.com) practice store.

The headline test registers a brand-new customer, searches for a product, adds it to the cart, checks out, pays, confirms the order and deletes the account. It is data-driven: scenarios come from a JSON file or a **Google Sheet**, and results can be written back to the sheet.

## Highlights

| Area | What it shows |
| --- | --- |
| Page Object Model | One class per page, fluent navigation, a shared header component |
| Hybrid data-driven | Purchase scenarios from JSON or Google Sheets (service account), chosen by config |
| Results to Sheets | Each run appends pass/fail rows to a Results tab; a CSV is always written too |
| API-assisted tests | Accounts are created and deleted through the site's API, so UI tests stay focused and leave no data behind |
| Reliable waits | Explicit waits throughout; no `Thread.sleep` |
| Parallel runs | Thread-safe `ThreadLocal` WebDriver, TestNG parallel methods |
| No driver binaries | Selenium Manager downloads the right driver automatically |
| Reporting | ExtentReports HTML with screenshots on failure |
| Config | `config.properties` defaults, overridable by `-D` flags or environment variables |
| CI | GitHub Actions on every push; Chrome + Firefox on a weekday schedule |

## Test coverage

| Class | Tests |
| --- | --- |
| `PurchaseFlowTest` | Register → search → cart → checkout → pay → order placed → delete account (one run per data row) |
| `LoginTest` | Valid login and logout, wrong password, duplicate-email signup |
| `CartTest` | Quantity and line total, removing an item |
| `ProductSearchTest` | Data-driven search terms, no-result search |

## Project structure

```
src/main/java/com/qa/framework/
├── config/        Config (properties → env vars → -D flags)
├── driver/        DriverFactory (ThreadLocal, Chrome/Edge/Firefox, headless)
├── pages/         Page objects + components/Header
├── data/          JSON and Google Sheets data sources, CustomerFactory (Datafaker)
├── sheets/        GoogleSheetsClient (service account)
├── api/           AutomationExerciseApi (account setup/cleanup)
├── reporting/     ExtentManager, ResultRecorder (CSV + Sheets)
└── models/        Customer, PurchaseScenario, CartItem, ...
src/test/java/com/qa/tests/
├── base/          BaseTest (browser per test)
├── listeners/     TestListener (report, screenshots, results)
└── *Test.java
src/test/resources/
├── config.properties
├── testdata/purchases.json
└── suites/        regression.xml, smoke.xml
```

## Running

Requires Java 17+ and Maven (bundled with IntelliJ). Chrome must be installed; the driver is fetched automatically.

```bash
mvn test                              # full regression, headless Chrome
mvn test -Dsuite=smoke                # smoke group only
mvn test -Dbrowser=firefox            # chrome | edge | firefox
mvn test -Dheadless=false             # watch the browser
mvn test -Dtest=PurchaseFlowTest      # a single class
```

After a run:

- HTML report: `target/extent-report/index.html`
- Results log: `target/test-results.csv`

## Google Sheets (optional)

Set `GOOGLE_SHEET_ID` and a service-account key, and the purchase test reads its rows from the sheet and writes results back. Without them, everything runs from the JSON file. Setup takes about five minutes: see [docs/google-sheets-setup.md](docs/google-sheets-setup.md).

No credentials are stored in this repository; they are read from environment variables or CI secrets only.

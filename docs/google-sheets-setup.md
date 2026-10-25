# Google Sheets setup

The framework can read purchase test data from a Google Sheet and append each run's results to it.
This is optional: without it, tests read `src/test/resources/testdata/purchases.json` and results go to `target/test-results.csv`.

It uses a **service account**, not your personal Google login. A service account can only touch sheets you explicitly share with it, and its key can be revoked at any time.

## 1. Create the service account (one time)

1. Open [Google Cloud Console](https://console.cloud.google.com/) and create (or pick) a project.
2. **APIs & Services → Library** → enable **Google Sheets API**.
3. **APIs & Services → Credentials → Create credentials → Service account**. Any name; no roles needed.
4. Open the new service account → **Keys → Add key → Create new key → JSON**. A `.json` file downloads.
5. Note the service account's email (looks like `name@project-id.iam.gserviceaccount.com`).

> Keep the key file **outside** the project folder. The `.gitignore` blocks common key file names, but the safest key is one that is never inside the repo.

## 2. Create the spreadsheet

1. Create a Google Sheet with two tabs named exactly **TestData** and **Results**.
2. Import `docs/sheets-testdata-template.csv` into **TestData** (File → Import → Upload → Replace current sheet).
3. Leave **Results** empty, or add a header row: `Run ID, Test, Data, Browser, Status, Duration (ms), Message`.
4. **Share** the sheet with the service account email as **Editor**.
5. Copy the sheet id from its URL: `https://docs.google.com/spreadsheets/d/<THIS-PART>/edit`.

`run` column: `Y` / blank runs the row, `N` skips it.

## 3. Run locally

```bash
export GOOGLE_SHEET_ID=your-sheet-id
export GOOGLE_SERVICE_ACCOUNT_FILE=$HOME/keys/selenium-sheets.json
mvn test
```

The console prints `[data] Purchase scenarios from Google Sheets (TestData!A1:E)` and, at the end, `[results] Appended N rows to Google Sheets`.

Force a source with `-Ddata.source=json` or `-Ddata.source=sheets`.

## 4. Run in GitHub Actions

Repo **Settings → Secrets and variables → Actions → New repository secret**:

| Name | Value |
| --- | --- |
| `GOOGLE_SHEET_ID` | the sheet id |
| `GOOGLE_SERVICE_ACCOUNT_JSON` | the **entire contents** of the key JSON file |

If the secrets are absent, CI falls back to the JSON test data.

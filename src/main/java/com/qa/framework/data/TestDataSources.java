package com.qa.framework.data;

import com.qa.framework.config.Config;
import com.qa.framework.sheets.GoogleSheetsClient;

/**
 * Picks the data source from the data.source setting:
 *   json   always the JSON file
 *   sheets always Google Sheets (fails clearly if it is not configured)
 *   auto   Google Sheets when configured, otherwise JSON (default)
 */
public final class TestDataSources {

    private TestDataSources() {
    }

    public static TestDataSource current() {
        String mode = Config.get("data.source").toLowerCase();
        return switch (mode) {
            case "json" -> json();
            case "sheets" -> sheets();
            case "auto" -> GoogleSheetsClient.isConfigured() ? sheets() : json();
            default -> throw new IllegalArgumentException("data.source must be auto, json or sheets, not '" + mode + "'");
        };
    }

    private static TestDataSource json() {
        return new JsonTestDataSource(Config.get("data.json.path"));
    }

    private static TestDataSource sheets() {
        GoogleSheetsClient client = GoogleSheetsClient.fromEnvironment().orElseThrow(() -> new IllegalStateException(
                "data.source=sheets but GOOGLE_SHEET_ID and GOOGLE_SERVICE_ACCOUNT_JSON/_FILE are not set"));
        return new SheetsTestDataSource(client, Config.get("sheets.data.range"));
    }
}

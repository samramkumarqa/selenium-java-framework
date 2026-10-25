package com.qa.framework.sheets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

/**
 * Reads and appends rows in one Google Sheet using a service account.
 *
 * Configuration comes only from environment variables, so nothing secret is ever committed:
 *   GOOGLE_SHEET_ID              the spreadsheet id from its URL
 *   GOOGLE_SERVICE_ACCOUNT_JSON  the service-account key JSON itself (used in CI), or
 *   GOOGLE_SERVICE_ACCOUNT_FILE  a path to the key file (handy locally; keep it outside the repo)
 *
 * The spreadsheet must be shared with the service account's email as an Editor.
 */
public final class GoogleSheetsClient {

    private static final String APP_NAME = "selenium-java-framework";

    private final Sheets sheets;
    private final String spreadsheetId;

    private GoogleSheetsClient(Sheets sheets, String spreadsheetId) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
    }

    /** Returns a client when Sheets is configured, or empty when it is not. */
    public static Optional<GoogleSheetsClient> fromEnvironment() {
        String sheetId = System.getenv("GOOGLE_SHEET_ID");
        String keyJson = System.getenv("GOOGLE_SERVICE_ACCOUNT_JSON");
        String keyFile = System.getenv("GOOGLE_SERVICE_ACCOUNT_FILE");

        if (isBlank(sheetId) || (isBlank(keyJson) && isBlank(keyFile))) {
            return Optional.empty();
        }

        try (InputStream key = isBlank(keyJson)
                ? new FileInputStream(keyFile)
                : new ByteArrayInputStream(keyJson.getBytes(StandardCharsets.UTF_8))) {

            GoogleCredentials credentials = GoogleCredentials.fromStream(key)
                    .createScoped(List.of(SheetsScopes.SPREADSHEETS));

            Sheets service = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APP_NAME)
                    .build();

            return Optional.of(new GoogleSheetsClient(service, sheetId.trim()));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load Google service-account credentials", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not create HTTPS transport for Google Sheets", e);
        }
    }

    public static boolean isConfigured() {
        return !isBlank(System.getenv("GOOGLE_SHEET_ID"))
                && (!isBlank(System.getenv("GOOGLE_SERVICE_ACCOUNT_JSON"))
                || !isBlank(System.getenv("GOOGLE_SERVICE_ACCOUNT_FILE")));
    }

    /** Reads a range such as "TestData!A1:E". Returns an empty list for an empty range. */
    public List<List<Object>> read(String range) {
        try {
            ValueRange response = sheets.spreadsheets().values().get(spreadsheetId, range).execute();
            List<List<Object>> values = response.getValues();
            return values == null ? List.of() : values;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read Google Sheet range " + range, e);
        }
    }

    /** Appends rows after the last filled row of the given range, e.g. "Results!A1". */
    public void append(String range, List<List<Object>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        try {
            sheets.spreadsheets().values()
                    .append(spreadsheetId, range, new ValueRange().setValues(rows))
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not append to Google Sheet range " + range, e);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}

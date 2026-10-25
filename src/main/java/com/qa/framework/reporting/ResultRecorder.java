package com.qa.framework.reporting;

import com.qa.framework.config.Config;
import com.qa.framework.models.TestResultRecord;
import com.qa.framework.sheets.GoogleSheetsClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collects one record per finished test, then at the end of the run:
 *  - always writes target/test-results.csv
 *  - also appends the rows to the Google Sheet "Results" tab when Sheets is configured
 */
public final class ResultRecorder {

    public static final Path CSV_PATH = Path.of("target", "test-results.csv");
    private static final List<TestResultRecord> RECORDS = Collections.synchronizedList(new ArrayList<>());

    private ResultRecorder() {
    }

    public static void record(TestResultRecord result) {
        RECORDS.add(result);
    }

    public static void publish() {
        List<TestResultRecord> snapshot;
        synchronized (RECORDS) {
            snapshot = List.copyOf(RECORDS);
        }
        writeCsv(snapshot);
        writeToSheets(snapshot);
    }

    private static void writeCsv(List<TestResultRecord> records) {
        List<String> lines = Stream.concat(
                Stream.of(csvLine(new ArrayList<>(TestResultRecord.headers()))),
                records.stream().map(r -> csvLine(r.toRow()))).toList();
        try {
            Files.createDirectories(CSV_PATH.getParent());
            Files.write(CSV_PATH, lines);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + CSV_PATH, e);
        }
    }

    private static void writeToSheets(List<TestResultRecord> records) {
        GoogleSheetsClient.fromEnvironment().ifPresent(client -> {
            try {
                List<List<Object>> rows = records.stream().map(TestResultRecord::toRow).toList();
                client.append(Config.get("sheets.results.range"), rows);
                System.out.println("[results] Appended " + rows.size() + " rows to Google Sheets");
            } catch (RuntimeException e) {
                // Reporting must never fail the build; the CSV is still there.
                System.err.println("[results] Could not write to Google Sheets: " + e.getMessage());
            }
        });
    }

    private static String csvLine(List<?> values) {
        return values.stream()
                .map(v -> String.valueOf(v).replace("\"", "\"\""))
                .map(v -> "\"" + v + "\"")
                .collect(Collectors.joining(","));
    }
}

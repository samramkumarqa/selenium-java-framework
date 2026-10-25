package com.qa.framework.models;

import java.util.List;

/** One finished test, written to the local CSV and (optionally) the Google Sheets results tab. */
public record TestResultRecord(
        String runId,
        String testName,
        String dataId,
        String browser,
        String status,
        long durationMs,
        String message) {

    public static List<String> headers() {
        return List.of("Run ID", "Test", "Data", "Browser", "Status", "Duration (ms)", "Message");
    }

    public List<Object> toRow() {
        return List.of(runId, testName, dataId, browser, status, durationMs, message);
    }
}

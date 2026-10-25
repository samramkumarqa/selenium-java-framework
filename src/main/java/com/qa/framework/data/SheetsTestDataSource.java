package com.qa.framework.data;

import com.qa.framework.models.PurchaseScenario;
import com.qa.framework.sheets.GoogleSheetsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Reads purchase scenarios from a Google Sheet tab.
 * The first row is a header; columns are matched by name, so their order does not matter:
 * id | searchTerm | productName | quantity | run
 */
public class SheetsTestDataSource implements TestDataSource {

    private final GoogleSheetsClient client;
    private final String range;

    public SheetsTestDataSource(GoogleSheetsClient client, String range) {
        this.client = client;
        this.range = range;
    }

    @Override
    public String name() {
        return "Google Sheets (" + range + ")";
    }

    @Override
    public List<PurchaseScenario> purchaseScenarios() {
        List<List<Object>> values = client.read(range);
        if (values.size() < 2) {
            throw new IllegalStateException("Sheet range " + range + " needs a header row and at least one data row");
        }

        List<String> header = values.get(0).stream().map(v -> v.toString().trim()).toList();
        Map<String, Integer> column = IntStream.range(0, header.size()).boxed()
                .collect(Collectors.toMap(header::get, Function.identity(), (a, b) -> a));
        for (String required : List.of("id", "searchTerm", "productName", "quantity")) {
            if (!column.containsKey(required)) {
                throw new IllegalStateException("Sheet header is missing column '" + required + "'. Found: " + header);
            }
        }

        List<PurchaseScenario> scenarios = new ArrayList<>();
        for (List<Object> row : values.subList(1, values.size())) {
            String id = cell(row, column.get("id"));
            if (id.isEmpty()) {
                continue; // skip blank lines
            }
            scenarios.add(new PurchaseScenario(
                    id,
                    cell(row, column.get("searchTerm")),
                    cell(row, column.get("productName")),
                    Integer.parseInt(cell(row, column.get("quantity"))),
                    parseRun(column.containsKey("run") ? cell(row, column.get("run")) : "")));
        }
        return scenarios;
    }

    /** Blank, Y, YES and TRUE all mean run; N, NO and FALSE mean skip. */
    private static boolean parseRun(String value) {
        return value.isEmpty() || List.of("y", "yes", "true").contains(value.toLowerCase());
    }

    /** Sheets omits trailing empty cells, so a short row just means blanks. */
    private static String cell(List<Object> row, int index) {
        return index < row.size() && row.get(index) != null ? row.get(index).toString().trim() : "";
    }
}

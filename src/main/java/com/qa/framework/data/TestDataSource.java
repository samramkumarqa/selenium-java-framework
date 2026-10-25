package com.qa.framework.data;

import com.qa.framework.models.PurchaseScenario;

import java.util.List;

/** Where purchase scenarios come from. Implementations: JSON file or Google Sheets. */
public interface TestDataSource {

    String name();

    /** All rows, including those with run=false. */
    List<PurchaseScenario> purchaseScenarios();

    /** Only the rows marked to run. */
    default List<PurchaseScenario> enabledPurchaseScenarios() {
        return purchaseScenarios().stream().filter(PurchaseScenario::run).toList();
    }
}

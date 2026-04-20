package com.example.toolinsert.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.import")
public class ImportProperties {

    private boolean skipBlankRows = true;
    private int maxRows = 10_000;
    private int batchSize = 200;

    public boolean isSkipBlankRows() {
        return skipBlankRows;
    }

    public void setSkipBlankRows(boolean skipBlankRows) {
        this.skipBlankRows = skipBlankRows;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}

package com.crypto.analysis.enums;


public enum TableNameEnum {
    RVN("ravencoin", "Ravencoin - rvn"),
    ONE("harmony", "Harmony - one"),
    BTC("bitcoin", "Bitcoin - btc"),
    REEF("reef", "Reef - reef"),
    NEAR("near", "NEAR Protocol - near");

    private final String tableName;
    private final String description;

    TableNameEnum(String tableName, String description) {
        this.tableName = tableName;
        this.description = description;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDescription() {
        return description;
    }
}
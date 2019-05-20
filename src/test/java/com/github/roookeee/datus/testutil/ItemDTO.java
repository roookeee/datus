package com.github.roookeee.datus.testutil;

public class ItemDTO {
    private final String id;
    private final String extendedId;

    public ItemDTO(String id, String extendedId) {
        this.id = id;
        this.extendedId = extendedId;
    }

    public String getId() {
        return id;
    }

    public String getExtendedId() {
        return extendedId;
    }
}

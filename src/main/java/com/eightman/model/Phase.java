package com.eightman.model;

public enum Phase {
    ANALYSIS("Analyse"),
    CONSENSUS("Konsensbildung"),
    DISSENT("Dissens (Achter Mann)"),
    SYNTHESIS("Synthese");

    private final String displayName;

    Phase(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

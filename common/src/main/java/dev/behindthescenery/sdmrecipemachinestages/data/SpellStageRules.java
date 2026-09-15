package dev.behindthescenery.sdmrecipemachinestages.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/** Immutable snapshots allow the integrated server and client to read rules safely. */
public final class SpellStageRules {
    private volatile Map<String, String> rules = Map.of();

    public synchronized void register(String spellId, String stage) {
        Objects.requireNonNull(spellId, "spellId");
        if (stage == null || stage.isBlank()) throw new IllegalArgumentException("Stage must not be blank");
        var updated = new HashMap<>(rules);
        updated.putIfAbsent(spellId, stage);
        rules = Map.copyOf(updated);
    }

    public void replace(Map<String, String> snapshot) {
        rules = Map.copyOf(snapshot);
    }

    public Map<String, String> snapshot() {
        return rules;
    }

    public boolean isUnlocked(String spellId, Predicate<String> hasStage) {
        String stage = rules.get(spellId);
        return stage == null || hasStage.test(stage);
    }
}

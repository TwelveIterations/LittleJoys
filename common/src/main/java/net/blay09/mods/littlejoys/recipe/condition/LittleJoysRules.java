package net.blay09.mods.littlejoys.recipe.condition;

import net.blay09.mods.shogi.Shogi;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.shogi.scope.ShogiScope;

import java.util.List;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public final class LittleJoysRules {
    public static final ShogiScope EVENT_CONDITIONS = Shogi.scope(id("event_conditions"), scope -> {
        scope.setDefaultNamespaces(List.of("littlejoys", "shogi"));
    });
    public static final ShogiEffect<Boolean> UNSYNCED_EVENT_CONDITION = ShogiEffect.simple(id("unsynced_event_condition"), () -> false);

    private LittleJoysRules() {
    }

    public static void initialize() {
        EVENT_CONDITIONS.identifier();
    }
}

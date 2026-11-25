package net.blay09.mods.littlejoys.registry.condition;

import net.blay09.mods.shogi.Shogi;
import net.blay09.mods.shogi.scope.ShogiScope;

import java.util.List;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class LittleJoysRules {
    public static final ShogiScope SCOPE = Shogi.scope(id("rules"), scope -> {
        scope.registerEffect(CanSpawnDigSpot.IDENTIFIER, CanSpawnDigSpot.MAP_CODEC);
        scope.registerEffect(CanSpawnFishingSpot.IDENTIFIER, CanSpawnFishingSpot.MAP_CODEC);
        scope.registerEffect(CanTriggerGoldRush.IDENTIFIER, CanTriggerGoldRush.MAP_CODEC);
        scope.registerEffect(CanTriggerDropRush.IDENTIFIER, CanTriggerDropRush.MAP_CODEC);
        scope.setDefaultNamespaces(List.of("littlejoys", "shogi"));
    });
}

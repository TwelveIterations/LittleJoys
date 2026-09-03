package net.blay09.mods.littlejoys.registry.condition;

import net.blay09.mods.shogi.Shogi;
import net.blay09.mods.shogi.scope.ShogiScope;

import java.util.List;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class LittleJoysRules {
    public static final ShogiScope SCOPE = Shogi.scope(id("rules"), scope -> {
        scope.setDefaultNamespaces(List.of("littlejoys", "shogi"));
    });
}

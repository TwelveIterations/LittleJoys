package net.blay09.mods.littlejoys.blessing;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class Blessing {

    private final Identifier identifier;
    private final Supplier<Holder<MobEffect>> effect;
    private final int defaultUses;

    public Blessing(Identifier identifier, Supplier<Holder<MobEffect>> effect, int defaultUses) {
        this.identifier = identifier;
        this.effect = effect;
        this.defaultUses = defaultUses;
    }

    public Identifier identifier() {
        return identifier;
    }

    public Holder<MobEffect> effect() {
        return effect.get();
    }

    public int defaultUses() {
        return defaultUses;
    }

    @Override
    public String toString() {
        return identifier.toString();
    }
}

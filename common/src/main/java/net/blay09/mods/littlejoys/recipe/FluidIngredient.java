package net.blay09.mods.littlejoys.recipe;

import com.google.gson.*;
import com.mojang.serialization.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record FluidIngredient(Value[] values) {
    public static final FluidIngredient WATER = new FluidIngredient(new Value[]{new TagValue(FluidTags.WATER)});
    public static final Codec<FluidIngredient> CODEC = MapCodec.unitCodec(WATER);

    public boolean test(FluidState state) {
        for (final var value : values) {
            if (value.is(state)) {
                return true;
            }
        }

        return false;
    }

    public interface Value {
        boolean is(FluidState state);

        Collection<Fluid> getFluids();
    }

    public record FluidValue(Fluid fluid) implements Value {
        @Override
        public boolean is(FluidState state) {
            return state.is(fluid);
        }

        @Override
        public Collection<Fluid> getFluids() {
            return List.of(fluid);
        }
    }

    public record TagValue(TagKey<Fluid> tag) implements Value {
        @Override
        public boolean is(FluidState state) {
            return state.is(tag);
        }

        @Override
        public Collection<Fluid> getFluids() {
            final var result = new ArrayList<Fluid>();
            for (final var holder : BuiltInRegistries.FLUID.getTagOrEmpty(tag)) {
                result.add(holder.value());
            }
            return result;
        }
    }

}

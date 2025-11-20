package net.blay09.mods.littlejoys.recipe;

import com.google.gson.*;
import com.mojang.serialization.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public Fluid[] getFluids() {
        return Arrays.stream(values).flatMap((value) -> value.getFluids().stream()).distinct().toArray(Fluid[]::new);
    }

    public static FluidIngredient fromJson(@Nullable JsonElement json, boolean canBeEmpty) {
        if (json != null && !json.isJsonNull()) {
            if (json.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(json.getAsJsonObject())));
            } else if (json.isJsonArray()) {
                JsonArray jsonarray = json.getAsJsonArray();
                if (jsonarray.isEmpty() && !canBeEmpty) {
                    throw new JsonSyntaxException("Fluid array cannot be empty, at least one fluid must be defined");
                } else {
                    return fromValues(StreamSupport.stream(jsonarray.spliterator(), false)
                            .map((it) -> valueFromJson(GsonHelper.convertToJsonObject(it, "fluid"))));
                }
            } else {
                throw new JsonSyntaxException("Expected fluid to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Fluid cannot be null");
        }
    }

    private static FluidIngredient fromValues(Stream<? extends Value> stream) {
        return new FluidIngredient(stream.toArray(Value[]::new));
    }

    private static Value valueFromJson(JsonObject json) {
        if (json.has("fluid") && json.has("tag")) {
            throw new JsonParseException("A fluid ingredient entry is either a tag or a fluid, not both");
        } else if (json.has("fluid")) {
            final var fluid = BuiltInRegistries.FLUID.getValue(Identifier.parse(GsonHelper.getAsString(json, "fluid")));
            return new FluidValue(fluid);
        } else if (json.has("tag")) {
            final var tag = TagKey.create(Registries.FLUID, Identifier.parse(GsonHelper.getAsString(json, "tag")));
            return new TagValue(tag);
        } else {
            throw new JsonParseException("A fluid ingredient entry needs either a tag or a fluid");
        }
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf buffer) {
        return fromValues(buffer.readList(FriendlyByteBuf::readIdentifier)
                .stream().map(BuiltInRegistries.FLUID::getValue).map(FluidValue::new));
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeCollection(Arrays.asList(getFluids()), (buf, fluid) -> buf.writeIdentifier(BuiltInRegistries.FLUID.getKey(fluid)));
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

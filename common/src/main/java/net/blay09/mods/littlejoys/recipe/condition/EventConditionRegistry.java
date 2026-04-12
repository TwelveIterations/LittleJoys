package net.blay09.mods.littlejoys.recipe.condition;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EventConditionRegistry {
    private static final BiMap<ResourceLocation, Class<? extends EventCondition>> TYPES_BY_CLASS = HashBiMap.create();
    private static final BiMap<ResourceLocation, EventConditionType<? extends EventCondition>> TYPES = HashBiMap.create();

    public static final Codec<EventConditionType<? extends EventCondition>> BY_NAME_CODEC = byNameCodec();
    public static final Codec<EventCondition> CODEC = BY_NAME_CODEC.dispatch(it -> getType(it.getClass()), EventConditionType::mapCodec);
    public static final Codec<List<EventCondition>> LIST_CODEC = CODEC.listOf();

    public record EventConditionType<T extends EventCondition>(ResourceLocation identifier, MapCodec<T> mapCodec,
                                                               Function<RegistryFriendlyByteBuf, ? extends EventCondition> networkDeserializer) {
        public Codec<T> codec() {
            return mapCodec.codec();
        }
    }

    private static Codec<EventConditionType<? extends EventCondition>> byNameCodec() {
        return ResourceLocation.CODEC.flatXmap((identifier) -> Optional.ofNullable(getType(identifier))
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Unknown event condition: " + identifier)), (type) -> DataResult.success(type.identifier));
    }

    public static <T extends EventCondition> void registerCondition(ResourceLocation identifier, Class<T> clazz, MapCodec<T> codec, Function<RegistryFriendlyByteBuf, T> networkDeserializer) {
        if (TYPES_BY_CLASS.containsKey(identifier)) {
            throw new IllegalArgumentException("Condition with identifier " + identifier + " is already registered");
        }

        TYPES.put(identifier, new EventConditionType<>(identifier, codec, networkDeserializer));
        TYPES_BY_CLASS.put(identifier, clazz);
    }

    public static ResourceLocation getIdentifier(Class<? extends EventCondition> conditionClass) {
        return TYPES_BY_CLASS.inverse().get(conditionClass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends EventCondition> EventConditionType<T> getType(ResourceLocation identifier) {
        return (EventConditionType<T>) TYPES.get(identifier);
    }

    public static <T extends EventCondition> EventConditionType<T> getType(Class<T> clazz) {
        return getType(getIdentifier(clazz));
    }

    public static EventCondition conditionFromNetwork(RegistryFriendlyByteBuf buf) {
        final var identifier = buf.readResourceLocation();
        final var type = getType(identifier);
        if (type == null) {
            throw new IllegalArgumentException("Unknown event condition " + identifier);
        }
        return type.networkDeserializer.apply(buf);
    }

    public static void conditionToNetwork(RegistryFriendlyByteBuf buf, EventCondition condition) {
        final var identifier = getIdentifier(condition.getClass());
        if (identifier == null) {
            throw new IllegalArgumentException("Event condition " + condition.getClass() + " is not registered");
        }
        buf.writeResourceLocation(identifier);
        condition.toNetwork(buf);
    }
}

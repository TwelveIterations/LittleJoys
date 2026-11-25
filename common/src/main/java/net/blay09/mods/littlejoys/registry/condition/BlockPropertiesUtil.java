package net.blay09.mods.littlejoys.registry.condition;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;

public class BlockPropertiesUtil {
    public static boolean matches(BlockState state, Map<String, String> properties) {
        return properties.entrySet().stream().allMatch(entry -> {
            final var property = state.getBlock().getStateDefinition().getProperty(entry.getKey());
            return property != null && matches(state, property, entry.getValue());
        });
    }

    private static <T extends Comparable<T>> boolean matches(BlockState state, Property<T> property, String value) {
        return property.getName(state.getValue(property)).equals(value);
    }
}

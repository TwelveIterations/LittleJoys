package net.blay09.mods.littlejoys.entity;

import net.blay09.mods.balm.world.entity.BalmEntityTypeRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

public class ModEntities {
    public static Holder<EntityType<DropRushItemEntity>> dropRushItem;
    public static Holder<EntityType<FallenStarEntity>> fallenStar;

    public static void initialize(BalmEntityTypeRegistrar entities) {
        dropRushItem = entities.register(
                "drop_rush_item",
                () -> EntityType.Builder.of((EntityType<DropRushItemEntity> type, Level level) -> new DropRushItemEntity(type, level), MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(6)
                        .updateInterval(20)
                        .noSave()).asHolder();
        fallenStar = entities.register(
                "fallen_star",
                () -> EntityType.Builder.of((EntityType<FallenStarEntity> type, Level level) -> new FallenStarEntity(type, level), MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(8)
                        .updateInterval(20)).asHolder();
    }

}

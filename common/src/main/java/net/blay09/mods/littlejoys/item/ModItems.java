package net.blay09.mods.littlejoys.item;

import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.minecraft.world.item.Item;

public class ModItems {

    public static DeferredItem fallenStar;

    public static void initialize(BalmItemRegistrar items) {
        fallenStar = items.register("fallen_star", Item::new).asDeferredItem();
    }
}

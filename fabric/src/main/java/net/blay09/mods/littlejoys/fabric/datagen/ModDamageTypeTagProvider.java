package net.blay09.mods.littlejoys.fabric.datagen;

import net.blay09.mods.littlejoys.tag.ModDamageTypeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagProvider extends FabricTagsProvider<DamageType> {
    public ModDamageTypeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(ModDamageTypeTags.PREVENTED_BY_STAR_OF_SERENITY)
                .add(DamageTypes.SWEET_BERRY_BUSH)
                .add(DamageTypes.CACTUS);
    }
}

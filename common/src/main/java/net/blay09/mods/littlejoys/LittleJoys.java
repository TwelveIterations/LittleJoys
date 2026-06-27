package net.blay09.mods.littlejoys;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.littlejoys.api.LittleJoysAPI;
import net.blay09.mods.littlejoys.blessing.Blessings;
import net.blay09.mods.littlejoys.blessing.ModMobEffects;
import net.blay09.mods.littlejoys.blessing.StarOfProsperity;
import net.blay09.mods.littlejoys.blessing.StarOfSerenity;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.blay09.mods.littlejoys.block.entity.ModBlockEntities;
import net.blay09.mods.littlejoys.command.LittleJoysCommand;
import net.blay09.mods.littlejoys.entity.ModEntities;
import net.blay09.mods.littlejoys.handler.DigSpotHandler;
import net.blay09.mods.littlejoys.handler.DropRushHandler;
import net.blay09.mods.littlejoys.handler.FallenStarHandler;
import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.blay09.mods.littlejoys.handler.GoldRushHandler;
import net.blay09.mods.littlejoys.item.ModItems;
import net.blay09.mods.littlejoys.loot.ModLoot;
import net.blay09.mods.littlejoys.network.ModNetworking;
import net.blay09.mods.littlejoys.particle.ModParticles;
import net.blay09.mods.littlejoys.poi.ModPoiTypes;
import net.blay09.mods.littlejoys.recipe.ModRecipeTypes;
import net.blay09.mods.littlejoys.recipe.condition.LittleJoysRules;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.blay09.mods.littlejoys.stats.ModStats;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LittleJoys {

    public static final Logger logger = LoggerFactory.getLogger(LittleJoys.class);

    public static final String MOD_ID = "littlejoys";

    public static void initialize(BalmRegistrars registrars) {
        LittleJoysAPI.__setupAPI(new InternalMethodsImpl());
        LittleJoysRules.initialize();

        LittleJoysConfig.initialize();
        registrars.blocks(ModBlocks::initialize);
        registrars.items(ModItems::initialize);
        registrars.blockEntityTypes(ModBlockEntities::initialize);
        registrars.entityTypes(ModEntities::initialize);
        ModLoot.initialize(Balm.lootModifiers());
        registrars.recipeTypes(ModRecipeTypes::initialize);
        registrars.customStats(ModStats::initialize);
        ModNetworking.initialize(Balm.networking());
        Blessings.initialize(registrars.registrar());
        registrars.registrar(Registries.SOUND_EVENT, ModSounds::initialize);
        registrars.registrar(Registries.MOB_EFFECT, ModMobEffects::initialize);
        registrars.particleTypes(ModParticles::initialize);
        registrars.poiTypes(ModPoiTypes::initialize);
        Balm.commands().register(LittleJoysCommand::register);

        StarOfProsperity.initialize();
        StarOfSerenity.initialize();
        DropRushHandler.initialize();
        GoldRushHandler.initialize();
        DigSpotHandler.initialize();
        FishingSpotHandler.initialize();
        FallenStarHandler.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

}

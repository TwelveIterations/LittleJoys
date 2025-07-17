package net.blay09.mods.littlejoys;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.littlejoys.api.LittleJoysAPI;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.blay09.mods.littlejoys.block.entity.ModBlockEntities;
import net.blay09.mods.littlejoys.command.LittleJoysCommand;
import net.blay09.mods.littlejoys.entity.ModEntities;
import net.blay09.mods.littlejoys.handler.DigSpotHandler;
import net.blay09.mods.littlejoys.handler.DropRushHandler;
import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.blay09.mods.littlejoys.handler.GoldRushHandler;
import net.blay09.mods.littlejoys.loot.ModLoot;
import net.blay09.mods.littlejoys.network.ModNetworking;
import net.blay09.mods.littlejoys.particle.ModParticles;
import net.blay09.mods.littlejoys.poi.ModPoiTypes;
import net.blay09.mods.littlejoys.recipe.ModRecipeTypes;
import net.blay09.mods.littlejoys.recipe.condition.*;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.blay09.mods.littlejoys.stats.ModStats;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LittleJoys {

    public static final Logger logger = LoggerFactory.getLogger(LittleJoys.class);

    public static final String MOD_ID = "littlejoys";

    public static void initialize() {
        LittleJoysAPI.__setupAPI(new InternalMethodsImpl());
        LittleJoysAPI.registerEventCondition(new ResourceLocation("above_fluid_source"),
                AboveFluidSourceCondition.class,
                AboveFluidSourceCondition.CODEC,
                AboveFluidSourceCondition::fromNetwork);
        LittleJoysAPI.registerEventCondition(new ResourceLocation("above_state"),
                AboveStateCondition.class,
                AboveStateCondition.CODEC,
                AboveStateCondition::fromNetwork);
        LittleJoysAPI.registerEventCondition(new ResourceLocation("is_state"), IsStateCondition.class, IsStateCondition.CODEC, IsStateCondition::fromNetwork);
        LittleJoysAPI.registerEventCondition(new ResourceLocation("all"), AndCondition.class, AndCondition.CODEC, AndCondition::fromNetwork);
        LittleJoysAPI.registerEventCondition(new ResourceLocation("any"), AnyCondition.class, AnyCondition.CODEC, AnyCondition::fromNetwork);
        LittleJoysAPI.registerEventCondition(new ResourceLocation("always"), AlwaysCondition.class, AlwaysCondition.CODEC, AlwaysCondition::fromNetwork);
        LittleJoysAPI.registerEventCondition(new ResourceLocation("can_see_sky"),
                CanSeeSkyCondition.class,
                CanSeeSkyCondition.CODEC,
                CanSeeSkyCondition::fromNetwork);
        LittleJoysAPI.registerEventCondition(new ResourceLocation("is_dimension"),
                IsDimensionCondition.class,
                IsDimensionCondition.CODEC,
                IsDimensionCondition::fromNetwork);

        LittleJoysConfig.initialize();
        ModBlocks.initialize(Balm.getBlocks());
        ModBlockEntities.initialize(Balm.getBlockEntities());
        ModEntities.initialize(Balm.getEntities());
        ModLoot.initialize(Balm.getLootTables());
        ModRecipeTypes.initialize(Balm.getRecipes());
        ModStats.initialize(Balm.getStats());
        ModNetworking.initialize(Balm.getNetworking());
        ModSounds.initialize(Balm.getSounds());
        ModParticles.initialize(Balm.getParticles());
        ModPoiTypes.initialize(Balm.getWorldGen());
        Balm.getCommands().register(LittleJoysCommand::register);

        DropRushHandler.initialize();
        GoldRushHandler.initialize();
        DigSpotHandler.initialize();
        FishingSpotHandler.initialize();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}

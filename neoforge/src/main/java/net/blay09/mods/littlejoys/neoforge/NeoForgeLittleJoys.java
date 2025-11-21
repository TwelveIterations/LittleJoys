package net.blay09.mods.littlejoys.neoforge;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.littlejoys.LittleJoys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(LittleJoys.MOD_ID)
public class NeoForgeLittleJoys {

    public NeoForgeLittleJoys(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        Balm.initializeMod(LittleJoys.MOD_ID, context, LittleJoys::initialize);

        Balm.initializeIfLoaded("fishingoverhaul", "net.blay09.mods.littlejoys.neoforge.compat.FishingOverhaulSupport");
    }
}

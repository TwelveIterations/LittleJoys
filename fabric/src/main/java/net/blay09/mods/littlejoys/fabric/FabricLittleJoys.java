package net.blay09.mods.littlejoys.fabric;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ModInitializer;
import net.blay09.mods.littlejoys.LittleJoys;

public class FabricLittleJoys implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(LittleJoys.MOD_ID, FabricLoadContext.INSTANCE, LittleJoys::initialize);
    }
}

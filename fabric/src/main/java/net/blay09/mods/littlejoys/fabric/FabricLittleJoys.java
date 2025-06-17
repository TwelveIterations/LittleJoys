package net.blay09.mods.littlejoys.fabric;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.fabricmc.api.ModInitializer;
import net.blay09.mods.littlejoys.LittleJoys;

public class FabricLittleJoys implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(LittleJoys.MOD_ID, EmptyLoadContext.INSTANCE, LittleJoys::initialize);
    }
}

package com.cesarmalari.orefinder;

import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = OreFinderMod.MODID, version = OreFinderMod.VERSION)
public class OreFinderMod
{
    public static final String MODID = "orefinder";
    public static final String VERSION = "0.1";
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new FindOreCommand());
    }
}

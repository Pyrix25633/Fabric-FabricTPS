package net.rupyber_studios.fabric_tps;

import net.fabricmc.api.ModInitializer;

import net.rupyber_studios.fabric_tps.util.ModRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricTPS implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("fabric_tps");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModRegistries.registerCommands();

		LOGGER.info("Hello Fabric world!");
	}
}
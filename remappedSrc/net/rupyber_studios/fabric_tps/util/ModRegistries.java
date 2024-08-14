package net.rupyber_studios.fabric_tps.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.rupyber_studios.fabric_tps.command.FabricTPSCommand;

public class ModRegistries {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(FabricTPSCommand::register);
    }
}
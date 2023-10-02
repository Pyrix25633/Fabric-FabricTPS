package net.rupyber_studios.fabric_tps.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;

import java.util.HashMap;

public class FabricTPSCommand {
    public static HashMap<String, Float> dimensionTickTimes = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("fabric")
                .then(CommandManager.literal("tps").executes(FabricTPSCommand::fabricTPS)));
        dispatcher.register(CommandManager.literal("fabric")
                .then(CommandManager.literal("tps").then(CommandManager.literal("fancy").executes(FabricTPSCommand::fabricTPSFancy))));
        dispatcher.register(CommandManager.literal("quilt")
                .then(CommandManager.literal("tps").executes(FabricTPSCommand::fabricTPS)));
        dispatcher.register(CommandManager.literal("quilt")
                .then(CommandManager.literal("tps").then(CommandManager.literal("fancy").executes(FabricTPSCommand::fabricTPSFancy))));
    }

    private static int fabricTPS(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = context.getSource().getServer();
        StringBuilder feedback = new StringBuilder();
        for(ServerWorld world : server.getWorlds()) {
            String key = world.getRegistryKey().getValue().toString();
            float mspt = dimensionTickTimes.get(key);
            float tps = 1000F / mspt;
            if(tps > 20F) tps = 20F;
            feedback.append("Dim ").append(key).append(" (").append(key).append("): Mean tick time: ")
                    .append(String.format("%.3f", mspt)).append(" ms. Mean TPS: ")
                    .append(String.format("%.3f", tps)).append("\n");
        }
        float mspt = server.getTickTime();
        float tps = 1000F / mspt;
        if(tps > 20F) tps = 20F;
        feedback.append("Overall: Mean tick time: ").append(String.format("%.3f", mspt))
                .append(" ms. Mean TPS: ").append(String.format("%.3f", tps));
        source.sendFeedback(new LiteralText(feedback.toString()), false);
        return 1;
    }

    private static int fabricTPSFancy(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = context.getSource().getServer();
        StringBuilder feedback = new StringBuilder();
        for(ServerWorld world : server.getWorlds()) {
            String key = world.getRegistryKey().getValue().toString();
            float mspt = dimensionTickTimes.get(key);
            float tps = 1000F / mspt;
            if(tps > 20F) tps = 20F;
            feedback.append("'").append(key).append("'").append(": ")
                    .append(String.format("%.3f", mspt)).append(" MSPT, ")
                    .append(String.format("%.3f", tps)).append(" TPS\n");
        }
        float mspt = server.getTickTime();
        float tps = 1000F / mspt;
        if(tps > 20F) tps = 20F;
        feedback.append("Overall: ").append(String.format("%.3f", mspt))
                .append(" MSPT, ").append(String.format("%.3f", tps)).append(" TPS");
        source.sendFeedback(new LiteralText(feedback.toString()), false);
        return 1;
    }
}
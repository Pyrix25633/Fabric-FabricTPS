package net.rupyber_studios.fabric_tps.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class FabricTPSCommand {
    public static HashMap<String, Float> dimensionTickTimes = new HashMap<>();
    public static HashMap<String, Float> dimensionTickDeltas = new HashMap<>();

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("fabric")
                .then(CommandManager.literal("tps").executes(FabricTPSCommand::fabricTPS)));
        dispatcher.register(CommandManager.literal("fabric")
                .then(CommandManager.literal("tps").then(CommandManager.literal("fancy").executes(FabricTPSCommand::fabricTPSFancy))));
        dispatcher.register(CommandManager.literal("quilt")
                .then(CommandManager.literal("tps").executes(FabricTPSCommand::fabricTPS)));
        dispatcher.register(CommandManager.literal("quilt")
                .then(CommandManager.literal("tps").then(CommandManager.literal("fancy").executes(FabricTPSCommand::fabricTPSFancy))));
    }

    private static int fabricTPS(@NotNull CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = context.getSource().getServer();
        StringBuilder feedback = new StringBuilder();
        double tpsSum = 0;
        int numberOfDimensions = 0;
        for(ServerWorld world : server.getWorlds()) {
            String key = world.getRegistryKey().getValue().toString();
            float mspt = dimensionTickTimes.get(key);
            float tps = 1000 / dimensionTickDeltas.get(key);
            tpsSum += tps;
            numberOfDimensions++;
            feedback.append("Dim ").append(key).append(" (").append(key).append("): Mean tick time: ")
                    .append(String.format("%.3f", mspt)).append(" ms. Mean TPS: ")
                    .append(String.format("%.1f", Math.round(tps * 10F) / 10F)).append("\n");
        }
        float mspt = server.getAverageTickTime();
        float tps = (float)tpsSum / numberOfDimensions;
        feedback.append("Overall: Mean tick time: ").append(String.format("%.3f", mspt))
                .append(" ms. Mean TPS: ").append(String.format("%.1f", Math.round(tps * 10F) / 10F));
        source.sendFeedback(() -> Text.literal(feedback.toString()), false);
        return 1;
    }

    private static int fabricTPSFancy(@NotNull CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = context.getSource().getServer();
        StringBuilder feedback = new StringBuilder();
        double tpsSum = 0;
        int numberOfDimensions = 0;
        for(ServerWorld world : server.getWorlds()) {
            String key = world.getRegistryKey().getValue().toString();
            float mspt = dimensionTickTimes.get(key);
            float tps = 1000 / dimensionTickDeltas.get(key);
            tpsSum += tps;
            numberOfDimensions++;
            feedback.append("'").append(key).append("'").append(": ")
                    .append(String.format("%.3f", mspt)).append(" MSPT, ")
                    .append(String.format("%.1f", Math.round(tps * 10F) / 10F)).append(" TPS\n");
        }
        float mspt = server.getAverageTickTime();
        float tps = (float)tpsSum / numberOfDimensions;
        feedback.append("Overall: ").append(String.format("%.3f", mspt))
                .append(" MSPT, ").append(String.format("%.1f", Math.round(tps * 10F) / 10F)).append(" TPS");
        source.sendFeedback(() -> Text.literal(feedback.toString()), false);
        return 1;
    }
}
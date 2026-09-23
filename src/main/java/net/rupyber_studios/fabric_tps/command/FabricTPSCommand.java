package net.rupyber_studios.fabric_tps.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FabricTPSCommand {
    public static final Map<String, Float> dimensionTickTimes = new HashMap<>();
    public static final Map<String, Float> dimensionTickDeltas = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fabric")
                .then(Commands.literal("tps").executes(FabricTPSCommand::fabricTPS)
                        .then(Commands.literal("fancy").executes(FabricTPSCommand::fabricTPSFancy))));
        dispatcher.register(Commands.literal("quilt")
                .then(Commands.literal("tps").executes(FabricTPSCommand::fabricTPS)
                        .then(Commands.literal("fancy").executes(FabricTPSCommand::fabricTPSFancy))));
    }

    private static int fabricTPS(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        StringBuilder feedback = new StringBuilder();
        double tpsSum = 0;
        int dimensionCount = 0;

        for (ServerLevel world : server.getAllLevels()) {
            String key = world.dimension().identifier().toString();
            float mspt = dimensionTickTimes.getOrDefault(key, 0F);
            float tps = calculateTps(dimensionTickDeltas.get(key));
            tpsSum += tps;
            dimensionCount++;
            feedback.append("Dim ").append(key).append(" (").append(key).append("): Mean tick time: ")
                    .append(format(mspt, 3)).append(" ms. Mean TPS: ")
                    .append(format(Math.round(tps * 10F) / 10F, 1)).append("\n");
        }

        float mspt = server.getAverageTickTimeNanos() / 1_000_000.0F;
        float tps = dimensionCount > 0 ? (float) tpsSum / dimensionCount : 20F;
        feedback.append("Overall: Mean tick time: ").append(format(mspt, 3))
                .append(" ms. Mean TPS: ").append(format(Math.round(tps * 10F) / 10F, 1));
        source.sendSuccess(() -> Component.literal(feedback.toString()), false);
        return 1;
    }

    private static int fabricTPSFancy(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        StringBuilder feedback = new StringBuilder();
        double tpsSum = 0;
        int dimensionCount = 0;

        for (ServerLevel world : server.getAllLevels()) {
            String key = world.dimension().identifier().toString();
            float mspt = dimensionTickTimes.getOrDefault(key, 0F);
            float tps = calculateTps(dimensionTickDeltas.get(key));
            tpsSum += tps;
            dimensionCount++;
            feedback.append("'").append(key).append("'").append(": ")
                    .append(format(mspt, 3)).append(" MSPT, ")
                    .append(format(Math.round(tps * 10F) / 10F, 1)).append(" TPS\n");
        }

        float mspt = server.getAverageTickTimeNanos() / 1_000_000.0F;
        float tps = dimensionCount > 0 ? (float) tpsSum / dimensionCount : 20F;
        feedback.append("Overall: ").append(format(mspt, 3))
                .append(" MSPT, ").append(format(Math.round(tps * 10F) / 10F, 1)).append(" TPS");
        source.sendSuccess(() -> Component.literal(feedback.toString()), false);
        return 1;
    }

    private static float calculateTps(Float tickDeltaMs) {
        if (tickDeltaMs == null || tickDeltaMs <= 0F) {
            return 20F;
        }
        return Math.min(1000F / tickDeltaMs, 20F);
    }

    private static String format(float value, int precision) {
        return String.format(Locale.ROOT, "%." + precision + "f", value);
    }
}

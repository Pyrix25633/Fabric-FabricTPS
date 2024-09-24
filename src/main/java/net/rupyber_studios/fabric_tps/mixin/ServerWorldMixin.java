package net.rupyber_studios.fabric_tps.mixin;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.rupyber_studios.fabric_tps.command.FabricTPSCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    @Unique
    private final String key = this.getRegistryKey().getValue().toString();
    @Unique
    private long tickStart = 0;

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tickStart(CallbackInfo info) {
        long currentTime = Util.getMeasuringTimeNano();
        long tickDelta = currentTime - tickStart;
        Float averageTickDelta = FabricTPSCommand.dimensionTickDeltas.get(key);
        if(averageTickDelta == null)
            averageTickDelta = 0F;
        FabricTPSCommand.dimensionTickDeltas.put(key, averageTickDelta * 0.8F + (float)tickDelta / 1000000.0F * 0.19999999F);
        tickStart = currentTime;
    }

    @Inject(at = @At("RETURN"), method = "tick")
    private void tickEnd(CallbackInfo info) {
        long currentTime = Util.getMeasuringTimeNano();
        long tickTime = currentTime - tickStart;
        Float averageTickTime = FabricTPSCommand.dimensionTickTimes.get(key);
        if(averageTickTime == null)
            averageTickTime = tickTime / 1000000.0F;
        FabricTPSCommand.dimensionTickTimes.put(key, averageTickTime * 0.8F + (float)tickTime / 1000000.0F * 0.19999999F);
    }
}
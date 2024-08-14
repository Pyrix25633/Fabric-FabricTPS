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
    private long tickStart = 0;

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tickStart(CallbackInfo info) {
        tickStart = Util.getMeasuringTimeNano();
    }

    @Inject(at = @At("RETURN"), method = "tick")
    private void tickEnd(CallbackInfo info) {
        String key = this.getRegistryKey().getValue().toString();
        long tickDuration =  Util.getMeasuringTimeNano() - tickStart;
        Float tickTime = FabricTPSCommand.dimensionTickTimes.get(key);
        if(tickTime == null)
            tickTime = 0F;
        FabricTPSCommand.dimensionTickTimes.put(this.getRegistryKey().getValue().toString(), tickTime * 0.8F + (float)tickDuration / 1000000.0F * 0.19999999F);
    }
}
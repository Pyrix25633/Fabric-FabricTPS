package net.rupyber_studios.fabric_tps.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.rupyber_studios.fabric_tps.command.FabricTPSCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {
    @Unique
    private long fabricTPS$tickStart = 0;

    @Inject(at = @At("HEAD"), method = "tick")
    private void fabricTPS$tickStart(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        long currentTime = Util.getNanos();
        if (fabricTPS$tickStart > 0) {
            String key = fabricTPS$key();
            long tickDelta = currentTime - fabricTPS$tickStart;
            float previousAverage = FabricTPSCommand.dimensionTickDeltas.getOrDefault(key, 50F);
            float currentDeltaMs = (float) tickDelta / 1_000_000.0F;
            FabricTPSCommand.dimensionTickDeltas.put(key, previousAverage * 0.8F + currentDeltaMs * 0.2F);
        }
        fabricTPS$tickStart = currentTime;
    }

    @Inject(at = @At("RETURN"), method = "tick")
    private void fabricTPS$tickEnd(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        String key = fabricTPS$key();
        long tickTime = Util.getNanos() - fabricTPS$tickStart;
        float currentTickTimeMs = (float) tickTime / 1_000_000.0F;
        float previousAverage = FabricTPSCommand.dimensionTickTimes.getOrDefault(key, currentTickTimeMs);
        FabricTPSCommand.dimensionTickTimes.put(key, previousAverage * 0.8F + currentTickTimeMs * 0.2F);
    }

    @Unique
    private String fabricTPS$key() {
        return ((ServerLevel) (Object) this).dimension().identifier().toString();
    }
}

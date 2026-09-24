package net.rupyber_studios.fabric_tps.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.rupyber_studios.fabric_tps.command.FabricTPSCommand;
import org.jspecify.annotations.NonNull;
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
    private void fabricTPS$tickStart(BooleanSupplier haveTime, CallbackInfo info) {
        fabricTPS$tickStart = Util.getNanos();
    }

    @Inject(at = @At("RETURN"), method = "tick")
    private void fabricTPS$tickEnd(BooleanSupplier haveTime, CallbackInfo info) {
        String key = fabricTPS$key();
        float tickTime = (Util.getNanos() - fabricTPS$tickStart) / 1_000_000F;
        float previousAverage = FabricTPSCommand.dimensionTickTimes.getOrDefault(key, tickTime);
        FabricTPSCommand.dimensionTickTimes.put(key, previousAverage * 0.8F + tickTime * 0.2F);
    }

    @Unique
    private @NonNull String fabricTPS$key() {
        return ((ServerLevel) (Object) this).dimension().identifier().toString();
    }
}

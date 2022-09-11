package net.set.spawn.mod.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin{

    @Inject(method = "prepareStartRegion", at = @At(value = "HEAD"))
    public void setspawnmod_startedWorldGen(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci){
        SetSpawn.shouldModifySpawn=true;
    }
}


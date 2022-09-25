package net.set.spawn.mod.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.set.spawn.mod.Seed;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ScreenHandlerListener {

    @Shadow @Final public MinecraftServer server;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }
    @Inject(method = "moveToSpawn", at = @At("HEAD"), cancellable = true)
    public void setspawnmod_setSpawn(ServerWorld world, CallbackInfo ci) {
        if (SetSpawn.shouldModifySpawn) {
            SetSpawn.shouldModifySpawn = false;
            Seed seedObject = SetSpawn.findSeedObjectFromLong(world.getSeed());
            String response;
            if (seedObject != null ) {
                int xFloor = MathHelper.floor(seedObject.getX());
                int zFloor = MathHelper.floor(seedObject.getZ());
                if ((Math.abs(xFloor - world.getSpawnPos().getX()) > this.server.getSpawnRadius(world))
                        || (Math.abs(zFloor - world.getSpawnPos().getZ()) > this.server.getSpawnRadius(world))) {
                    SetSpawn.shouldSendErrorMessage = true;
                    response = "The X or Z coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are more than 10 blocks away from the world spawn. Not overriding player spawnpoint.";
                    SetSpawn.errorMessage = response;
                    SetSpawn.LOGGER.warn(response);
                } else {
                    BlockPos spawnPos = SpawnLocatingAccessor.callFindOverworldSpawn(world, xFloor, zFloor, false);
                    if (spawnPos != null) {
                        this.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
                        if (world.isSpaceEmpty(this)) {
                            SetSpawn.shouldSendErrorMessage = false;
                            SetSpawn.LOGGER.info("Spawning player at: " + seedObject.getX() + " " + spawnPos.getY() + " " + seedObject.getZ());
                            ci.cancel();
                        } else {
                            SetSpawn.shouldSendErrorMessage = true;
                            response = "The coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are obstructed by blocks. Not overriding player spawnpoint.";
                            SetSpawn.errorMessage = response;
                            SetSpawn.LOGGER.warn(response);
                        }
                    } else {
                        SetSpawn.shouldSendErrorMessage = true;
                        response = "There is no valid spawning location at the specified coordinates (" + seedObject.getX() + ", " + seedObject.getZ() + "). Not overriding player spawnpoint.";
                        SetSpawn.errorMessage = response;
                        SetSpawn.LOGGER.warn(response);
                    }
                }
            }
        }
    }

}

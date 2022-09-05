package net.set.spawn.mod.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.set.spawn.mod.Conditionals;
import net.set.spawn.mod.config.SetSpawnProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ScreenHandlerListener {

    private static final double[] coordinates = new double[3];
    private static final Logger LOGGER = LogManager.getLogger("SetSpawn");

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }
    @Inject(method = "moveToSpawn", at = @At("HEAD"), cancellable = true)
    public void setSpawn(ServerWorld world, CallbackInfo ci) {
        if (properSpawnEditingCircumstances(world)) {
            double x = coordinates[0];
            double y = coordinates[1];
            double z = coordinates[2];
            this.refreshPositionAndAngles(new BlockPos(x, y, z), 0.0F, 0.0F);
            Conditionals.isAWorldGenerating = false;
            ci.cancel();
        }
    }

    private boolean properSpawnEditingCircumstances(ServerWorld world){
        if (!isWorldLoading()) return false;
        if (!isSetSeed(world)) return false;
        if (!isValidSyntax()) return false;
        if (!areCoordinatesPossible(world)) return false;
        return true;
    }

    private boolean isWorldLoading(){
        return Conditionals.isAWorldGenerating;
    }

    private boolean isSetSeed(ServerWorld world) {
        return String.valueOf(world.getSeed()).equals(SetSpawnProperties.seed);
    }

    private boolean isValidSyntax() {
        String[] coordinatesStringArray = SetSpawnProperties.coordinates.split(" ");
        if (coordinatesStringArray.length != 3) {
            LOGGER.warn("Coordinates given were in invalid format. Not overriding player spawnpoint.");
            return false;
        }
        try {
            coordinates[0] = Double.parseDouble(coordinatesStringArray[0]);
            coordinates[1] = Double.parseDouble(coordinatesStringArray[1]);
            coordinates[2] = Double.parseDouble(coordinatesStringArray[2]);
        } catch (NumberFormatException e) {
            LOGGER.warn("Coordinates given were in invalid format. Not overriding player spawnpoint.");
            return false;
        }
        return true;
    }

    private boolean areCoordinatesPossible(ServerWorld world) {
        // validate the coords
        return true;
    }

}

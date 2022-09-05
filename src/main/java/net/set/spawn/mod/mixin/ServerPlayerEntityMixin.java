package net.set.spawn.mod.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.set.spawn.mod.Conditionals;
import net.set.spawn.mod.config.SetSpawnProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
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
            LOGGER.warn("Coordinates given were in an invalid format. Not overriding player spawnpoint.");
            return false;
        }
        try {
            coordinates[0] = Double.parseDouble(coordinatesStringArray[0]);
            coordinates[1] = Double.parseDouble(coordinatesStringArray[1]);
            coordinates[2] = Double.parseDouble(coordinatesStringArray[2]);
        } catch (NumberFormatException e) {
            LOGGER.warn("Coordinates given were in an invalid format. Not overriding player spawnpoint.");
            return false;
        }
        return true;
    }

    private boolean areCoordinatesPossible(ServerWorld world) {
        double x = coordinates[0];
        double y = coordinates[1];
        double z = coordinates[2];
        if ( (Math.abs(x) % 1 != 0.5) || (Math.abs(y) % 1 != 0.0) || (Math.abs(z) % 1 != 0.5) ) {
            LOGGER.warn("Coordinates given were impossible. Make sure the coordinates are in <X>.5 <Y>.0 <Z>.5 format. Not overriding player spawnpoint.");
            return false;
        }

        int spawnX = world.getSpawnPos().getX();
        int spawnZ = world.getSpawnPos().getZ();
        if ( (Math.abs(x - spawnX) > 10) || (Math.abs(z - spawnZ) > 10) ) {
            LOGGER.warn("Coordinates given were impossible. Make sure X and Z are not more than 10 blocks away from the world spawn. Not overriding player spawnpoint.");
            return false;
        }

        BlockPos setCoords = new BlockPos(x, y, z);
        double overworldSpawnY = findOverworldSpawn(world, setCoords.getX(), setCoords.getZ()).getY();
        if (overworldSpawnY != y) {
            LOGGER.warn("Coordinates given were impossible. Make sure the Y coordinate is possible. Not overriding player spawnpoint.");
            return false;
        }
        return true;
    }

    @Nullable
    private static BlockPos findOverworldSpawn(ServerWorld world, int x, int z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
        Biome biome = world.getBiome(mutable);
        boolean bl = world.getDimension().hasCeiling();
        BlockState blockState = biome.getGenerationSettings().getSurfaceConfig().getTopMaterial();
        if (!blockState.getBlock().isIn(BlockTags.VALID_SPAWN)) {
            return null;
        } else {
            WorldChunk worldChunk = world.getChunk(x >> 4, z >> 4);
            int i = bl ? world.getChunkManager().getChunkGenerator().getSpawnHeight() : worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 15, z & 15);
            if (i < 0) {
                return null;
            } else {
                int j = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15);
                if (j <= i && j > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 15, z & 15)) {
                    return null;
                } else {
                    for(int k = i + 1; k >= 0; --k) {
                        mutable.set(x, k, z);
                        BlockState blockState2 = world.getBlockState(mutable);
                        if (!blockState2.getFluidState().isEmpty()) {
                            break;
                        }

                        if (blockState2.equals(blockState)) {
                            return mutable.up().toImmutable();
                        }
                    }

                    return null;
                }
            }
        }
    }

}

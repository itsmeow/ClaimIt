package dev.itsmeow.claimit.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TeleportUtils {

    public static BlockPos findNearestYLiquidOrSolid(World world, int x, int z) {
        BlockPos pos = new BlockPos(x, 80, z);
        boolean foundBlock = false;
        for(int i = 1; i > 0 && i < 255; i++) {
            BlockPos pos1 = pos.down(i);
            if(isGoodPosition(world, pos1)) {
                return pos1;
            }
        }
        if(!foundBlock) {
            for(int i = 254; i > 0 && i < 255; i--) {
                BlockPos pos1 = pos.down(i);
                if(isGoodPosition(world, pos1)) {
                    return pos1;
                }
            }
        }
        return pos;
    }
    
    private static boolean isGoodPosition(World world, BlockPos pos1) {
        BlockPos pos2 = new BlockPos(pos1.getX(), pos1.getY() + 1, pos1.getZ());
        BlockPos pos3 = new BlockPos(pos1.getX(), pos1.getY() - 1, pos1.getZ());
        return world.isAirBlock(pos1) && world.isAirBlock(pos2) && !(world.isAirBlock(pos3) | world.getBlockState(pos3) == Blocks.BEDROCK.getDefaultState());
    }
    
    public static void moveIfDifferentID(MinecraftServer server, EntityPlayerMP moved, int destination) {
        int current = moved.getEntityWorld().provider.getDimension();
        if(destination != current) {
            server.getPlayerList().transferPlayerToDimension(moved, destination, new Teleport(server.getWorld(destination)));
        }
    }

    public static class Teleport extends Teleporter {

        public Teleport(WorldServer worldIn) {
            super(worldIn);
        }

        @Override
        public void placeInPortal(Entity entityIn, float rotationYaw) {}

        @Override
        public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
            return true;
        }

        @Override
        public boolean makePortal(Entity entityIn) {
            return false;
        }

        @Override
        public void removeStalePortalLocations(long worldTime) {}

        @Override
        public void placeEntity(World world, Entity entity, float yaw) {}
    }

}

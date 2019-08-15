package its_meow.claimit.claim;

import java.util.HashMap;
import java.util.List;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.event.claim.ClaimAddedEvent;
import its_meow.claimit.api.event.claim.ClaimRemovedEvent;
import its_meow.claimit.api.event.claim.ClaimsClearedEvent;
import its_meow.claimit.permission.ClaimItPermissions;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
public class ClaimEventListener implements IWorldEventListener {
    
    public static HashMap<World, ClaimEventListener> listeners = new HashMap<World, ClaimEventListener>();
    
    protected final MinecraftServer server;
    protected final World world;

    public ClaimEventListener(MinecraftServer server, World world) {
        this.server = server;
        this.world = world;
    }

    @SubscribeEvent
    public static void onClaimAdded(ClaimAddedEvent event) {
        World world = event.getClaim().getWorld();
        ClaimEventListener listener = new ClaimEventListener(world.getMinecraftServer(), world);
        world.addEventListener(listener);
        listeners.put(world, listener);
    }
    
    @SubscribeEvent
    public static void onClaimRemoved(ClaimRemovedEvent event) {
        World world = event.getClaim().getWorld();
        if(listeners.containsKey(world)) {
            world.removeEventListener(listeners.remove(world));
        }
    }
    
    @SubscribeEvent
    public static void onClaimsCleared(ClaimsClearedEvent.Pre event) {
        for(ClaimArea claim : ClaimManager.getManager().getClaimsList()) {
            onClaimRemoved(new ClaimRemovedEvent(claim));
        }
    }

    @Override
    public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        if(oldState.getBlock() instanceof BlockPressurePlate && newState.getBlock() instanceof BlockPressurePlate) {
            boolean oldPower = oldState.getValue(BlockPressurePlate.POWERED);
            boolean newPower = newState.getValue(BlockPressurePlate.POWERED);
            if(!oldPower && newPower) {
                ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, pos);
                if(claim != null) {
                    AxisAlignedBB box = new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(1, 2, 1));
                    List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, box);
                    if(players != null && players.size() > 0) {
                        int amountWithPerms = (int) players.stream().filter(player -> claim.getMostSpecificClaim(pos).canUse(player)).count();
                        if(amountWithPerms < 1) {
                            world.setBlockState(pos, oldState);
                        }
                    } else {
                        if(!claim.getMostSpecificClaim(pos).isPermissionToggled(ClaimItPermissions.PRESSURE_PLATE)) {
                            world.setBlockState(pos, oldState);
                        }
                    }
                }
            }
        }
        if(newState.getBlock() == Blocks.FIRE) {
            for(EnumFacing facing : EnumFacing.VALUES) {
                BlockPos posF = pos.offset(facing);
                ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, posF);
                if(claim != null && !claim.getMostSpecificClaim(pos).isPermissionToggled(ClaimItPermissions.FIRE_CREATE)) {
                    if(!(world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN && claim.getMostSpecificClaim(pos).isPermissionToggled(ClaimItPermissions.FIRE_CREATE_ON_OBSIDIAN))) {
                        world.setBlockState(pos, oldState);
                    }
                }
            }
        }
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        Entity breaker = world.getEntityByID(breakerId);
        if(breaker != null && breaker instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) breaker;
            ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, pos);
            if(claim != null) {
                if(!claim.canModify(player)) {
                    for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
                        if(entityplayermp != null && entityplayermp.world == this.world) {
                            double d0 = (double)pos.getX() - entityplayermp.posX;
                            double d1 = (double)pos.getY() - entityplayermp.posY;
                            double d2 = (double)pos.getZ() - entityplayermp.posZ;

                            if(d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
                                entityplayermp.connection.sendPacket(new SPacketBlockBreakAnim(breakerId, pos, -1));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void notifyLightSet(BlockPos pos) {}

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}

    @Override
    public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x,
            double y, double z, float volume, float pitch) {}

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos) {}

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord,
            double xSpeed, double ySpeed, double zSpeed, int... parameters) {}

    @Override
    public void spawnParticle(int id, boolean ignoreRange, boolean minimiseParticleLevel, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed, int... parameters) {}

    @Override
    public void onEntityAdded(Entity entityIn) {}

    @Override
    public void onEntityRemoved(Entity entityIn) {}

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {}

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {}

}

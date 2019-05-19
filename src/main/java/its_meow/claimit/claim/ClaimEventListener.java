package its_meow.claimit.claim;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.event.claim.ClaimAddedEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
public class ClaimEventListener implements IWorldEventListener {

    protected final MinecraftServer server;
    protected final World world;

    public ClaimEventListener(MinecraftServer server, World world) {
        this.server = server;
        this.world = world;
    }

    @SubscribeEvent
    public static void onClaimAdded(ClaimAddedEvent event) {
        World world = event.getClaim().getWorld();
        world.addEventListener(new ClaimEventListener(world.getMinecraftServer(), world));
    }

    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {

    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        Entity breaker = world.getEntityByID(breakerId);
        if(breaker != null && breaker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) breaker;
            BlockPos lookPos = new BlockPos(player.getLookVec());
            if(lookPos != null) {
                ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, lookPos);
                if(claim != null) {
                    if(!claim.canModify(player)) {
                        for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
                            if(entityplayermp != null && entityplayermp.world == this.world && entityplayermp.getEntityId() != breakerId) {
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

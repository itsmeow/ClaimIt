package its_meow.claimit.command.claimit;

import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.util.objects.ClaimChunkUtil;
import its_meow.claimit.api.util.objects.ClaimChunkUtil.ClaimChunk;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.config.ClaimItConfig;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
public class CommandSubShowBorders extends CommandCIBase {

    private static HashMap<UUID, Long> borderTime = new HashMap<UUID, Long>();

    @Override
    public String getHelp(ICommandSender sender) {
        return "Displays claim corners within a chunk area.";
    }

    @Override
    public String getName() {
        return "showborders";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit showborders";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            UUID uuid = player.getGameProfile().getId();
            if(borderTime.containsKey(uuid)) {
                long oldtime = borderTime.get(uuid);
                long time = Math.abs(player.ticksExisted - oldtime);
                if(time < ClaimItConfig.max_show_borders_seconds * 20L) {
                    sendMessage(sender, TextFormatting.RED + "You are already viewing claim borders.");
                } else if(time < (ClaimItConfig.show_borders_cooldown * 20L) + (ClaimItConfig.max_show_borders_seconds * 20L)) {
                    sendMessage(sender, TextFormatting.RED + "You cannot view members for the next " + ClaimItConfig.show_borders_cooldown + " seconds.");
                } else {
                    borderTime.put(uuid, (long) player.ticksExisted);
                    sendMessage(sender, TextFormatting.GREEN + "Viewing claim borders for " + ClaimItConfig.max_show_borders_seconds + " seconds.");
                }
            } else {
                borderTime.put(uuid, (long) player.ticksExisted);
                sendMessage(sender, TextFormatting.GREEN + "Viewing claim borders for " + ClaimItConfig.max_show_borders_seconds + " seconds.");
            }
        } else {
            throw new CommandException("You must be a player to use this command!");
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.showborders";
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent e) {
        if(borderTime.containsKey(e.player.getGameProfile().getId())) {
            long oldtime = borderTime.get(e.player.getGameProfile().getId());
            long time = Math.abs(e.player.ticksExisted - oldtime);
            if(time <= ClaimItConfig.max_show_borders_seconds * 20L) {
                if(e.player.isAddedToWorld() && e.player.getEntityWorld() instanceof WorldServer && e.player instanceof EntityPlayerMP && e.player.ticksExisted % 20L == 0) {
                    EntityPlayerMP player = (EntityPlayerMP) e.player;
                    WorldServer world = (WorldServer) e.player.getEntityWorld();
                    ClaimChunk chunk = ClaimChunkUtil.getChunk(e.player.getPosition());
                    displayClaimsInChunk(player, world, chunk);
                    for(EnumFacing facing : EnumFacing.HORIZONTALS) {
                        int xOff = facing.getXOffset();
                        int zOff = facing.getZOffset();
                        displayClaimsInChunk(player, world, new ClaimChunk(chunk.x + xOff, chunk.z + zOff));
                        displayClaimsInChunk(player, world, new ClaimChunk(chunk.x + (xOff * 2), chunk.z + (zOff * 2)));
                    }
                }
            }
        }
    }

    public static void displayClaimsInChunk(EntityPlayerMP player, WorldServer world, ClaimChunk chunk) {
        ImmutableSet<ClaimArea> claims = ClaimManager.getManager().getClaimsInChunk(world.provider.getDimension(), chunk);
        if(claims != null && claims.size() > 0) {
            for(ClaimArea claim : claims) {
                for(int i = 0; i < 4; i++) {
                    BlockPos pos = claim.getFourCorners()[i];
                    if(i == 1) {
                        pos = pos.add(1, 0, 0);
                    } else if(i == 2) {
                        pos = pos.add(0, 0, 1);
                    } else if(i == 3) {
                        pos = pos.add(1, 0, 1);
                    }
                    spawnPathBetween(player, world, new BlockPos(pos.getX(), 0, pos.getZ()), new BlockPos(pos.getX(), world.getActualHeight(), pos.getZ()));
                }
            }
        }
    }

    public static void spawnPathBetween(EntityPlayerMP player, WorldServer world, BlockPos start, BlockPos dest) {
        final double stops = 3;
        double dirX = (dest.getX() - start.getX()) / stops;
        double dirY = (dest.getY() - start.getY()) / stops;
        double dirZ = (dest.getZ() - start.getZ()) / stops;
        Vec3d dir = new Vec3d(dirX, dirY, dirZ);
        for(double i = 1; i <= stops; i++) {
            Vec3d posOff = dir.scale(i);
            Vec3d pos = posOff.add(start.getX(), start.getY(), start.getZ());
            player.connection.sendPacket(new SPacketParticles(EnumParticleTypes.VILLAGER_HAPPY, false, (float) pos.x, (float) pos.y, (float) pos.z, 0F, 20F, 0F, 20F, 20));
        }
    }

}

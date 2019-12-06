package its_meow.claimit;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;

import its_meow.claimit.api.AdminManager;
import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.userconfig.UserConfigManager;
import its_meow.claimit.api.userconfig.UserConfigTypeRegistry;
import its_meow.claimit.api.userconfig.UserConfigs;
import its_meow.claimit.api.util.objects.ClaimChunkUtil.ClaimChunk;
import its_meow.claimit.command.CommandClaimIt;
import its_meow.claimit.config.ClaimItConfig;
import its_meow.claimit.permission.ClaimItPermissions;
import its_meow.claimit.util.UserClaimBlocks;
import its_meow.claimit.util.command.ConfirmationManager;
import its_meow.claimit.util.text.ColorUtil;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
@Mod(modid = ClaimIt.MOD_ID, name = ClaimIt.NAME, version = ClaimIt.VERSION, acceptedMinecraftVersions = ClaimIt.acceptedMCV, acceptableRemoteVersions = "*", dependencies = "after-required:claimitapi")
public class ClaimIt {

    public static final String MOD_ID = "claimit";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "ClaimIt";
    public static final String acceptedMCV = ClaimItAPI.acceptedMCV;

    @Instance(ClaimIt.MOD_ID)
    public static ClaimIt mod;

    public static Item claiming_item = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        UserConfigs.register();
        ClaimItPermissions.register();
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        AdminManager.clearAdmins();
        UserConfigManager.deserialize();
        ConfirmationManager.getManager().removeAllConfirms();
        UserClaimBlocks.deserialize();
        event.registerServerCommand(new CommandClaimIt());
        claiming_item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ClaimItConfig.claim_create_item));
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        UserConfigManager.serialize();
        UserClaimBlocks.serialize();
    }

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save e) {
        UserConfigManager.serialize();
        UserClaimBlocks.serialize();
    }

    private static HashMap<UUID, Integer> tickJoined = new HashMap<UUID, Integer>();

    @SubscribeEvent
    public static void onPlayerJoin(PlayerLoggedInEvent e) {
        tickJoined.put(e.player.getGameProfile().getId(), e.player.world.getMinecraftServer().getTickCounter());
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerLoggedOutEvent e) {
        tickJoined.remove(e.player.getGameProfile().getId());
        lastMsPos.remove(e.player.getGameProfile().getId());
        AdminManager.removeAdmin(e.player);
        ConfirmationManager.getManager().removeConfirm(e.player);
    }

    private static HashMap<UUID, BlockPos> lastMsPos = new HashMap<UUID, BlockPos>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent e) {
        if(!e.player.world.isRemote) {
            if(ClaimItConfig.claim_blocks_accrual_period > 0) {
                int curTick = e.player.world.getMinecraftServer().getTickCounter();
                int joinedTick = tickJoined.getOrDefault(e.player.getGameProfile().getId(), 0);
                if(curTick != joinedTick && (curTick - joinedTick) % ClaimItConfig.claim_blocks_accrual_period == 0) {
                    UUID uuid = e.player.getGameProfile().getId();
                    int oldAllowed = UserClaimBlocks.getClaimBlocksAllowed(uuid);
                    int amount = ClaimItConfig.claim_blocks_accrual_amount;
                    if(UserClaimBlocks.getClaimBlocksRemaining(uuid) + amount >= 0) {
                        UserClaimBlocks.setAllowedClaimBlocks(uuid, oldAllowed + amount);
                    }
                }
            }
            if(e.player.ticksExisted % 5 == 0) {
                if(e.player.ticksExisted % 2500 == 0) {
                    lastMsPos.clear();
                }
                if(lastMsPos.get(e.player.getGameProfile().getId()) == null) {
                    lastMsPos.put(e.player.getGameProfile().getId(), e.player.getPosition());
                }
                BlockPos pos = e.player.getPosition();
                ClaimArea lastMsClaim = ClaimManager.getManager().getClaimAtLocation(e.player.world, lastMsPos.get(e.player.getGameProfile().getId()));
                ClaimArea currClaim = ClaimManager.getManager().getClaimAtLocation(e.player.world, pos);
                if(lastMsClaim != null) {
                    if(currClaim == null) {
                        Boolean value = UserConfigTypeRegistry.BOOLEAN.storage.getValueFor(UserConfigs.EXIT_MESSAGE, e.player.getGameProfile().getId());
                        if(value == null || value) {
                            e.player.sendStatusMessage(new TextComponentString(ColorUtil.getFormattedClaimMessage(ClaimItConfig.claim_exit_message, lastMsClaim)), true);
                        }
                    }
                } else {
                    if(currClaim != null) {
                        Boolean value = UserConfigTypeRegistry.BOOLEAN.storage.getValueFor(UserConfigs.ENTRY_MESSAGE, e.player.getGameProfile().getId());
                        if((value == null || value)) {
                            e.player.sendStatusMessage(new TextComponentString(ColorUtil.getFormattedClaimMessage(ClaimItConfig.claim_entry_message, currClaim)), true);
                        }
                    }
                }
                lastMsPos.put(e.player.getGameProfile().getId(), pos);
            }
        }
    }

    private static int lastIndex = 0;

    @SubscribeEvent
    public static void worldTick(WorldTickEvent e) {
        if(lastIndex != -1 && ClaimItConfig.prune_unclaimed_chunks && e.world instanceof WorldServer && e.world.getTotalWorldTime() % 20 == 0 && e.phase == Phase.END) {
            WorldServer world = (WorldServer) e.world;
            File regionData = new File(world.getSaveHandler().getWorldDirectory().getAbsoluteFile() + "/region");
            if(regionData.exists()) {
                String[] list = regionData.list((file, string) -> {return string.matches("r.(0|-?[1-9]([0-9]+)?).(0|-?[1-9]([0-9]+)?).mca");});
                if(list.length > lastIndex) {
                    String region = list[lastIndex];
                    String xStr = region.substring(region.indexOf('.') + 1, region.substring(0, region.lastIndexOf('.') - 1).lastIndexOf('.'));
                    String zStr = region.substring(region.substring(0, region.lastIndexOf('.') - 1).lastIndexOf('.') + 1, region.lastIndexOf('.'));
                    int x = Integer.parseInt(xStr);
                    int z = Integer.parseInt(zStr);
                    x *= 32; // convert to chunk coords
                    z *= 32;
                    int nonDeleteableChunks = 0;
                    for(int i = 0; i < 32; i++) {
                        
                        for(int j = 0; j < 32; j++) {
                            int tX = x + i;
                            int tZ = z + j;
                            if(world.isChunkGeneratedAt(tX, tZ)) {
                                Chunk chunk = world.getChunk(tX, tZ);
                                if(chunk.isTerrainPopulated() && ClaimManager.getManager().getClaimsInChunk(world.provider.getDimension(), new ClaimChunk(tX, tZ)).size() == 0) {
                                    world.getChunkProvider().queueUnload(chunk);
                                    Chunk chunk2 = world.getChunkProvider().chunkGenerator.generateChunk(tX, tZ);
                                    chunk.setTerrainPopulated(false);
                                    chunk.setStorageArrays(chunk2.getBlockStorageArray());
                                    chunk.markDirty();
                                    LogManager.getLogger().info("Pruned chunk " + tX + ", " + tZ);
                                } else {
                                    nonDeleteableChunks++;
                                }
                            }
                        }
                    }
                    if(nonDeleteableChunks == 0) {
                        new File(regionData.getAbsolutePath() + "/" + region).delete();
                    }
                    lastIndex++;
                } else {
                    lastIndex = -1;
                }
            }
        }
    }

}

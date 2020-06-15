package dev.itsmeow.claimit.util;

import java.util.HashMap;
import java.util.UUID;

import dev.itsmeow.claimit.ClaimIt;
import dev.itsmeow.claimit.api.claim.ClaimManager;
import dev.itsmeow.claimit.api.event.claim.ClaimCreatedEvent;
import dev.itsmeow.claimit.config.ClaimItConfig;
import dev.itsmeow.claimit.serialization.ClaimItGlobalDataSerializer;
import dev.itsmeow.claimit.util.text.FTC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
public class UserClaimBlocks {
    
    private static HashMap<UUID, Integer> claimBlocksAllowed = new HashMap<UUID, Integer>();
    
    public static int getClaimBlocksUsed(UUID uuid) {
        return ClaimManager.getManager().getClaimsOwnedByPlayer(uuid).stream().mapToInt(claim -> claim.getArea()).sum();
    }
    
    public static int getClaimBlocksRemaining(UUID uuid) {
        return getClaimBlocksAllowed(uuid) - getClaimBlocksUsed(uuid);
    }
    
    public static int getClaimBlocksAllowed(UUID uuid) {
        return claimBlocksAllowed.getOrDefault(uuid, ClaimItConfig.default_claim_max_area);
    }
    
    public static void setAllowedClaimBlocks(UUID uuid, int allowedBlocks) {
        claimBlocksAllowed.put(uuid, allowedBlocks);
    }
    
    @SubscribeEvent
    public static void onClaimCreated(ClaimCreatedEvent e) {
        int area = e.getClaim().getArea();
        UUID owner = e.getClaim().getOwner();
        int allowed = getClaimBlocksRemaining(owner);
        EntityPlayer player = e.getClaim().getWorld().getPlayerEntityByUUID(owner);
        if(allowed - area < 0) {
            if(player != null) {
                player.sendMessage(new FTC("You need " + Math.abs(allowed - area) + " more claim blocks to create this claim!", TextFormatting.RED));
                player.sendMessage(new FTC("Claim Blocks Required: " + area, TextFormatting.YELLOW));
                player.sendMessage(new FTC("Claim Blocks Remaining: " + allowed, TextFormatting.YELLOW));
            }
            e.setCanceled(true);
        } else {
            if(player != null) {
                player.sendMessage(new FTC("This claim is " + area + " claim blocks. You now have " + (allowed - area) + " claim blocks remaining.", TextFormatting.GREEN));
            }
        }
    }
    
    public static void serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        claimBlocksAllowed.forEach((uuid, amount) -> { if(amount != ClaimItConfig.default_claim_max_area) tag.setInteger(uuid.toString(), amount); }); 
        ClaimItGlobalDataSerializer.get().data.setTag("USER_MAX_CLAIM_BLOCKS", tag);
        WorldSavedData wsd = ClaimItGlobalDataSerializer.get();
        wsd.markDirty();
    }
    
    public static void deserialize() {
        NBTTagCompound tag = ClaimItGlobalDataSerializer.get().data.getCompoundTag("USER_MAX_CLAIM_BLOCKS");
        if(tag != null) {
            tag.getKeySet().forEach(key -> { if(tag.hasKey(key, Constants.NBT.TAG_INT)) claimBlocksAllowed.put(UUID.fromString(key), tag.getInteger(key)); });
        }
    }
    
}

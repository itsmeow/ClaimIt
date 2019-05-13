package its_meow.claimit.util;

import java.util.HashMap;
import java.util.UUID;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.event.claim.ClaimCreatedEvent;
import its_meow.claimit.config.ClaimItConfig;
import its_meow.claimit.serialization.ClaimItGlobalDataSerializer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
public class UserClaimBlocks {
    
    private static HashMap<UUID, Integer> claimBlocksAllowed = new HashMap<UUID, Integer>();
    
    public static int getClaimBlocksUsed(UUID user) {
        return ClaimManager.getManager().getClaimsOwnedByPlayer(user).stream().mapToInt(claim -> claim.getArea()).sum();
    }
    
    public static int getClaimBlocksRemaining(UUID user) {
        return getClaimBlocksAllowed(user) - getClaimBlocksUsed(user);
    }
    
    public static int getClaimBlocksAllowed(UUID user) {
        return claimBlocksAllowed.getOrDefault(user, ClaimItConfig.default_claim_max_area);
    }
    
    /**
     * @return the previous value associated with key, or null if there was no mapping for key. (A null return can also indicate that the map previously associated null with key.)
     */
    public static int setAllowedClaimBlocks(UUID user, int allowedBlocks) {
        return claimBlocksAllowed.put(user, allowedBlocks);
    }
    
    @SubscribeEvent
    public static void onClaimCreated(ClaimCreatedEvent e) {
        int area = e.getClaim().getArea();
        UUID owner = e.getClaim().getOwner();
        int allowed = getClaimBlocksRemaining(owner);
        EntityPlayer player = e.getClaim().getWorld().getPlayerEntityByUUID(owner);
        if(allowed - area < 0) {
            if(player != null) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You need " + Math.abs(allowed - area) + " more claim blocks to create this claim!"));
                player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Claim Blocks Required: " + area));
                player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Claim Blocks Remaining: " + allowed));
            }
            e.setCanceled(true);
        } else {
            if(player != null) {
                player.sendMessage(new TextComponentString(TextFormatting.GREEN + "This claim is " + area + " claim blocks. You now have " + (allowed - area) + " claim blocks remaining."));
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

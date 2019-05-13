package its_meow.claimit.api.util.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

public class ClaimNBTUtil {
    
    public static NBTTagCompound writeToggles(NBTTagCompound tag, Map<ClaimPermissionToggle, Boolean> toggles) {
        NBTTagCompound togglesTag = new NBTTagCompound();
        for(ClaimPermissionToggle perm : toggles.keySet()) {
            togglesTag.setBoolean(perm.parsedName, toggles.get(perm));
        }
        tag.setTag("TOGGLES", togglesTag);
        return tag;
    }
    
    public static NBTTagCompound writeClaimNames(NBTTagCompound tag, Collection<ClaimArea> claims) {
        NBTTagList claimsTagList = new NBTTagList();
        for(ClaimArea claim : claims) {
            claimsTagList.appendTag(new NBTTagString(claim.getTrueViewName()));
        }
        tag.setTag("CLAIMS", claimsTagList);
        return tag;
    }
    
    public static ArrayList<ClaimArea> readClaimNames(NBTTagCompound tag) {
        ArrayList<ClaimArea> claims = new ArrayList<ClaimArea>();
        NBTTagList tagList = tag.getTagList("CLAIMS", Constants.NBT.TAG_STRING);
        for(int i = 0; i < tagList.tagCount(); i++) {
            String trueName = tagList.getStringTagAt(i);
            ClaimArea claim = ClaimManager.getManager().getClaimByTrueName(trueName);
            if(claim != null) {
                claims.add(claim);
            } else {
                throw new RuntimeException("There was no claim with the name " + trueName + " found. This means data is missing/the world is corrupted/loading order is incorrect! Crashing to protect data.");
            }
        }
        return claims;
    }
    
    public static NBTTagCompound writeUUIDs(NBTTagCompound tag, String tagName, Collection<UUID> uuids) {
        NBTTagList uuidTag = new NBTTagList();
        for(UUID uuid : uuids) {
            uuidTag.appendTag(new NBTTagString(uuid.toString()));
        }
        tag.setTag(tagName, uuidTag);
        return tag;
    }
    
    public static ArrayList<UUID> readUUIDs(NBTTagCompound tag, String tagName) {
        ArrayList<UUID> list = new ArrayList<UUID>();
        NBTTagList tagList = tag.getTagList(tagName, Constants.NBT.TAG_STRING);
        for(int i = 0; i < tagList.tagCount(); i++) {
            String uuidString = tagList.getStringTagAt(i);
            UUID uuid = UUID.fromString(uuidString);
            list.add(uuid);
        }
        return list;
    }
    
    public static Map<ClaimPermissionToggle, Boolean> readToggles(NBTTagCompound tag) {
        HashMap<ClaimPermissionToggle, Boolean> map = new HashMap<ClaimPermissionToggle, Boolean>();
        NBTTagCompound toggles = tag.getCompoundTag("TOGGLES");
        for(String permString : toggles.getKeySet()) {
            ClaimPermissionToggle perm = ClaimPermissionRegistry.getPermissionToggle(permString);
            if(perm != null) {
                if(perm.getForceEnabled()) {
                    map.put(perm, perm.getForceValue());
                } else {
                    map.put(perm, toggles.getBoolean(permString));
                }
            }
        }
        return map;
    }
     
}

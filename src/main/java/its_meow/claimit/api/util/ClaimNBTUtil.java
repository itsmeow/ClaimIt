package its_meow.claimit.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

public class ClaimNBTUtil {
    
    public static NBTTagCompound writeMembers(NBTTagCompound tag, Map<ClaimPermissionMember, ArrayList<UUID>> uuids) {
        NBTTagCompound memberCompound = new NBTTagCompound();
        for(ClaimPermissionMember perm : uuids.keySet()) {
            NBTTagList members = new NBTTagList();
            for(UUID member : uuids.get(perm)) {
                members.appendTag(new NBTTagString(member.toString()));
            }
            memberCompound.setTag(perm.parsedName, members);
        }
        tag.setTag("MEMBERS", memberCompound);
        return tag;
    }
    
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
    
    public static Map<ClaimPermissionMember, ArrayList<UUID>> readMembers(NBTTagCompound tag) {
        HashMap<ClaimPermissionMember, ArrayList<UUID>> map = new HashMap<ClaimPermissionMember, ArrayList<UUID>>();
        NBTTagCompound memberCompound = tag.getCompoundTag("MEMBERS");
        for(String permString : memberCompound.getKeySet()) {
            if(ClaimPermissionRegistry.getPermissionMember(permString) != null) {
                NBTTagList tagList = memberCompound.getTagList(permString, Constants.NBT.TAG_STRING);
                ClaimPermissionMember perm = ClaimPermissionRegistry.getPermissionMember(permString);
                map.putIfAbsent(perm, new ArrayList<UUID>());
                for(int i = 0; i < tagList.tagCount(); i++) {
                    String uuidString = tagList.getStringTagAt(i);
                    UUID member = UUID.fromString(uuidString);
                    map.get(perm).add(member);
                }
            }
        }
        return map;
    }
    
    public static Map<ClaimPermissionToggle, Boolean> readToggles(NBTTagCompound tag) {
        HashMap<ClaimPermissionToggle, Boolean> map = new HashMap<ClaimPermissionToggle, Boolean>();
        NBTTagCompound toggles = tag.getCompoundTag("TOGGLES");
        for(String permString : toggles.getKeySet()) {
            ClaimPermissionToggle perm = ClaimPermissionRegistry.getPermissionToggle(permString);
            if(perm != null) {
                if(perm.force) {
                    map.put(perm, perm.toForce);
                } else {
                    map.put(perm, toggles.getBoolean(permString));
                }
            }
        }
        return map;
    }
    
    public static Map<ClaimPermissionMember, ArrayList<UUID>> mergeMembers(Map<ClaimPermissionMember, ArrayList<UUID>> one, Map<ClaimPermissionMember, ArrayList<UUID>> two) {
        HashMap<ClaimPermissionMember, ArrayList<UUID>> map = new HashMap<ClaimPermissionMember, ArrayList<UUID>>();
        for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
            map.put(perm, new ArrayList<UUID>());
            if(one.containsKey(perm)) {
                map.get(perm).addAll(one.get(perm));
            }
            if(two.containsKey(perm)) {
                map.get(perm).addAll(two.get(perm));
            }
        }
        return map;
    }
     
}

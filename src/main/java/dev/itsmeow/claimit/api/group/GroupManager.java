package dev.itsmeow.claimit.api.group;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import dev.itsmeow.claimit.api.ClaimItAPI;
import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.event.group.GroupClaimAddedEvent;
import dev.itsmeow.claimit.api.event.group.GroupClaimRemovedEvent;
import dev.itsmeow.claimit.api.serialization.GlobalDataSerializer;
import dev.itsmeow.claimit.api.util.objects.BiMultiMap;
import dev.itsmeow.claimit.util.text.ColorUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroupManager {

    private static HashMap<String, Group> groups = new HashMap<String, Group>();
    private static BiMultiMap<ClaimArea, Group> claimToGroup = new BiMultiMap<ClaimArea, Group>();

    public static boolean addGroup(Group group) {
        if(groups.containsKey(group.getName())) {
            return false;
        }
        groups.put(group.getName(), group);
        return true;
    }

    public static void removeGroup(Group group) {
        group.removeAllClaims();
        group.removeAllMembers();
        groups.remove(group.getName());
    }

    @Nullable
    public static Group getGroup(String name) {
        return groups.get(name);
    }

    @Nullable
    public static ImmutableSet<Group> getGroupsForClaim(ClaimArea claim) {
        return ImmutableSet.copyOf(claimToGroup.getValues(claim));
    }

    public static boolean renameGroup(String name, String newName) {
        if(!groups.containsKey(name) || groups.containsKey(newName)) {
            return false;
        }
        Group group = groups.get(name);
        groups.remove(name);
        groups.put(newName, group);
        group.name = newName;
        return true;
    }
    
    public static boolean setGroupTag(Group group, String tag) {
        String cleanTag = ColorUtil.removeColorCodes(tag);
        boolean pass = true;
        for(Group group1 : groups.values()) {
            if(group1 != group && group1.getTag() != null && ColorUtil.removeColorCodes(group1.getTag()).equalsIgnoreCase(cleanTag)) {
                pass = false;
            }
        }
        if(pass) {
            group.setTag(tag);
        }
        return pass;
    }

    public static ImmutableSet<Group> getGroups() {
        return ImmutableSet.copyOf(groups.values());
    }

    public static void serialize() {
        GlobalDataSerializer store = GlobalDataSerializer.get();
        NBTTagCompound comp = store.data;
        NBTTagCompound groupsTag = new NBTTagCompound();
        for(String groupName : groups.keySet()) {
            NBTTagCompound groupCompound = groups.get(groupName).serialize();
            groupsTag.setTag(groupName, groupCompound);
            ClaimItAPI.logger.debug("Serializing group " + groupName);
        }
        comp.setTag("GROUPS", groupsTag);
        store.markDirty();
    }

    public static void deserialize() {
        groups.clear();
        GlobalDataSerializer store = GlobalDataSerializer.get();
        NBTTagCompound comp = store.data;
        if(comp != null) {
            NBTTagCompound groupsTag = comp.getCompoundTag("GROUPS");
            for(String key : groupsTag.getKeySet()) {
                ClaimItAPI.logger.debug("Loading group " + key);
                Group group = Group.deserialize(groupsTag.getCompoundTag(key));
                if(!addGroup(group)) {
                    ClaimItAPI.logger.error("Duplicate group name of " + group.name + " failed to load! Was the data edited?");
                }
            }
        } else {
            ClaimItAPI.logger.warn("Could not get group data tag.");
        }
    }

    @Mod.EventBusSubscriber(modid = ClaimItAPI.MOD_ID)
    private static class InternalGroupEventHandler {

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onGroupClaimAdded(GroupClaimAddedEvent e) {
            claimToGroup.put(e.getClaim(), e.getGroup());
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onGroupClaimRemoved(GroupClaimRemovedEvent e) {
            claimToGroup.remove(e.getClaim(), e.getGroup());
        }

    }

}
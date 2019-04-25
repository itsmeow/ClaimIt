package its_meow.claimit.api.group;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.serialization.GlobalDataSerializer;
import net.minecraft.nbt.NBTTagCompound;

public class GroupManager {
    
    private static HashMap<String, Group> groups = new HashMap<String, Group>();

    public static boolean addGroup(Group group) {
        if(groups.containsKey(group.getName())) {
            return false;
        }
        groups.put(group.getName(), group);
        return true;
    }

    public static void removeGroup(Group group) {
        groups.remove(group.getName());
    }

    @Nullable
    public static Group getGroup(String name) {
        return groups.get(name);
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

    public static ImmutableSet<Group> getGroups() {
        return ImmutableSet.copyOf(groups.values());
    }
    
    public static void serialize() {
        GlobalDataSerializer store = GlobalDataSerializer.get();
        NBTTagCompound comp = store.data;
        if(store != null && store.data != null && store.data.getSize() > 0) {
            Set<String> toRemove = new HashSet<String>();
            for(String key : store.data.getKeySet()) { // Remove all data
                if(!key.equals("")) {
                    toRemove.add(key);
                }
            }
            for(String key : toRemove) {
                store.data.removeTag(key);
            }
        }
        for(String groupName : groups.keySet()) {
            NBTTagCompound groupCompound = groups.get(groupName).serialize();
            comp.setTag(groupName, groupCompound);
        }
    }
    
    public static void deserialize() {
        groups.clear();
        GlobalDataSerializer store = GlobalDataSerializer.get();
        NBTTagCompound comp = store.data;
        if(comp != null) {
            for(String key : comp.getKeySet()) {
                System.out.println("Loading " + key);
                Group group = Group.deserialize(comp.getCompoundTag(key));
                if(!addGroup(group)) {
                    ClaimIt.logger.error("Duplicate group name of " + group.name + " failed to load! Was the data edited?");
                }
            }
        }
    }

}
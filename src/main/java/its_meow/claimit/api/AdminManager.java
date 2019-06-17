package its_meow.claimit.api;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

public class AdminManager {
    
    private static final Set<EntityPlayer> admins = new HashSet<EntityPlayer>();
    
    /** Adds a player to the admin list, allowing claim bypass **/
    public static void addAdmin(EntityPlayer player) {
        admins.add(player);
    }

    /** Removes a player from the admin list, removing claim bypass **/
    public static void removeAdmin(EntityPlayer player) {
        admins.remove(player);
    }

    /** Tells whether a player is an admin/has claim bypass
     * @return True if has admin, false if not. **/
    public static boolean isAdmin(EntityPlayer player) {
        return admins.contains(player);
    }
    
    /** Clears the list of players with admin enabled. **/
    public static void clearAdmins() {
        admins.clear();
    }
    
}

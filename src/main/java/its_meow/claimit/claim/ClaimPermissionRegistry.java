package its_meow.claimit.claim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import its_meow.claimit.ClaimIt;
import net.minecraft.util.ResourceLocation;

public class ClaimPermissionRegistry {
	
	private static Map<ResourceLocation, ClaimPermission> permissions = new HashMap<ResourceLocation, ClaimPermission>();
	
	public static void addPermission(ClaimPermission permission) {
		if(permission.resource.getResourceDomain().equals("minecraft")) {
			ClaimIt.logger.log(Level.WARN, "Permission " + permission.resource.toString() + " registered with default prefix `minecraft`. Expected MOD ID.");
		}
		for(ResourceLocation location : permissions.keySet())  {
			if(location.equals(permission.resource)) {
				throw new RuntimeException("Identical permission IDs registered: " + location + " and " + permission.resource);
			}
		}
		permissions.put(permission.resource, permission);
	}

	public static final Collection<ClaimPermission> getPermissions() {
		return permissions.values();
	}
	
	public static final ClaimPermission getPermission(ResourceLocation resource) {
		return permissions.get(resource);
	}
	
}

package its_meow.claimit.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import its_meow.claimit.ClaimIt;
import net.minecraft.util.ResourceLocation;

public class ClaimPermissionRegistry {

	private static Map<ResourceLocation, ClaimPermissionMember> memberPermissions = new HashMap<ResourceLocation, ClaimPermissionMember>();
	private static Map<ResourceLocation, ClaimPermissionToggle> togglePermissions = new HashMap<ResourceLocation, ClaimPermissionToggle>();

	@SuppressWarnings("unchecked") // Yes, I know. Works better this way.
	public static void addPermission(ClaimPermission permission) {
		if(permission.resource.getResourceDomain().equals("minecraft")) {
			ClaimIt.logger.log(Level.WARN, "Permission " + permission.resource.toString() + " registered with default prefix `minecraft`. Expected MOD ID.");
		}
		@SuppressWarnings("rawtypes") // Look, I had to. I hate it too.
		Map map = permission.type == EnumPermissionType.MEMBER ? memberPermissions : togglePermissions;
		if(map.containsKey(permission.resource)) {
			throw new RuntimeException("Identical permission ID registered: " + permission.resource);
		}
		map.put(permission.resource, permission);
	}

	public static final Collection<ClaimPermissionMember> getMemberPermissions() {
		return memberPermissions.values();
	}
	
	public static final Collection<ClaimPermissionToggle> getTogglePermissions() {
		return togglePermissions.values();
	}
	
	public static final ClaimPermissionMember getPermissionMember(ResourceLocation resource) {
		return memberPermissions.get(resource);
	}
	
	public static final ClaimPermissionToggle getPermissionToggle(ResourceLocation resource) {
		return togglePermissions.get(resource);
	}
	
	public static final ClaimPermissionMember parseMember(String uniqueName) {
		for(ClaimPermissionMember perm : memberPermissions.values()) {
			if(perm.parsedName.equals(uniqueName)) {
				return perm;
			}
		}
		return null;
	}
	
	public static final ClaimPermissionToggle parseToggle(String uniqueName) {
		for(ClaimPermissionToggle perm : togglePermissions.values()) {
			if(perm.parsedName.equals(uniqueName)) {
				return perm;
			}
		}
		return null;
	}
	
}

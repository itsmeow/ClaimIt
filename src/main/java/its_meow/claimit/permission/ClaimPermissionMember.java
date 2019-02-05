package its_meow.claimit.permission;

import net.minecraft.util.ResourceLocation;

public class ClaimPermissionMember extends ClaimPermission {
	
	/** @param resource - ResourceLocation used to serialize this permission
	 *  @param parsedUniqueName - The UNIQUE name that will be used in commands and displayed to the user for this permission **/
	public ClaimPermissionMember(ResourceLocation resource, String parsedUniqueName) {
		super(resource, parsedUniqueName, EnumPermissionType.TOGGLE);
	}
	
}

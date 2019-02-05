package its_meow.claimit.permission;

import net.minecraft.util.ResourceLocation;

public class ClaimPermission {
	
	public final EnumPermissionType type;
	public final String parsedName;
	public final ResourceLocation resource;
	
	/** @param resource - ResourceLocation used to serialize this permission
	 *  @param parsedUniqueName - The UNIQUE name that will be used in commands and displayed to the user for this permission
	 *  @param type - The type of this permission. Determines whether it will allow members to be added or a simple toggle by the owner. **/
	protected ClaimPermission(ResourceLocation resource, String parsedUniqueName, EnumPermissionType type) {
		this.resource = resource;
		this.parsedName = parsedUniqueName;
		this.type = type;
	}
	
}

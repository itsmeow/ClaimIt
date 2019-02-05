package its_meow.claimit.permission;

public class ClaimPermission {
	
	public final EnumPermissionType type;
	public final String parsedName;
	
	/** @param parsedUniqueName - The UNIQUE name that will be used in commands and displayed to the user for this permission
	 *  @param type - The type of this permission. Determines whether it will allow members to be added or a simple toggle by the owner. **/
	protected ClaimPermission(String parsedUniqueName, EnumPermissionType type) {
		this.parsedName = parsedUniqueName;
		this.type = type;
	}
	
}

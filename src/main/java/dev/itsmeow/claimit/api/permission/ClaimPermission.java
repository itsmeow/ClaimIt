package dev.itsmeow.claimit.api.permission;

public class ClaimPermission {
	
	public final EnumPermissionType type;
	public final String parsedName;
	public final String helpInfo;
	
	/** @param parsedUniqueName - The UNIQUE name that will be used in commands and displayed to the user for this permission
	 *  @param type - The type of this permission. Determines whether it will allow members to be added or a simple toggle by the owner. 
	 *  @param helpInfo - Information for this permission to be displayed in the help menu. **/
	protected ClaimPermission(String parsedUniqueName, EnumPermissionType type, String helpInfo) {
		this.parsedName = parsedUniqueName;
		this.type = type;
		this.helpInfo = helpInfo;
	}
	
}

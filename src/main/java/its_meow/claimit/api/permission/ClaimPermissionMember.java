package its_meow.claimit.api.permission;

public class ClaimPermissionMember extends ClaimPermission {
	
	/** @param parsedUniqueName - The UNIQUE name that will be used in commands and displayed to the user for this permission 
	 *  @param helpInfo - Information to be displayed in the help entry for this permission. **/
	public ClaimPermissionMember(String parsedUniqueName, String helpInfo) {
		super(parsedUniqueName, EnumPermissionType.MEMBER, helpInfo);
	}
	
}

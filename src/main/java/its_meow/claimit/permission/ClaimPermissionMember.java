package its_meow.claimit.permission;

public class ClaimPermissionMember extends ClaimPermission {
	
	/** @param parsedUniqueName - The UNIQUE name that will be used in commands and displayed to the user for this permission **/
	public ClaimPermissionMember(String parsedUniqueName) {
		super(parsedUniqueName, EnumPermissionType.TOGGLE);
	}
	
}

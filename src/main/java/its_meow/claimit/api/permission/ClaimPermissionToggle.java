package its_meow.claimit.api.permission;

public class ClaimPermissionToggle extends ClaimPermission {
	
	public boolean defaultValue;

	/** @param parsedUniqueName - The UNIQUE name that will be used in commands and displayed to the user for this permission 
	 *  @param defaultValue - The value a claim will use for this toggle by default. **/
	public ClaimPermissionToggle(String parsedUniqueName, boolean defaultValue) {
		super(parsedUniqueName, EnumPermissionType.MEMBER);
		this.defaultValue = defaultValue;
	}
	
}

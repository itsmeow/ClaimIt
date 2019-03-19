package its_meow.claimit.api.permission;

public class ClaimPermissionToggle extends ClaimPermission {
	
	public boolean defaultValue;
	public final boolean force;
	public final boolean toForce;

	/** @param parsedUniqueName - The UNIQUE name that will be used in commands and displayed to the user for this permission 
	 *  @param defaultValue - The value a claim will use for this toggle by default. 
	 *  @param helpInfo - Information to be displayed in the help information for this permission 
	 *  @param force - Set to true to force this toggle to the value of 'toForce' 
	 *  @param toForce - If 'force' is set to true, this value will be the only possible value to load on deserialization. **/
	public ClaimPermissionToggle(String parsedUniqueName, boolean defaultValue, String helpInfo, boolean force, boolean toForce) {
		super(parsedUniqueName, EnumPermissionType.MEMBER, helpInfo);
		this.defaultValue = defaultValue;
		this.force = force;
		this.toForce = toForce;
	}
	
}

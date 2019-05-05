package its_meow.claimit.api.permission;

public class ClaimPermissionToggle extends ClaimPermission {
	
	public final boolean defaultValue;
	protected boolean force;
	protected boolean toForce;

	/** @param parsedUniqueName - The UNIQUE name that will be used in commands and displayed to the user for this permission 
	 *  @param defaultValue - The value a claim will use for this toggle by default. 
	 *  @param helpInfo - Information to be displayed in the help information for this permission 
     **/
	public ClaimPermissionToggle(String parsedUniqueName, boolean defaultValue, String helpInfo) {
		super(parsedUniqueName, EnumPermissionType.MEMBER, helpInfo);
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Gets the value that this is forced to, should be combined with a check to {@link getForceEnabled()}
	 */
    public boolean getForceValue() {
        return force;
    }
    
    /**
     * Sets the value that this will be forced to, should it be enabled
     * @param force - The value
     */
    public void setForceValue(boolean force) {
        this.force = force;
    }
    
    /**
     * Gets whether the forced value is enabled
     * @return True if it is
     */
    public boolean getForceEnabled() {
        return toForce;
    }
    
    /**
     * Sets whether the forced value is enabled
     * @param toForce - True to enable force
     */
    public void setForceEnabled(boolean toForce) {
        this.toForce = toForce;
    }
	
}

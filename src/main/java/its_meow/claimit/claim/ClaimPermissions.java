package its_meow.claimit.claim;

import its_meow.claimit.Ref;
import net.minecraft.util.ResourceLocation;

public class ClaimPermissions {
	
	public static final ClaimPermission MODIFY = new ClaimPermission(new ResourceLocation(Ref.MOD_ID, "modify"), "modify", EnumPermissionType.MEMBER);
	public static final ClaimPermission USE = new ClaimPermission(new ResourceLocation(Ref.MOD_ID, "use"), "use", EnumPermissionType.MEMBER);
	public static final ClaimPermission ENTITY = new ClaimPermission(new ResourceLocation(Ref.MOD_ID, "entity"), "entity", EnumPermissionType.MEMBER);
	public static final ClaimPermission PVP = new ClaimPermission(new ResourceLocation(Ref.MOD_ID, "pvp"), "pvp", EnumPermissionType.MEMBER);
	
	public static void register() {
		ClaimPermissionRegistry.addPermission(MODIFY);
		ClaimPermissionRegistry.addPermission(USE);
		ClaimPermissionRegistry.addPermission(ENTITY);
		ClaimPermissionRegistry.addPermission(PVP);
	}
	
}

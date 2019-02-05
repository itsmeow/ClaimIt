package its_meow.claimit.permission;

import its_meow.claimit.Ref;
import net.minecraft.util.ResourceLocation;

public class ClaimPermissions {
	
	public static final ClaimPermissionMember MODIFY = new ClaimPermissionMember(new ResourceLocation(Ref.MOD_ID, "modify"), "modify");
	public static final ClaimPermissionMember USE = new ClaimPermissionMember(new ResourceLocation(Ref.MOD_ID, "use"), "use");
	public static final ClaimPermissionMember ENTITY = new ClaimPermissionMember(new ResourceLocation(Ref.MOD_ID, "entity"), "entity");
	public static final ClaimPermissionMember PVP = new ClaimPermissionMember(new ResourceLocation(Ref.MOD_ID, "pvp"), "pvp");
	
	public static void register() {
		ClaimPermissionRegistry.addPermission(MODIFY);
		ClaimPermissionRegistry.addPermission(USE);
		ClaimPermissionRegistry.addPermission(ENTITY);
		ClaimPermissionRegistry.addPermission(PVP);
	}
	
}

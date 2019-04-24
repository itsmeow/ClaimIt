package its_meow.claimit.api.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import net.minecraft.entity.player.EntityPlayer;

public class Group {
	
	private final String name;
	/* Package fields for storing data */
	Map<ClaimPermissionMember, ArrayList<UUID>> memberLists;
	ArrayList<UUID> members = new ArrayList<UUID>();
	ArrayList<ClaimArea> claims = new ArrayList<ClaimArea>();
	
	public Group(String name) {
		this.name = name;
		this.memberLists = new HashMap<ClaimPermissionMember, ArrayList<UUID>>();
		for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
			if(!memberLists.containsKey(perm)) {
				this.memberLists.put(perm, new ArrayList<UUID>());
			}
		}
	}
	
	boolean addMember(UUID uuid) {
		return members.add(uuid);
	}
	
	boolean addMember(EntityPlayer player) {
		return addMember(player.getGameProfile().getId());
	}
	
	boolean addMemberPermission(UUID uuid, ClaimPermissionMember permission) {
		return this.memberLists.get(permission).add(uuid);
	}
	
	boolean addMemberPermission(EntityPlayer player, ClaimPermissionMember permission) {
		return addMemberPermission(player.getGameProfile().getId(), permission);
	}
	
	boolean addClaim(ClaimArea claim) {
		return claims.add(claim);
	}
	
	public String getName() {
		return name;
	}
}

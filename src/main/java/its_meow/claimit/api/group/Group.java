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
	/* Private field for storing member UUIDs */
	private Map<ClaimPermissionMember, ArrayList<UUID>> memberLists;
	private ArrayList<UUID> members = new ArrayList<UUID>();
	private ArrayList<ClaimArea> claims = new ArrayList<ClaimArea>();
	
	public Group(String name) {
		this.name = name;
		this.memberLists = new HashMap<ClaimPermissionMember, ArrayList<UUID>>();
		for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
			if(!memberLists.containsKey(perm)) {
				this.memberLists.put(perm, new ArrayList<UUID>());
			}
		}
	}
	
	public boolean addMember(UUID uuid) {
		return members.add(uuid);
	}
	
	public boolean addMember(EntityPlayer player) {
		return addMember(player.getGameProfile().getId());
	}
	
	public boolean addMemberPermission(UUID uuid, ClaimPermissionMember permission) {
		return this.memberLists.get(permission).add(uuid);
	}
	
	public boolean addMemberPermission(EntityPlayer player, ClaimPermissionMember permission) {
		return addMemberPermission(player.getGameProfile().getId(), permission);
	}
	
	public boolean addClaim(ClaimArea claim) {
		return claims.add(claim);
	}
	
	public String getName() {
		return name;
	}
}

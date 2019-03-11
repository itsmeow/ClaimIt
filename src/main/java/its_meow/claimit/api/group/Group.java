package its_meow.claimit.api.group;

import java.util.ArrayList;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import net.minecraft.entity.player.EntityPlayer;

public class Group {
	
	private final String name;
	private ArrayList<UUID> members = new ArrayList<UUID>();
	private ArrayList<ClaimArea> claims = new ArrayList<ClaimArea>();
	
	public Group(String name) {
		this.name = name;
	}
	
	public boolean addMember(UUID uuid) {
		return members.add(uuid);
	}
	
	public boolean addMember(EntityPlayer player) {
		return addMember(player.getGameProfile().getId());
	}
	
	public boolean addClaim(ClaimArea claim) {
		return claims.add(claim);
	}
	
	public String getName() {
		return name;
	}
}

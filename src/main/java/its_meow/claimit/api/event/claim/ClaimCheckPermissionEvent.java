package its_meow.claimit.api.event.claim;

import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

@HasResult
public class ClaimCheckPermissionEvent extends ClaimEvent {
    
    private final EntityPlayer player;
    private final UUID uuid;
    private final ClaimPermissionMember permission;
    
    public ClaimCheckPermissionEvent(ClaimArea claim, EntityPlayer player, ClaimPermissionMember permission) {
        super(claim);
        this.player = player;
        this.permission = permission;
        this.uuid = player.getGameProfile().getId();
    }
    
    public ClaimCheckPermissionEvent(ClaimArea claim, UUID uuid, ClaimPermissionMember permission) {
        super(claim);
        this.uuid = uuid;
        this.permission = permission;
        this.player = null;
    }
    
    public EntityPlayer getCheckedPlayer() {
        return player;
    }
    
    public UUID getUUID() {
        return uuid;
    }
    
    public boolean isPlayer() {
        return player != null;
    }
    
    public ClaimPermissionMember getCheckedPermission() {
        return permission;
    }

}

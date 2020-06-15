package dev.itsmeow.claimit.api.event.claim;

import java.util.UUID;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.permission.ClaimPermissionMember;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

@HasResult
public class ClaimCheckPermissionEvent extends ClaimEvent {
    
    private final UUID uuid;
    private final ClaimPermissionMember permission;
    
    public ClaimCheckPermissionEvent(ClaimArea claim, UUID uuid, ClaimPermissionMember permission) {
        super(claim);
        this.uuid = uuid;
        this.permission = permission;
    }
    
    public UUID getUUID() {
        return uuid;
    }
    
    public ClaimPermissionMember getCheckedPermission() {
        return permission;
    }

}

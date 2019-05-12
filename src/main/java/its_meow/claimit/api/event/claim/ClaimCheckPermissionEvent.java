package its_meow.claimit.api.event.claim;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

@HasResult
public class ClaimCheckPermissionEvent extends ClaimEvent {
    
    private final EntityPlayer player;
    private final ClaimPermissionMember permission;
    
    public ClaimCheckPermissionEvent(ClaimArea claim, EntityPlayer player, ClaimPermissionMember permission) {
        super(claim);
        this.player = player;
        this.permission = permission;
    }
    
    public EntityPlayer getCheckedPlayer() {
        return player;
    }
    
    public ClaimPermissionMember getCheckedPermission() {
        return permission;
    }

}

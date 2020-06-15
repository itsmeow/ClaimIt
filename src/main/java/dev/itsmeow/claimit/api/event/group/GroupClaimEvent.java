package dev.itsmeow.claimit.api.event.group;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.group.Group;

public abstract class GroupClaimEvent extends GroupEvent {
    
    protected final ClaimArea claim;
    
    public GroupClaimEvent(Group group, ClaimArea claim) {
        super(group);
        this.claim = claim;
    }
    
    public ClaimArea getClaim() {
        return claim;
    }

}

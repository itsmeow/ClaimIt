package its_meow.claimit.api.event.group;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.group.Group;

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

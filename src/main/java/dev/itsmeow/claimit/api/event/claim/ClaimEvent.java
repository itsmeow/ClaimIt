package dev.itsmeow.claimit.api.event.claim;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class ClaimEvent extends Event {
    
    protected final ClaimArea claim;
    
    public ClaimEvent(ClaimArea claim) {
        this.claim = claim;
    }
    
    public ClaimArea getClaim() { 
        return claim; 
    }
}

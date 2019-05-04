package its_meow.claimit.api.event;

import its_meow.claimit.api.claim.ClaimArea;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fires when a claim is added using {@link its_meow.claimit.api.claim.ClaimManager#addClaim(ClaimArea)}
 * Canceling stops the claim from being added to the list
 * @author its_meow
 */
@Cancelable
public class EventClaimAdded extends ClaimEvent {

    public EventClaimAdded(ClaimArea claim) {
        super(claim);
    }

}

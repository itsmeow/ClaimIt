package its_meow.claimit.api.event.claim;

import its_meow.claimit.api.claim.ClaimArea;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fires when a claim is added using {@link its_meow.claimit.api.claim.ClaimManager#addClaim(ClaimArea)}
 * Canceling stops the claim from being created.
 * @author its_meow
 */
@Cancelable
public class ClaimCreatedEvent extends ClaimEvent {

    public ClaimCreatedEvent(ClaimArea claim) {
        super(claim);
    }

}

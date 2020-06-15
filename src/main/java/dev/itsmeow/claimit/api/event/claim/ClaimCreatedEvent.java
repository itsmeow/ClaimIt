package dev.itsmeow.claimit.api.event.claim;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fires when a claim is added using {@link dev.itsmeow.claimit.api.claim.ClaimManager#addClaim(ClaimArea)}
 * Canceling stops the claim from being created.
 * @author its_meow
 */
@Cancelable
public class ClaimCreatedEvent extends ClaimEvent {

    public ClaimCreatedEvent(ClaimArea claim) {
        super(claim);
    }

}

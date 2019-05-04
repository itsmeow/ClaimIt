package its_meow.claimit.api.event;

import its_meow.claimit.api.claim.ClaimArea;

/**
 * Fires when a claim is added using {@link its_meow.claimit.api.claim.ClaimManager#addClaim(ClaimArea)}
 * Non-cancelable. Cancel deserialized claims using {@link ClaimDeserializationEvent} and cancel created claims using {@link ClaimCreatedEvent}
 * @author its_meow
 */
public class ClaimCreatedEvent extends ClaimEvent {

    public ClaimCreatedEvent(ClaimArea claim) {
        super(claim);
    }

}

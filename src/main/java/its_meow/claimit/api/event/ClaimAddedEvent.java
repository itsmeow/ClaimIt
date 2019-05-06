package its_meow.claimit.api.event;

import its_meow.claimit.api.claim.ClaimArea;

/**
 * Fires when a claim is added using {@link its_meow.claimit.api.claim.ClaimManager#addClaimToListInsecurely(ClaimArea)} (or any methods that call such)
 * Non-cancelable. Cancel deserialized claims using {@link ClaimDeserializationEvent} and cancel created claims using {@link ClaimCreatedEvent}
 * @author its_meow
 */
public class ClaimAddedEvent extends ClaimEvent {

    public ClaimAddedEvent(ClaimArea claim) {
        super(claim);
    }

}

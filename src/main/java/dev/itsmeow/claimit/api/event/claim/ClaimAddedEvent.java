package dev.itsmeow.claimit.api.event.claim;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.event.claim.serialization.ClaimDeserializationEvent;

/**
 * Fires when a claim is added using {@link dev.itsmeow.claimit.api.claim.ClaimManager#addClaimToListInsecurely(ClaimArea)} (or any methods that call such)
 * Non-cancelable. Cancel deserialized claims using {@link ClaimDeserializationEvent} and cancel created claims using {@link ClaimCreatedEvent}
 * @author its_meow
 */
public class ClaimAddedEvent extends ClaimEvent {

    public ClaimAddedEvent(ClaimArea claim) {
        super(claim);
    }

}

package its_meow.claimit.api.event.claim;

import its_meow.claimit.api.claim.ClaimArea;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fires whenever a claim is deleted. Excludes deserialization. Use {@link ClaimsClearedEvent} to detect when the claim list is cleared prior to deserialization
 * Canceling prevents the claim from being deleted.
 * @author its_meow
 */
@Cancelable
public class ClaimRemovedEvent extends ClaimEvent {

    public ClaimRemovedEvent(ClaimArea claim) {
        super(claim);
    }

}

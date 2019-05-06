package its_meow.claimit.api.event;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.group.Group;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fired when a claim is added to a group - through any source (including deserialization)
 * If canceled, prevents the claim from being added
 * @author its_meow
 */
@Cancelable
public class GroupClaimAddedEvent extends GroupClaimEvent {

    public GroupClaimAddedEvent(Group group, ClaimArea claim) {
        super(group, claim);
    }

}

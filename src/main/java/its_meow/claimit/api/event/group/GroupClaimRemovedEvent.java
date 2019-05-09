package its_meow.claimit.api.event.group;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.group.Group;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fired when a claim is removed from a group - through any source
 * If canceled, prevents the claim from being removed
 * @author its_meow
 */
@Cancelable
public class GroupClaimRemovedEvent extends GroupClaimEvent {

    public GroupClaimRemovedEvent(Group group, ClaimArea claim) {
        super(group, claim);
    }

}

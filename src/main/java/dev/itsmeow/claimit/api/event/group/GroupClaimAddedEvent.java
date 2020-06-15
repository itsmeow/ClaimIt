package dev.itsmeow.claimit.api.event.group;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.group.Group;
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

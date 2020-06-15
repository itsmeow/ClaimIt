package dev.itsmeow.claimit.api.event.group;

import dev.itsmeow.claimit.api.group.Group;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class GroupEvent extends Event {
    
    protected final Group group;
    
    public GroupEvent(Group group) {
        this.group = group;
    }
    
    public Group getGroup() {
        return group;
    }
    
}

package its_meow.claimit.api.event;

import its_meow.claimit.api.group.Group;
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

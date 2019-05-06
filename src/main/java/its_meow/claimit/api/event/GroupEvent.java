package its_meow.claimit.api.event;

import its_meow.claimit.api.group.Group;

public abstract class GroupEvent {
    
    protected final Group group;
    
    public GroupEvent(Group group) {
        this.group = group;
    }
    
    public Group getGroup() {
        return group;
    }
    
}

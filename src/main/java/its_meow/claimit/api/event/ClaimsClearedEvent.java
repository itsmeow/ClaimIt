package its_meow.claimit.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Use subclasses Post and Pre
 * @author its_meow
 */
public class ClaimsClearedEvent {
    
    /**
     * Non-cancelable event that fires before claims are cleared during deserialization.
     * @author its_meow
     */
    public static class Pre extends Event {
        
    }
    
    /**
     * Non-cancelable event that fires after claims are cleared during deserialization.
     * @author its_meow
     */
    public static class Post extends Event {
        
    }
    
}

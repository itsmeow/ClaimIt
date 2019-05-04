package its_meow.claimit.util;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;

public class PageChatStyle extends Style {
    
    public PageChatStyle() {
        this.setClickEvent(new ClickEvent(Action.CHANGE_PAGE, "next") {});
    }
    
}
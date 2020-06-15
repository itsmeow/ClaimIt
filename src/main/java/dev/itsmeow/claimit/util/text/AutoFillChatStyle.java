package dev.itsmeow.claimit.util.text;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class AutoFillChatStyle extends Style {

    public AutoFillChatStyle(String command, boolean showHover, String hoverText) {
        this.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        if(showHover) {
            this.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(hoverText)));
        }
    }

}

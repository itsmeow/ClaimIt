package dev.itsmeow.claimit.util.text;

import javax.annotation.Nullable;

public class PageChatStyle extends CommandChatStyle {
    
    public PageChatStyle(String commandBase, boolean admin, String nextPage, @Nullable String playerFilter) {
        super("/" + commandBase + " " + (admin ? (playerFilter == null ? nextPage : playerFilter + " " + nextPage) : nextPage), true, "Next Page: " + nextPage);
    }
    
}
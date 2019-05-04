package its_meow.claimit.util;

public class ClaimInfoChatStyle extends CommandChatStyle {
    
    public ClaimInfoChatStyle(String claimName) {
        super("/ci claim info " + claimName, true, "Click for info");
    }
    
}
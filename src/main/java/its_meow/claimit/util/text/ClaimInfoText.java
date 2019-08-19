package its_meow.claimit.util.text;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.SubClaimArea;
import its_meow.claimit.util.text.FTC.Form;
import net.minecraft.util.text.TextFormatting;

public class ClaimInfoText extends TextComponentStyled {

    public ClaimInfoText(ClaimArea claim) {
        super(claim.getDisplayedViewName(), claim instanceof SubClaimArea ? new CommandChatStyle("/ci subclaim info " + ((SubClaimArea)claim).getParent().getTrueViewName() + " " + claim.getDisplayedViewName(), true, "Click for info.") : new CommandChatStyle("/ci claim info " + claim.getTrueViewName(), true, "Click for info."));
    }
    
    public ClaimInfoText(ClaimArea claim, TextFormatting color) {
        this(claim);
        this.getStyle().setColor(color);
    }
    
    public ClaimInfoText(ClaimArea claim, Form form) {
        this(claim);
        form.applyToStyle(this.getStyle());
    }
    
    public ClaimInfoText(ClaimArea claim, TextFormatting color, Form form) {
        this(claim);
        form.applyToStyle(this.getStyle().setColor(color));
    }
    
    public ClaimInfoText(ClaimArea claim, Form form, TextFormatting color) {
        this(claim);
        form.applyToStyle(this.getStyle().setColor(color));
    }

}

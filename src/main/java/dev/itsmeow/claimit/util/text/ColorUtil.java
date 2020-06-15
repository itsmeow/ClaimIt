package dev.itsmeow.claimit.util.text;

import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.group.Group;
import dev.itsmeow.claimit.config.ClaimItConfig;
import dev.itsmeow.claimit.util.command.CommandUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ColorUtil {
    
    public static String convertFromColorCodes(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll("&([0-9a-f])", "\u00A7$1");
    }
    
    public static String removeColorCodes(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll("&([0-9a-f])", "");
    }
    
    public static String convertFromColorFormattingCodes(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll("&([0-9a-fkl-or])", "\u00A7$1");
    }
    
    public static String removeColorFormattingCodes(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll("&([0-9a-fkl-or])", "");
    }
    
    public static String convertFromFormattingCodes(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll("&([kl-or])", "\u00A7$1");
    }
    
    public static String removeFormattingCodes(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll("&([kl-or])", "");
    }
    
    public static ITextComponent getGroupTagComponent(Group group) {
        return new TextComponentString(convertFromColorFormattingCodes(ClaimItConfig.tag_prefix + group.getTag() + ClaimItConfig.tag_suffix));
    }
    
    public static String getFormattedClaimMessage(String text, ClaimArea claim) {
        return ColorUtil.convertFromColorFormattingCodes(text).replaceAll("%1", CommandUtils.getNameForUUID(claim.getOwner(), claim.getWorld().getMinecraftServer())).replaceAll("%2", claim.getDisplayedViewName());
    }
    
    public static String removeTextForPermission(String text, boolean canUseColors, boolean canUseFormatting) {
        String output = text;
        if(canUseColors) {
            output = removeColorCodes(output);
        }
        if(canUseFormatting) {
            output = removeFormattingCodes(output);
        }
        return output;
    }
    
    public static String convertTextForPermission(String text, boolean canUseColors, boolean canUseFormatting) {
        String output = text;
        if(canUseColors) {
            output = convertFromColorCodes(output);
        }
        if(canUseFormatting) {
            output = convertFromFormattingCodes(output);
        }
        return output;
    }

}

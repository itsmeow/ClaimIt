package its_meow.claimit.util.text;

import java.util.regex.Pattern;

import its_meow.claimit.api.group.Group;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ColorUtil {
    
    private static final Pattern COLOR_REGEX = Pattern.compile("(?i)&([0-9A-Fa-f])");
    
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
    
    public static ITextComponent getGroupTagComponent(Group group) {
        return (new TextComponentString("[").setStyle(new Style().setColor(TextFormatting.GREEN)).appendSibling(new TextComponentString(convertFromColorCodes(group.getTag()))).appendSibling(new TextComponentString(TextFormatting.GREEN + "]" + TextFormatting.RESET)));
    }

}

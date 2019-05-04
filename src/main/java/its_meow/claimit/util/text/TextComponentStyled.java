package its_meow.claimit.util.text;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;

public class TextComponentStyled extends TextComponentString {

    public TextComponentStyled(String msg, Style style) {
        super(msg);
        this.setStyle(style);
    }

}

package its_meow.claimit.util;

public class TextComponentCommand extends TextComponentStyled {

    public TextComponentCommand(String formatting, String msg) {
        super(formatting + msg, new CommandChatStyle(msg, true, "Click to run"));
    }

}

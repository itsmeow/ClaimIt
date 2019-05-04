package its_meow.claimit.util;

public class TextComponentCommand extends TextComponentStyled {

    public TextComponentCommand(String msg) {
        super(msg, new CommandChatStyle(msg, true, "Click to run"));
    }

}

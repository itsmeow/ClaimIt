package dev.itsmeow.claimit.util.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.command.ICommandSender;

public class CommandHelpRegistry {
    
    private static Map<String, Function<ICommandSender, String>> helpMessages = new HashMap<String, Function<ICommandSender, String>>();
    
    public static void registerHelp(String command, Function<ICommandSender, String> helpFunc) {
        helpMessages.put(command, helpFunc);
    }
    
    public static Function<ICommandSender, String> getHelp(String command) {
        return helpMessages.get(command);
    }
    

    
}

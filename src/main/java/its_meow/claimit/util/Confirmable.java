package its_meow.claimit.util;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface Confirmable {
    
    String getConfirmName();
    
    void doAction(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
    
}

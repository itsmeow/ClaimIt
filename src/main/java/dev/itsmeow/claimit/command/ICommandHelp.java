package dev.itsmeow.claimit.command;

import net.minecraft.command.ICommandSender;

public interface ICommandHelp {
    
    public abstract String getHelp(ICommandSender sender);
    
}

package dev.itsmeow.claimit.util.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface IConfirmable {
    
    String getConfirmName();
    
    void doAction(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
    
}

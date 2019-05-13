package its_meow.claimit.command;

import its_meow.claimit.util.command.CommandUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class CommandCIBaseAdminOnly extends CommandCIBase {

    @Override
    public String getHelp(ICommandSender sender) {
        return CommandUtils.isAdminNoded(sender, "claimit.command." + this.getPermissionString()) ? this.getAdminHelp(sender) : "You do not have permission to use this command.";
    }
    
    public abstract String getAdminHelp(ICommandSender sender);

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return CommandUtils.isAdminNoded(sender, "claimit.command." + this.getPermissionString());
    }

    @Override
    public String getPermissionString() {
        return "claimit.claimblocks.add";
    }

}

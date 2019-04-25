package its_meow.claimit.command.claimit;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.BOLD;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import its_meow.claimit.command.claimit.group.CommandSubGroupClaim;
import its_meow.claimit.command.claimit.group.CommandSubGroupCreate;
import its_meow.claimit.command.claimit.group.CommandSubGroupDelete;
import its_meow.claimit.command.claimit.group.CommandSubGroupPermission;
import its_meow.claimit.command.claimit.group.CommandSubGroupSetName;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandSubGroup extends CommandTreeBase {
    
    public CommandSubGroup() {
        this.addSubcommand(new CommandSubGroupCreate());
        this.addSubcommand(new CommandSubGroupSetName());
        this.addSubcommand(new CommandSubGroupDelete());
        this.addSubcommand(new CommandSubGroupPermission());
        this.addSubcommand(new CommandSubGroupClaim());
    }
    
    @Override
    public String getName() {
        return "group";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group <subcommand>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        super.execute(server, sender, args);
        if(args.length == 0) {
            sendMessage(sender, AQUA + "" + BOLD + "Subcommands: ");
            sendMessage(sender, YELLOW + "/claimit group create");
            sendMessage(sender, YELLOW + "/claimit group info");
            sendMessage(sender, YELLOW + "/claimit group delete");
            sendMessage(sender, YELLOW + "/claimit group list");
            sendMessage(sender, YELLOW + "/claimit group setname");
            sendMessage(sender, YELLOW + "/claimit group permission");
            sendMessage(sender, YELLOW + "/claimit group claim");
        }
    }
    
    private static void sendMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }
    
}

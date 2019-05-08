package its_meow.claimit.command.claimit.help;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.UNDERLINE;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.command.CommandCITreeBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubHelpCommand extends CommandCIBase {

    @Override
    public String getHelp(ICommandSender sender) {
        return "Help command for info on commands. Please, stop asking for help getting help.";
    }

    @Override
    public String getName() {
        return "command";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit help command <choice>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:"  + YELLOW + " /claimit help command <choice>");
            sendCMessage(sender, GREEN, "/claimit help command claimit");
        } else if(args.length >= 1) {
            ICommand cmdI;
            String cmdStr = "";
            for(int i = 0; i < args.length; i++) {
                cmdStr += args[i] + " ";
            }
            cmdStr = cmdStr.trim();
            cmdI = server.getCommandManager().getCommands().get(args[0]);
            if(args.length > 1) {
                String[] args2 = CommandCITreeBase.shiftArgs(args);
                if(cmdI instanceof CommandCITreeBase) {
                    CommandCITreeBase cmd = (CommandCITreeBase) cmdI;
                    cmdI = cmd.getLowestCommandInTree(args2, true).getLeft();
                }
            }
            if(cmdI instanceof CommandCIBase) {
                CommandCIBase cmd = (CommandCIBase) cmdI;
                sendMessage(sender, BLUE + "Help Information for " + GREEN + cmdStr + BLUE + ":");
                sendMessage(sender, GOLD + cmd.getHelp(sender));
                if(cmd instanceof CommandCITreeBase) {
                    CommandCITreeBase tree = (CommandCITreeBase) cmd;
                    tree.displaySubCommands(server, sender);
                }
            } else if(cmdI != null) {
                sendMessage(sender, RED + "No information for command \"" + cmdStr + "\"");
            } else {
                sendMessage(sender, RED + "No such command!");
            }
        }
    }

}

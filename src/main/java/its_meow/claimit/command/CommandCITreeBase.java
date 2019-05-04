package its_meow.claimit.command;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.BOLD;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import its_meow.claimit.util.TextComponentCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.command.CommandTreeBase;

public abstract class CommandCITreeBase extends CommandTreeBase {

    public CommandCITreeBase(CommandBase... subcommands) {
        for(CommandBase cmd : subcommands) {
            this.addSubcommand(cmd);
        }
    }

    /**
     * Callback for when the command is executed
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length < 1) {
            this.displaySubCommands(server, sender);
        } else {
            ICommand cmd = getSubCommand(args[0]);

            if(cmd == null) {
                this.executeBaseCommand(server, sender, args);
            } else if(!cmd.checkPermission(server, sender)) {
                throw new CommandException("commands.generic.permission");
            } else {
                cmd.execute(server, sender, shiftArgs(args));
            }
        }
    }

    protected void displaySubCommands(MinecraftServer server, ICommandSender sender) throws CommandException {
        sendMessage(sender, AQUA + "" + BOLD + "Subcommands: ");
        for(ICommand subCmd : this.getSubCommands()) {
            sendCMessage(sender, YELLOW, this.getUsage(sender).substring(0, this.getUsage(sender).lastIndexOf(this.getName()) + this.getName().length()) + " " + subCmd.getName());
        }
    }

    protected void executeBaseCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        this.displaySubCommands(server, sender);
    }

    protected static String[] shiftArgs(@Nullable String[] s) {
        if(s == null || s.length == 0) {
            return new String[0];
        }

        String[] s1 = new String[s.length - 1];
        System.arraycopy(s, 1, s1, 0, s1.length);
        return s1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        ICommand command = this.getLowestCommandInTree(args);
        if(command == this) {
            List<String> list = new ArrayList<String>();
            if(args.length == 1) {
                for(ICommand subCommand : this.getSubCommands()) {
                    if(subCommand.getName().startsWith(args[0])) {
                        list.add(subCommand.getName());
                    }
                }
            }
            if(list.isEmpty()) {
                for(ICommand subCommand : this.getSubCommands()) {
                    list.add(subCommand.getName());
                }
            }
            return list;
        } else {
            return command.getTabCompletions(server, sender, shiftArgs(args), pos);
        }
    }

    protected ICommand getLowestCommandInTree(String[] args) {
        ICommand cmd = this;
        boolean endTree = false;
        while(!endTree) {
            // This command has subcommands
            if(cmd instanceof CommandCITreeBase) {
                if(args.length > 0) {
                    ICommand newCmd = ((CommandCITreeBase) cmd).getSubCommand(args[0]);
                    if(newCmd != null) {
                        // This has subcommands with this name, continue and get completions for subcommand
                        cmd = newCmd;
                        args = shiftArgs(args);
                    } else {
                        // This has no subcommand with this name, get completions for specific command
                        endTree = true;
                    }
                } else {
                    // This is a tree, but no subcommands were found
                    endTree = true;
                }
            } else {
                // This command has no subcommands, get completions for subcommand
                endTree = true;
            }
        }
        return cmd;
    }

    protected static void sendMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

    protected static void sendCMessage(ICommandSender sender, TextFormatting formatting, String message) {
        sendCMessage(sender, formatting.toString(), message);
    }

    protected static void sendCMessage(ICommandSender sender, String formatting, String message) {
        sender.sendMessage(new TextComponentCommand(formatting, message));
    }

}

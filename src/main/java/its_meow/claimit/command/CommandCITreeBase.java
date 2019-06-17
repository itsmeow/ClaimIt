package its_meow.claimit.command;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import its_meow.claimit.util.command.CommandHelpRegistry;
import its_meow.claimit.util.text.AutoFillHelpChatStyle;
import its_meow.claimit.util.text.FTC.Form;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public abstract class CommandCITreeBase extends CommandCIBase {

    private final Map<String, CommandCIBase> commandMap = new HashMap<>();
    private final Map<String, CommandCIBase> commandAliasMap = new HashMap<>();

    public CommandCITreeBase(CommandCIBase... subcommands) {
        for(CommandCIBase cmd : subcommands) {
            this.addSubcommand(cmd);
        }
        
    }

    public void addSubcommand(CommandCIBase command) {
        commandMap.put(command.getName(), command);
        for(String alias : command.getAliases()) {
            commandAliasMap.put(alias, command);
        }

        CommandHelpRegistry.registerHelp(this.getName() + " " + command.getName(), command::getHelp);
        for(String alias : this.getAliases()) {
            CommandHelpRegistry.registerHelp(alias + " " + command.getName(), command::getHelp);
        }
    }

    public Collection<CommandCIBase> getSubCommands() {
        return commandMap.values();
    }

    @Nullable
    public CommandCIBase getSubCommand(String command) {
        CommandCIBase cmd = commandMap.get(command);
        if(cmd != null) {
            return cmd;
        }
        return commandAliasMap.get(command);
    }

    public Map<String, CommandCIBase> getCommandMap() {
        return Collections.unmodifiableMap(commandMap);
    }

    public List<CommandCIBase> getSortedCommandList() {
        List<CommandCIBase> list = new ArrayList<>(getSubCommands());
        Collections.sort(list);
        return list;
    }

    /**
     * Callback for when the command is executed
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length < 1) {
            this.executeBaseCommand(server, sender, args);
        } else {
            CommandCIBase cmd = getSubCommand(args[0]);

            if(cmd == null) {
                this.executeBaseCommand(server, sender, args);
            } else if(!cmd.checkPermission(server, sender)) {
                throw new CommandException("commands.generic.permission");
            } else {
                cmd.execute(server, sender, shiftArgs(args));
            }
        }
    }

    public void displaySubCommands(MinecraftServer server, ICommandSender sender) throws CommandException {
        sendMessage(sender, AQUA, Form.BOLD, "Subcommands: ");
        for(CommandCIBase subCmd : this.getSubCommands()) {
            String cmd = this.getUsage(sender).substring(0, this.getUsage(sender).indexOf(this.getName() + " ") + this.getName().length()) + " " + subCmd.getName();
            sendSMessage(sender, cmd, new AutoFillHelpChatStyle(cmd, subCmd, sender).setColor(YELLOW));
        }
    }

    protected void executeBaseCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        this.displaySubCommands(server, sender);
    }

    public static String[] shiftArgs(@Nullable String[] s) {
        if(s == null || s.length == 0) {
            return new String[0];
        }

        String[] s1 = new String[s.length - 1];
        System.arraycopy(s, 1, s1, 0, s1.length);
        return s1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        Pair<CommandCIBase, String[]> pair = this.getLowestCommandInTree(args, false);
        CommandCIBase command = pair.getLeft();
        if(command == this) {
            List<String> list = new ArrayList<String>();
            if(args.length == 1) {
                for(CommandCIBase subCommand : this.getSubCommands()) {
                    if(subCommand.getName().startsWith(args[0])) {
                        list.add(subCommand.getName());
                    }
                }
            }
            if(list.isEmpty()) {
                for(CommandCIBase subCommand : this.getSubCommands()) {
                    list.add(subCommand.getName());
                }
            }
            return list;
        } else {
            return command.getTabCompletions(server, sender, pair.getRight(), pos);
        }
    }

    public Pair<CommandCIBase, String[]> getLowestCommandInTree(String[] args, boolean extraArgsNull) {
        CommandCIBase cmd = this;
        boolean endTree = false;
        while(!endTree) {
            // This command has subcommands
            if(cmd instanceof CommandCITreeBase) {
                if(args.length > 0) {
                    CommandCIBase newCmd = ((CommandCITreeBase) cmd).getSubCommand(args[0]);
                    if(newCmd != null) {
                        // This has subcommands with this name, continue and get completions for subcommand
                        cmd = newCmd;
                        args = shiftArgs(args);
                    } else {
                        // This has no subcommand with this name, get completions for specific command
                        if(extraArgsNull) {
                            cmd = null;
                        }
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
        return Pair.of(cmd, args);
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if(index > 0 && args.length > 1) {
            CommandCIBase cmd = getSubCommand(args[0]);
            if(cmd != null) {
                return cmd.isUsernameIndex(shiftArgs(args), index - 1);
            }
        }

        return false;
    }

}

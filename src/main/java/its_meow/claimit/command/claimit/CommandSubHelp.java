package its_meow.claimit.command.claimit;

import static net.minecraft.util.text.TextFormatting.*;

import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.api.permission.ClaimPermissionToggle;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.command.CommandCITreeBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubHelp extends CommandCIBase {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit help <command/permission/topic>";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "The help command. Are you okay? Asking for help on getting help while also getting help? What are you doing? Put another command to view help on it.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:");
            sendCMessage(sender, GREEN, "/claimit help command");
            sendCMessage(sender, GREEN, "/claimit help permissions");
            sendCMessage(sender, GREEN, "/claimit help topics");
        }

        if(args.length >= 1) {
            String choice = args[0];
            // Basic tree
            if(choice.equalsIgnoreCase("command")) {
                if(args.length == 1) {
                    sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:"  + YELLOW + " /claimit help command <choice>");
                    sendCMessage(sender, GREEN, "/claimit help command claimit");
                } else if(args.length >= 2) {
                    ICommand cmdI;
                    String cmdStr = "";
                    for(int i = 1; i < args.length; i++) {
                        cmdStr += args[i] + " ";
                    }
                    cmdStr = cmdStr.trim();
                    cmdI = server.getCommandManager().getCommands().get(args[1]);
                    if(args.length > 2) {
                        String[] args2 = new String[args.length - 2];
                        for(int i = 2; i < args.length; i++) {
                            args2[i - 2] = args[i];
                        }
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
            if(args.length == 1) {
                if(choice.equalsIgnoreCase("permission")) {
                    sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:" + YELLOW + " /claimit help <choice>");
                    sendMessage(sender, GREEN + "member");
                    sendMessage(sender, GREEN + "toggle");
                }
                if(choice.equalsIgnoreCase("topic")) {
                    sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:" + YELLOW + " /claimit help <choice>");
                    sendMessage(sender, GREEN + "creating");
                    sendMessage(sender, GREEN + "members");
                    sendMessage(sender, GREEN + "toggles");
                    sendMessage(sender, GREEN + "management");
                }

                // Second tree - permissions
                if(choice.equalsIgnoreCase("memberperms")) {
                    sendMessage(sender, GOLD + "" + UNDERLINE + "Member Permissions:");
                    for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
                        sendMessage(sender, GREEN + perm.parsedName + ": " + YELLOW + perm.helpInfo);
                    }
                }
                if(choice.equalsIgnoreCase("toggleperms")) {
                    sendMessage(sender, GOLD + "" + UNDERLINE + "Toggle Permissions:");
                    for(ClaimPermissionToggle perm : ClaimPermissionRegistry.getTogglePermissions()) {
                        sendMessage(sender, GREEN + perm.parsedName + ": " + YELLOW + perm.helpInfo + BLUE + " (Default: " + perm.defaultValue + ")");
                    }
                }
            }
            //Second tree - topics


        }
    }

}

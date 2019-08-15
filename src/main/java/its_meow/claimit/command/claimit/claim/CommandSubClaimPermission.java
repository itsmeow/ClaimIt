package its_meow.claimit.command.claimit.claim;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.command.CommandCITreeBase;
import its_meow.claimit.command.claimit.claim.permission.CommandSubClaimPermissionList;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.FTC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubClaimPermission extends CommandCITreeBase {

    public CommandSubClaimPermission() {
        super(new CommandSubClaimPermissionList());
    }

    @Override
    public String getName() {
        return "permission";
    }

    @Override
    public List<String> getAliases() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("perm");
        list.add("player");
        return list;
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Both a tree command and a command. Add or remove members from a claim. Subcommand 'list' exists. First required argument is add or remove. Second is a member permission. Third is a username. Fourth, optional argument is a claim name. Otherwise, your current location is used.";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claim permission <add/remove> <permission> <username> [claimname]";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length == 3;
    }

    @Override
    public void executeBaseCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length < 3 || args.length > 4) {
            throw new CommandException("Improper argument count! Usage: \n" + YELLOW + this.getUsage(sender));
        }
        String action = args[0].toLowerCase();
        String permissionStr = args[1];
        String username = args[2];
        String claimName = null;
        if(args.length == 4) {
            claimName = args[3];
        }
        if(!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
            throw new CommandException("Invalid action! Specify add or remove. Usage: \n" + YELLOW + this.getUsage(sender));
        }
        ClaimPermissionMember permission = CommandUtils.getPermissionMember(permissionStr, "\n" + YELLOW + this.getUsage(sender));
        UUID id = CommandUtils.getUUIDForName(username, server);
        ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(claimName, sender);

        if(claim != null) {
            if(!CommandUtils.isAdminWithNodeOrManage(sender, claim, "claimit.command.claimit.claim.permission.others")) {
                throw new CommandException("You cannot modify members of this claim!");
            }
            if(action.equals("add"))  {
                // Add user
                if(claim.addMember(id, permission)) {
                    sendMessage(sender, new FTC(GREEN, "Successfully added "), new FTC(YELLOW, username), new FTC(GREEN, " to claim "), new FTC(DARK_GREEN, claim.getDisplayedViewName()), new FTC(GREEN, " with permission "), new FTC(AQUA, permission.parsedName));
                } else {
                    sendMessage(sender, YELLOW, "This player already has that permission!");
                }
            } else if(action.equals("remove")) {
                // Remove user
                if(claim.removeMember(id, permission)) {
                        sendMessage(sender, new FTC(GREEN, "Successfully removed permission "), new FTC(AQUA, permission.parsedName), new FTC(GREEN, " from user "), new FTC(YELLOW, username), new FTC(GREEN, " in claim "), new FTC(DARK_GREEN, claim.getDisplayedViewName()));
                } else {
                    sendMessage(sender, YELLOW, "This player does not have that permission!");
                }
            } else {
                throw new CommandException("Invalid action! Specify add or remove. Usage: \n" + YELLOW + this.getUsage(sender));
            }
        } else {
            if(claimName != null && !claimName.equals("")) {
                sendMessage(sender, RED, "No claim with this name was found.");
            } else {
                sendMessage(sender, RED, "There is no claim here! Specify a name to get a specific claim.");
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        List<String> list = new ArrayList<String>();
        if(args.length == 1) {
            list.add("add");
            list.add("remove");
            list.add("list");
            return CommandBase.getListOfStringsMatchingLastWord(args, list);
        } else if(args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getMemberPermissions(list));
        } else if(args.length == 3) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getPossiblePlayers(list, server, sender, args));
        } else if(args.length == 4) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(list, sender));
        }
        return list;
    }

    @Override
    public String getPermissionString() {
        return "claimit.claim.permission";
    }


}

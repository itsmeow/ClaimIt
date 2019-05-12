package its_meow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubGroupClaim extends CommandCIBase {

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group claim <add/remove> <groupname> [claimname]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Adds a claim to a group. You must own the claim. First argument is add or remove, second is groupname. Third, optional, claim name. Otherwise uses location. Adding a claim gives members their group permissions in that claim.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if(args.length > 3 || args.length < 2) {
            throw new WrongUsageException("Improper argument count! Usage: " + this.getUsage(sender));
        }
        String action = args[0].toLowerCase();
        String groupName = args[1];
        String claimName = null;
        if(args.length == 3) {
            claimName = args[2];
        }
        if(!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
            throw new WrongUsageException("Invalid action! Specify add or remove. Usage: " + this.getUsage(sender));
        }


        Group group = GroupManager.getGroup(groupName);
        if(group != null) {
            ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(claimName, sender);
            if(claim != null) {
                if(action.equals("add"))  {
                    // Add claim
                    if(!group.hasClaim(claim)) {
                        if(CommandUtils.equivalentOwnerWithNode(sender, claim, "claimit.claim.manage.others")) {
                            if(CommandUtils.isAdminNoded(sender, "claimit.command.claimit.group.claim.others") || (sender instanceof EntityPlayer) && group.getMembers().containsKey(((EntityPlayer) sender).getGameProfile().getId())) {
                                group.addClaim(claim);
                                sendMessage(sender, GREEN + "Successfully added claim " + YELLOW + claim.getDisplayedViewName() + GREEN + " to group " + DARK_GREEN + groupName);
                                sendMessage(sender, YELLOW + "Please make sure you trust " + CommandUtils.getNameForUUID(group.getOwner(), server) + " and the people they trust, as they will have full permission in this claim, as well as the ability to add more people!");
                            } else {
                                sendMessage(sender, RED + "You cannot add claims to a group you are not a member of!");
                            }
                        } else {
                            sendMessage(sender, RED + "You do not own this claim!");
                        }
                    } else {
                        sendMessage(sender, YELLOW + "This claim is already present!");
                    }
                } else if(action.equals("remove")) {
                    // Remove claim
                    if(group.hasClaim(claim)) {
                        if(CommandUtils.isAdminNoded(sender, "claimit.command.claimit.group.claim.others") || (sender instanceof EntityPlayer && claim.isOwner((EntityPlayer)sender))) {
                            group.removeClaim(claim);
                            sendMessage(sender, GREEN + "Successfully removed claim " + YELLOW + claim.getDisplayedViewName() + GREEN + " from group " + DARK_GREEN + groupName);
                        } else {
                            sendMessage(sender, RED + "You do not own this claim!");
                        }
                    } else {
                        sendMessage(sender, YELLOW + "This claim is not in this group!");
                    }
                } else {
                    throw new WrongUsageException("Invalid action! Specify add or remove. Usage: " + this.getUsage(sender));
                }
            } else {
                sendMessage(sender, RED + "No claim " + (args.length == 3 ? "with this name that you own found!" : "at your location!"));
            }
        } else {
            sendMessage(sender, RED + "No such group: " + groupName);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "add", "remove");
        } else if(args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getRelevantGroupNames(sender));
        } else if(args.length == 3) {
            return CommandBase.getListOfStringsMatchingLastWord(args, CommandUtils.getOwnedClaimNames(null, sender));
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    protected String getPermissionString() {
        return "claimit.group.claim";
    }

}

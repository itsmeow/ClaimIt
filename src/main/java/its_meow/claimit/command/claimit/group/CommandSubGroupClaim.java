package its_meow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.util.CommandUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubGroupClaim extends CommandBase {

    public CommandSubGroupClaim() {

    }

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group claim <add/remove> <groupname> [claimname]";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length == 3;
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
            claimName = args[1];
        }
        if(!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
            throw new WrongUsageException("Invalid action! Specify add or remove. Usage: " + this.getUsage(sender));
        }

        if(sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            Group group = GroupManager.getGroup(groupName);
            if(group != null) {
                ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(claimName, sender);
                if(claim != null) {
                    if(action.equals("add"))  {
                        // Add claim
                        if(!group.hasClaim(claim)) {
                            if(claim.isOwner(player)) {
                                group.addClaim(claim);
                                sendMessage(sender, GREEN + "Successfully added claim " + YELLOW + claim.getDisplayedViewName() + GREEN + " to group " + DARK_GREEN + groupName);
                            } else {
                                sendMessage(sender, RED + "You do not own this claim!");
                            }
                        } else {
                            sendMessage(sender, YELLOW + "This claim is already present!");
                        }
                    } else if(action.equals("remove")) {
                        // Remove claim
                        if(group.hasClaim(claim)) {
                            if(claim.isOwner(player)) {
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
                    sendMessage(sender, RED + "No claim " + (args.length == 3 ? "with this name found!" : "at your location!"));
                }
            } else {
                sendMessage(sender, RED + "No such group: " + groupName);
            }
        } else {
            sendMessage(sender, "You must be a player to use this command!");
        }
    }

    private static void sendMessage(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

}

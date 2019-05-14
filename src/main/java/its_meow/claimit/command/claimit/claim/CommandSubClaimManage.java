package its_meow.claimit.command.claimit.claim;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.BOLD;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.ITALIC;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.UNDERLINE;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.api.permission.ClaimPermissionRegistry;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.ClaimInfoChatStyle;
import its_meow.claimit.util.text.CommandChatStyle;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubClaimManage extends CommandCIBase {

    @Override
    public String getHelp(ICommandSender sender) {
        return "Provides clickable management links for a claim. Defaults to location, but accepts claim name argument.";
    }

    @Override
    public String getName() {
        return "manage";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claim manage [claimname] [member] [playername]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String cName = null;
        if(args.length == 1) {
            cName = args[0];
        }
        ClaimArea claim = CommandUtils.getClaimWithNameOrLocation(cName, sender);
        if(claim != null) {
            if(CommandUtils.isAdminWithNodeOrManage(sender, claim, "claimit.command.claimit.claim.manage.others")) {
                String fName = (CommandUtils.isAdmin(sender) ? claim.getTrueViewName() : claim.getDisplayedViewName());
                if(args.length < 1) {
                    sendMessage(sender, BLUE + "" + BOLD + "Management for " + GREEN + claim.getDisplayedViewName() + BLUE + ":");
                    sendSMessage(sender, ITALIC + "" + UNDERLINE + "" + YELLOW + "View Info", new ClaimInfoChatStyle(fName));
                    if(CommandUtils.isAdminWithNodeOrOwner(sender, claim, "claimit.command.claimit.claim.delete.others")) {
                        sendSMessage(sender, ITALIC + "" + UNDERLINE + "" + RED + "Delete", new CommandChatStyle("/claimit claim delete " + fName, true, "Click to delete"));
                    }
                    if(claim.getMembers().keySet().size() > 0) {
                        sendMessage(sender, BLUE + "Members (click to manage):");
                        claim.getMembers().keySet().forEach(uuid -> {
                            String pName = CommandUtils.getNameForUUID(uuid, server);
                            sendSMessage(sender, ITALIC + "" + UNDERLINE + "" + YELLOW + pName, new CommandChatStyle("/claimit claim manage " + fName + " member " + pName, true, "Click to manage permissions"));
                        });
                    }
                    Set<Group> groups = GroupManager.getGroupsForClaim(claim);
                    if(groups.size() > 0) {
                        sendMessage(sender, BLUE + "Groups (click to remove):");
                        groups.forEach(group -> {
                            sendSMessage(sender, ITALIC + "" + UNDERLINE + "" + YELLOW + group.getName(), new CommandChatStyle("/claimit group claim remove " + group.getName() + " " + fName, true, "Click to remove from group"));
                        });
                    }
                } else if(args.length >= 3 && args[1].equals("member") && CommandUtils.getUUIDForName(args[2], server) != null) {
                    sendMessage(sender, BLUE + "" + BOLD + "Member management for " + YELLOW + args[2] + BLUE + " in " + GREEN + claim.getDisplayedViewName() + BLUE + ":");
                    if(args.length == 3) {
                        sendSMessage(sender, ITALIC + "" + UNDERLINE + "" + GREEN + "Add Permission", new CommandChatStyle("/claimit claim manage " + fName + " member " + args[2] + " add", true, "Click to add permission"));
                        sendSMessage(sender, ITALIC + "" + UNDERLINE + "" + RED + "Remove Permission", new CommandChatStyle("/claimit claim manage " + fName + " member " + args[2] + " remove", true, "Click to remove permission"));
                    } else if(args.length == 4 && (args[3].equals("add") || args[3].equals("remove"))) {
                        sendMessage(sender, BLUE + "" + ITALIC + "Choose permission to " + args[3] + ":");
                        UUID uuid = CommandUtils.getUUIDForName(args[2], server);
                        for(ClaimPermissionMember perm : ClaimPermissionRegistry.getMemberPermissions()) {
                            boolean isInList = claim.inPermissionList(perm, uuid);
                            if((args[3].equals("add") && !isInList) || (args[3].equals("remove") && isInList)) {
                                sendSMessage(sender, BLUE + " - " + ITALIC + "" + UNDERLINE + "" + YELLOW + perm.parsedName, new CommandChatStyle("/claimit claim permission " + args[3] + " " + perm.parsedName + " " + args[2] + " " + fName, true, "Click to " + args[3]));
                            }
                        }
                    }
                }
            } else {
                sendMessage(sender, RED + "You do have permission to manage this claim!");
            }
        } else {
            sendMessage(sender, RED + (cName == null ? "No claim at your location!" : "No claim with this name!"));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
            BlockPos targetPos) {
        if(CommandUtils.isAdmin(sender)) {
            return super.getTabCompletions(server, sender, args, targetPos);
        } else {
            return CommandUtils.getOwnedClaimNames(null, sender);
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.claim.manage";
    }

}

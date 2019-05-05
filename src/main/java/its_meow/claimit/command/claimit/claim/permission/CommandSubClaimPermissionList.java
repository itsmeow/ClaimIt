package its_meow.claimit.command.claimit.claim.permission;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.CommandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandSubClaimPermissionList extends CommandCIBase {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit claim permission list [claimname]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Lists member permissions within a claim. Argument one is optional claim name. Defaults to location.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ClaimManager m = ClaimManager.getManager();
        if(args.length == 0) {
            if(sender instanceof EntityPlayer) {
                ClaimArea claim = m.getClaimAtLocation(sender.getEntityWorld(), sender.getPosition());
                if(claim != null) {
                    outputMembers(claim, sender);
                } else {
                    throw new CommandException("There is no claim here! Specify a name. Usage: " + this.getUsage(sender));
                }
            } else {
                throw new CommandException("Must specify name as non-player.");
            }
        } else if(args.length == 1) {
            ClaimArea claim = CommandUtils.getClaimWithName(args[0], sender);
            if(claim != null) {
                outputMembers(claim, sender);
            } else {
                throw new CommandException("There is no claim with this name!");
            }
        } else {
            throw new CommandException("Too many arguments! Usage: " + this.getUsage(sender));
        }
    }

    private static void outputMembers(ClaimArea claim, ICommandSender sender) throws CommandException {
        Map<UUID, HashSet<ClaimPermissionMember>> permMap = claim.getMembers();
        if(sender instanceof EntityPlayer) {
            if(!claim.canManage((EntityPlayer) sender)) {
                throw new CommandException("You cannot view the members of this claim!");
            }
        }
        if(permMap == null || permMap.isEmpty()) {
            throw new CommandException("This claim has no members.");
        }
        for(UUID member : permMap.keySet()) {
            String permString = "";
            HashSet<ClaimPermissionMember> permSet = permMap.get(member);
            for(ClaimPermissionMember p : permSet) {
                permString += p.parsedName + ", ";
            }
            int end = permString.lastIndexOf(',');
            permString = permString.substring(0, end);
            sendMessage(sender, YELLOW + ClaimManager.getPlayerName(member, sender.getEntityWorld()) + BLUE + ":" + GREEN + permString);
        }
    }

}

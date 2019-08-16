package its_meow.claimit.command.claimit.subclaim.permission;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSetMultimap;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.SubClaimArea;
import its_meow.claimit.api.permission.ClaimPermissionMember;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.FTC;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSubSubClaimPermissionList extends CommandCIBase {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit subclaim permission list [claimname) (subclaimname]";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Lists member permissions within a subclaim. Argument one and two are optional claim/subclaim name. Defaults to location.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        SubClaimArea subClaim = CommandUtils.getSubClaimWithNamesOrLocation(0, args, sender);
        outputMembers(subClaim, sender);
    }

    private static void outputMembers(ClaimArea claim, ICommandSender sender) throws CommandException {
        ImmutableSetMultimap<UUID,ClaimPermissionMember> permMap = claim.getMembers();
        if(!CommandUtils.isAdminWithNodeOrManage(sender, claim, "claimit.subclaim.permission.list.others")) {
            throw new CommandException("You cannot view the members of this subclaim!");
        }
        if((permMap == null || permMap.isEmpty())) {
            sendMessage(sender, RED, "This subclaim has no members.");
        } else {
            for(UUID uuid : permMap.keySet()) {
                sendMessage(sender, new FTC(YELLOW, CommandUtils.getNameForUUID(uuid, sender.getEntityWorld().getMinecraftServer())), new FTC(BLUE, " - "), new FTC(GREEN, getMemberLine(uuid, permMap.get(uuid))));
            }
        }
    }

    private static String getMemberLine(UUID member, Set<ClaimPermissionMember> permSet) {
        String permString = "";
        for(ClaimPermissionMember p : permSet) {
            permString += p.parsedName + BLUE + ", " + GREEN;
        }
        int end = permString.lastIndexOf(',');
        return permString.substring(0, end);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1 || args.length == 2) {
            return CommandUtils.getSubclaimCompletions(null, 0, args, sender);
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.subclaim.permission.list";
    }

}

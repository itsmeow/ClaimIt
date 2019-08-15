package its_meow.claimit.command.claimit.subclaim;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

import its_meow.claimit.api.AdminManager;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.claim.SubClaimArea;
import its_meow.claimit.api.permission.ClaimPermissions;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.FTC;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandSubSubClaimSetName extends CommandCIBase {

    @Override
    public String getName() {
        return "setname";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit subclaim setname <name>";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Sets the name of the subclaim you are currently located in to the first argument (required).";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) { // ci subclaim setname (name)
            if(sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(player.world, player.getPosition());
                if(claim != null) {
                    SubClaimArea subClaim = claim.getSubClaimAtLocation(player.getPosition());
                    if(subClaim != null) {
                        if(CommandUtils.isAdminWithNodeOrOwner(sender, claim, "claimit.command.claimit.subclaim.setname.others") || claim.hasPermission(player, ClaimPermissions.MANAGE_PERMS)) {
                            boolean pass = subClaim.setViewName(args[0]);
                            if(pass) {
                                sendMessage(sender, new FTC(AQUA, "Set this subclaim's name to: "), new FTC(GREEN, subClaim.getDisplayedViewName()));
                                if(AdminManager.isAdmin(player)) {
                                    sendMessage(sender, new FTC(AQUA, "Set this subclaim's true name to: "), new FTC(GREEN, subClaim.getTrueViewName()));
                                }
                            } else {
                                sendMessage(sender, RED, "Failed to set name. There is another subclaim in this claim with this name.");
                            }
                        } else {
                            sendMessage(sender, RED, "You cannot rename this subclaim!");
                        }
                    } else {
                        sendMessage(sender, RED, "There is no subclaim here!");
                    }
                } else {
                    sendMessage(sender, RED, "No claim here!");
                }
            } else {
                sendMessage(sender, RED, "You must be a player to use this command!");
            }
        } else {
            throw new SyntaxErrorException("Specify a name with no spaces. Usage: " + this.getUsage(sender));
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.subclaim.setname";
    }

}

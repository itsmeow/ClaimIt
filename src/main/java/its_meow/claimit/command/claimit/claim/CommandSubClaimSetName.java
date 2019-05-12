package its_meow.claimit.command.claimit.claim;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

import its_meow.claimit.AdminManager;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.command.CommandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandSubClaimSetName extends CommandCIBase {

	@Override
	public String getName() {
		return "setname";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit claim setname <name>";
	}
	
    @Override
    public String getHelp(ICommandSender sender) {
        return "Sets the name of the claim you are currently located in to the first argument (required).";
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 1) { // ci claim setname (name)
			if(sender instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) sender;
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(player.world, player.getPosition());
				if(claim != null) {
					if(CommandUtils.isAdminWithNodeOrOwner(sender, claim, "claimit.command.claimit.claim.setname.others")) {
						boolean pass = claim.setViewName(args[0]);
						if(pass) {
							sendMessage(sender, AQUA + "Set this claim's name to: " + GREEN + claim.getDisplayedViewName());
							if(AdminManager.isAdmin(player)) {
								sendMessage(sender, AQUA + "Set this claim's true name to: " + GREEN + claim.getTrueViewName());
							}
						} else {
							sendMessage(sender, RED + "Failed to set name. There is another claim " + (AdminManager.isAdmin(player) ? "this player owns" : "you own") + " with this name.");
						}
					} else {
						sendMessage(sender, RED + "You cannot rename this claim!");
					}
				} else {
					sendMessage(sender, RED + "No claim there or you don't own the claim!");
				}
			} else {
				sendMessage(sender, "You must be a player to use this command!");
			}
		} else {
			throw new SyntaxErrorException("Specify a name with no spaces. Usage: " + this.getUsage(sender));
		}
	}

    @Override
    protected String getPermissionString() {
        return "claimit.claim.setname";
    }

}

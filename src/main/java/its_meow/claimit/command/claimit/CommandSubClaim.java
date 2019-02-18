package its_meow.claimit.command.claimit;

import its_meow.claimit.command.claimit.claim.CommandSubClaimDelete;
import its_meow.claimit.command.claimit.claim.CommandSubClaimDeleteAll;
import its_meow.claimit.command.claimit.claim.CommandSubClaimInfo;
import its_meow.claimit.command.claimit.claim.CommandSubClaimList;
import its_meow.claimit.command.claimit.claim.CommandSubClaimMember;
import its_meow.claimit.command.claimit.claim.CommandSubClaimSetName;
import its_meow.claimit.command.claimit.claim.CommandSubClaimToggle;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;
import static net.minecraft.util.text.TextFormatting.*;

public class CommandSubClaim extends CommandTreeBase {
	
	public CommandSubClaim() {
		this.addSubcommand(new CommandSubClaimInfo());
		this.addSubcommand(new CommandSubClaimDelete());
		this.addSubcommand(new CommandSubClaimList());
		this.addSubcommand(new CommandSubClaimSetName());
		this.addSubcommand(new CommandSubClaimMember());
		this.addSubcommand(new CommandSubClaimDeleteAll());
		this.addSubcommand(new CommandSubClaimToggle());
	}
	
	@Override
	public String getName() {
		return "claim";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit claim <subcommand>";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		super.execute(server, sender, args);
		if(args.length == 0) {
			sendMessage(sender, AQUA + "" + BOLD + "Subcommands: ");
			sendMessage(sender, YELLOW + "/claimit claim info");
			sendMessage(sender, YELLOW + "/claimit claim delete");
			sendMessage(sender, YELLOW + "/claimit claim list");
			sendMessage(sender, YELLOW + "/claimit claim setname");
			sendMessage(sender, YELLOW + "/claimit claim member");
			sendMessage(sender, YELLOW + "/claimit claim deleteall");
			sendMessage(sender, YELLOW + "/claimit claim toggle");
		}
	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}
	
}

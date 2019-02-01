package its_meow.claimit.common.command.claimit;

import its_meow.claimit.common.command.claimit.claim.CommandSubClaimDelete;
import its_meow.claimit.common.command.claimit.claim.CommandSubClaimInfo;
import its_meow.claimit.common.command.claimit.claim.CommandSubClaimList;
import its_meow.claimit.common.command.claimit.claim.CommandSubClaimMember;
import its_meow.claimit.common.command.claimit.claim.CommandSubClaimSetName;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandSubClaim extends CommandTreeBase {
	
	public CommandSubClaim() {
		this.addSubcommand(new CommandSubClaimInfo());
		this.addSubcommand(new CommandSubClaimDelete());
		this.addSubcommand(new CommandSubClaimList());
		this.addSubcommand(new CommandSubClaimSetName());
		this.addSubcommand(new CommandSubClaimMember());
	}
	
	@Override
	public String getName() {
		return "claim";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "claimit claim <subcommand>";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		super.execute(server, sender, args);
		if(args.length == 0) {
			sendMessage(sender, "§b§lSubcommands: ");
			sendMessage(sender, "§e/claimit claim info");
			sendMessage(sender, "§e/claimit claim delete");
			sendMessage(sender, "§e/claimit claim list");
			sendMessage(sender, "§e/claimit claim setname");
			sendMessage(sender, "§e/claimit claim member");
		}
	}
	
	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}
	
}

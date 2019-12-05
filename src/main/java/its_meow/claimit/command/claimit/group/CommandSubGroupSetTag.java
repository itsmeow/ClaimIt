package its_meow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.RED;

import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.config.ClaimItConfig;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimit.util.text.ColorUtil;
import its_meow.claimit.util.text.FTC;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandSubGroupSetTag extends CommandCIBase {

    @Override
    public String getName() {
        return "settag";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group settag <groupname> <tag>";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Sets the tag of a group. Tags must be unique. Only group owner may change the tag. First argument is the group's name. Second argument is the tag to set.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 2) {
            String groupname = args[0];
            String tag = args[1];
            Group group = GroupManager.getGroup(groupname);
            if(group != null) {
                if(CommandUtils.isAdminNoded(sender, "claimit.command.claimit.group.settag.others") || (sender instanceof EntityPlayer && group.isOwner((EntityPlayer)sender))) {
                    if(ColorUtil.removeColorCodes(tag).length() <= ClaimItConfig.max_tag_length && ColorUtil.removeColorCodes(tag).length() >= ClaimItConfig.min_tag_length) {
                        boolean pass = GroupManager.setGroupTag(group, tag);
                        if(pass) {
                            sendMessage(sender, new FTC(AQUA, "Set this group's tag to: "), ColorUtil.getGroupTagComponent(group));
                        } else {
                            sendMessage(sender, RED, "Failed to set tag. There is another group with this tag.");
                        }
                    } else {
                        sendMessage(sender, RED, "Tag must be within " + ClaimItConfig.min_tag_length + " and " + ClaimItConfig.max_tag_length + " characters!");
                    }
                } else {
                    sendMessage(sender, RED, "You do not own this group!");
                }
            } else {
                sendMessage(sender, RED, "There is no group with this name!");
            }
        } else {
            throw new SyntaxErrorException("Invalid syntax. Usage: " + this.getUsage(sender));
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.group.settag";
    }

}
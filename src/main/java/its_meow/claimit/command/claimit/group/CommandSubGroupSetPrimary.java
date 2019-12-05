package its_meow.claimit.command.claimit.group;

import static net.minecraft.util.text.TextFormatting.RED;

import java.util.UUID;

import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.serialization.ClaimItGlobalDataSerializer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

public class CommandSubGroupSetPrimary extends CommandCIBase {

    @Override
    public String getName() {
        return "setprimary";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit group setprimary <groupname>";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Sets a primary group. This group determines your tag.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) {
            if(!(sender instanceof EntityPlayer)) {
                throw new CommandException("You must be a player to set a primary group!");
            }
            Group group = GroupManager.getGroup(args[0]);
            if(group != null) {
                UUID uuid = ((EntityPlayer) sender).getGameProfile().getId();
                if(group.getMembers().keySet().contains(uuid) || group.isOwner(uuid)) {
                    NBTTagCompound data = ClaimItGlobalDataSerializer.get().data;
                    NBTTagCompound tag;
                    if(!data.hasKey("PRIMARY_GROUPS", Constants.NBT.TAG_COMPOUND)) {
                        tag = new NBTTagCompound();
                    } else {
                        tag = data.getCompoundTag("PRIMARY_GROUPS");
                    }
                    tag.setString(uuid.toString(), group.getName());
                    data.setTag("PRIMARY_GROUPS", tag);
                    sendMessage(sender, TextFormatting.GREEN, "Set primary group to: " + TextFormatting.YELLOW + group.getName());
                } else {
                    sendMessage(sender, RED, "You are not part of this group!");
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
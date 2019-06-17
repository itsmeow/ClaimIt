package its_meow.claimit.command.claimit;

import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

import its_meow.claimit.api.AdminManager;
import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.TeleportUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

public class CommandSubAdmin extends CommandCIBase {

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit admin";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 4 || !args[0].equals("tpdimxz")) {
            if(sender instanceof EntityPlayer) {
                if(this.checkPermission(server, sender)) {
                    if(AdminManager.isAdmin((EntityPlayer) sender)) {
                        AdminManager.removeAdmin((EntityPlayer) sender);
                        sendMessage(sender, GREEN, "Admin bypass disabled.");
                    } else {
                        AdminManager.addAdmin((EntityPlayer) sender);
                        sendMessage(sender, GREEN, "Admin bypass enabled. You may now manage all claims.");
                    }
                } else {
                    sendMessage(sender, RED, "You do not have permission to use this command!");
                }
            } else {
                sendBMessage(sender, "You must be a player to use this command!");
            }
        } else if(args.length == 4 && args[0].equals("tpdimxz") && sender instanceof EntityPlayerMP && sender.canUseCommand(2, "minecraft.command.tp")) {
            try {
                EntityPlayerMP p = (EntityPlayerMP) sender;
                int dim = Integer.valueOf(args[1]);
                int x = Integer.valueOf(args[2]);
                int z = Integer.valueOf(args[3]);
                int y = TeleportUtils.findNearestYLiquidOrSolid(DimensionManager.getWorld(dim), x, z).getY();
                TeleportUtils.moveIfDifferentID(server, p, dim);
                p.setLocationAndAngles(x, y, z, p.rotationYaw, p.rotationPitch);
                p.setPositionAndUpdate(x, y, z);
                p.setRotationYawHead(p.rotationYawHead);
                p.motionX = 0.0D;
                p.motionY = 0.0D;
                p.motionZ = 0.0D;
            } catch(NullPointerException | NumberFormatException e) {
                sendBMessage(sender, "Invalid coordinates for teleportation.");
            }
        } else {
            throw new CommandException("You do not have permission to teleport!");
        }
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Toggles admin mode on or off (if you have permission to do so), which allows server level management of ClaimIt.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getPermissionString());
    }

    @Override
    public String getPermissionString() {
        return "claimit.admin";
    }

}

package dev.itsmeow.claimit.command.claimit.help;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import dev.itsmeow.claimit.api.userconfig.UserConfigType;
import dev.itsmeow.claimit.api.userconfig.UserConfigTypeRegistry;
import dev.itsmeow.claimit.api.userconfig.UserConfigType.UserConfig;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.text.FTC;
import dev.itsmeow.claimit.util.text.FTC.Form;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubHelpUserConfig extends CommandCIBase {

    @Override
    public String getHelp(ICommandSender sender) {
        return "Gives information on registered user configs.";
    }

    @Override
    public String getName() {
        return "userconfig";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit help userconfig [configname]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            sendMessage(sender, GOLD, Form.UNDERLINE, "User Configs:");
            for(Class<?> clazz : UserConfigTypeRegistry.getRegistries().keySet()) {
                UserConfigType<?> type = UserConfigTypeRegistry.getRegistries().get(clazz);
                for(UserConfig<?> config : type.getConfigs().values()) {
                    sendMessage(sender, new FTC(config.parsedName, GREEN), new FTC(" - " + "(Default " + config.defaultValue.toString() + ") - ", BLUE), new FTC(config.helpInfo, YELLOW));
                }
            }
        } else if(args.length == 1) {
            String cfgName = args[0];
            boolean hit = false;
            for(Class<?> clazz : UserConfigTypeRegistry.getRegistries().keySet()) {
                UserConfigType<?> type = UserConfigTypeRegistry.getRegistries().get(clazz);
                UserConfig<?> config = type.getConfig(cfgName);
                if(config != null) {
                    sendMessage(sender, new FTC(config.parsedName, GREEN), new FTC(" - " + "(Default " + config.defaultValue.toString() + ") - ", BLUE), new FTC(config.helpInfo, YELLOW));
                    hit = true;
                }
            }
            if(!hit) {
                throw new CommandException("No config with that name found!");
            }
        } else {
            throw new CommandException("Too many arguments! Usage: " + this.getUsage(sender));
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.help.userconfig";
    }

}

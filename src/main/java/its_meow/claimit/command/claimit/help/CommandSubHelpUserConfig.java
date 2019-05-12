package its_meow.claimit.command.claimit.help;

import static net.minecraft.util.text.TextFormatting.BLUE;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.UNDERLINE;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.userconfig.UserConfigType;
import its_meow.claimit.userconfig.UserConfigTypeRegistry;
import its_meow.claimit.userconfig.UserConfigType.UserConfig;
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
            sendMessage(sender, GOLD + "" + UNDERLINE + "User Configs:");
            for(Class<?> clazz : UserConfigTypeRegistry.getRegistries().keySet()) {
                UserConfigType<?> type = UserConfigTypeRegistry.getRegistries().get(clazz);
                for(UserConfig<?> config : type.getConfigs().values()) {
                    sendMessage(sender, GREEN + config.parsedName + BLUE + " - " + "(Default " + config.defaultValue.toString() + ") - " + YELLOW + config.helpInfo);
                }
            }
        } else if(args.length == 1) {
            String cfgName = args[0];
            boolean hit = false;
            for(Class<?> clazz : UserConfigTypeRegistry.getRegistries().keySet()) {
                UserConfigType<?> type = UserConfigTypeRegistry.getRegistries().get(clazz);
                UserConfig<?> config = type.getConfig(cfgName);
                if(config != null) {
                    sendMessage(sender, GREEN + config.parsedName + BLUE + " - " + "(Default " + config.defaultValue.toString() + ") - " + YELLOW + config.helpInfo);
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
    protected String getPermissionString() {
        return "claimit.help.userconfig";
    }

}

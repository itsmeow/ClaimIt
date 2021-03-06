package dev.itsmeow.claimit.command.claimit;

import java.util.ArrayList;
import java.util.List;

import dev.itsmeow.claimit.api.userconfig.UserConfigType;
import dev.itsmeow.claimit.api.userconfig.UserConfigTypeRegistry;
import dev.itsmeow.claimit.api.userconfig.UserConfigType.UserConfig;
import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.text.FTC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class CommandSubConfig extends CommandCIBase {

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit config <config> <value>";
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "User configuration command. With no arguments, lists your configurations. Specify a configuration to view its value. Specify a configuration and a value as arguments to set the value.";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(sender instanceof EntityPlayer) {
            if(args.length > 2) {
                throw new WrongUsageException("Too many arguments!");
            }
            EntityPlayer player = (EntityPlayer) sender;
            if(args.length == 0) {
                for(Class<?> clazz : UserConfigTypeRegistry.getRegistries().keySet()) {
                    UserConfigType<?> type = UserConfigTypeRegistry.getRegistries().get(clazz);
                    for(UserConfig<?> config : type.getConfigs().values()) {
                        Object value = type.storage.getValueFor((UserConfig) config, player.getGameProfile().getId());
                        if(value == null) {
                            value = config.defaultValue;
                        }
                        sendMessage(player, new FTC(TextFormatting.YELLOW, config.parsedName), new FTC(TextFormatting.BLUE, ": "), new FTC(TextFormatting.GREEN, value.toString()));
                    }
                }
            } else if(args.length == 1) {
                String parsedName = args[0];
                boolean hit = false;
                for(Class<?> clazz : UserConfigTypeRegistry.getRegistries().keySet()) {
                    UserConfigType<?> type = UserConfigTypeRegistry.getRegistries().get(clazz);
                    UserConfig<?> config = type.getConfig(parsedName);
                    if(config != null) {
                        Object value = type.storage.getValueFor((UserConfig) config, player.getGameProfile().getId());
                        if(value == null) {
                            value = config.defaultValue;
                        }
                        sendMessage(player, new FTC(TextFormatting.YELLOW, config.parsedName), new FTC(TextFormatting.BLUE, ": "), new FTC(TextFormatting.GREEN, value.toString()));
                        hit = true;
                    }
                }
                if(!hit) {
                    throw new CommandException("Invalid config name!");
                }
            } else if(args.length == 2) {
                String parsedName = args[0];
                String valueStr = args[1];
                boolean hit = false;
                for(Class<?> clazz : UserConfigTypeRegistry.getRegistries().keySet()) {
                    UserConfigType<?> type = UserConfigTypeRegistry.getRegistries().get(clazz);
                    UserConfig<?> config = type.getConfig(parsedName);
                    if(config != null) {
                        if(type.isValidValue(valueStr)) {
                            type.setValue((UserConfig) config, player.getGameProfile().getId(), valueStr);
                            sendMessage(player, TextFormatting.GREEN, "Set " + parsedName + " to " + valueStr);
                            hit = true;
                        } else {
                            throw new CommandException("Invalid value for config " + parsedName + " of " + valueStr + "!");
                        }
                    }
                }
                if(!hit) {
                    throw new CommandException("Invalid config name!");
                }
            }
        } else {
            throw new CommandException("You must be a player to use this command!");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 1) {
            ArrayList<String> configs = UserConfigTypeRegistry.getRegistries().values().stream().collect(ArrayList<String>::new, (l, t) -> l.addAll(t.getConfigs().keySet()), (r, r1) -> r1.addAll(r));
            return CommandBase.getListOfStringsMatchingLastWord(args, configs);
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.config";
    }


}

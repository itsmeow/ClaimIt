package its_meow.claimit.command.claimit;

import its_meow.claimit.api.userconfig.UserConfig;
import its_meow.claimit.api.userconfig.UserConfigBoolean;
import its_meow.claimit.api.userconfig.UserConfigFloat;
import its_meow.claimit.api.userconfig.UserConfigManager;
import its_meow.claimit.api.userconfig.UserConfigRegistry;
import its_meow.claimit.api.userconfig.UserConfigString;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSubConfig extends CommandBase {

	@Override
	public String getName() {
		return "config";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/claimit config <config> <value>";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			if(args.length > 2) {
				throw new WrongUsageException("Too many arguments!");
			}
			EntityPlayer player = (EntityPlayer) sender;
			if(args.length == 0) {
				for(String name : UserConfigRegistry.getConfigs().keySet()) {
					UserConfig<?> config = UserConfigRegistry.getConfig(name);
					if(config.defaultValue instanceof Boolean) {
						Boolean value = UserConfigManager.getManager().get(player.getGameProfile().getId(), (UserConfigBoolean) config);
						sendMessage(player, config.parsedName + ": " + value.toString());
					}
					if(config.defaultValue instanceof Float) {
						Float value = UserConfigManager.getManager().get(player.getGameProfile().getId(), (UserConfigFloat) config);
						sendMessage(player, config.parsedName + ": " + value.toString());
					}
					if(config.defaultValue instanceof String) {
						String value = UserConfigManager.getManager().get(player.getGameProfile().getId(), (UserConfigString) config);
						sendMessage(player, config.parsedName + ": " + value);
					}
				}
			}
			if(args.length == 1) {
				String cfgName = args[0];
				UserConfig<?> config = UserConfigRegistry.getConfig(cfgName);
				if(config == null) {
					throw new CommandException("Invalid config name! Possible values: " + UserConfigRegistry.getValidConfigList());
				} else {
					if(config.defaultValue instanceof Boolean) {
						Boolean value = UserConfigManager.getManager().get(player.getGameProfile().getId(), (UserConfigBoolean) config);
						sendMessage(player, config.parsedName + ": " + value.toString());
					}
					if(config.defaultValue instanceof Float) {
						Float value = UserConfigManager.getManager().get(player.getGameProfile().getId(), (UserConfigFloat) config);
						sendMessage(player, config.parsedName + ": " + value.toString());
					}
					if(config.defaultValue instanceof String) {
						String value = UserConfigManager.getManager().get(player.getGameProfile().getId(), (UserConfigString) config);
						sendMessage(player, config.parsedName + ": " + value);
					}
				}
			}
			if(args.length == 2) {
				String cfgName = args[0];
				UserConfig<?> config = UserConfigRegistry.getConfig(cfgName);
				String valueStr = args[1];
				if(config == null) {
					throw new CommandException("Invalid config name! Possible values: " + UserConfigRegistry.getValidConfigList());
				} else {
					if(config.defaultValue instanceof Boolean) {
						if(valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
							Boolean bool = Boolean.valueOf(valueStr);
							UserConfigManager.getManager().<Boolean>set(player.getGameProfile().getId(), (UserConfig<Boolean>) config, bool);
							sendMessage(player, "Set " + cfgName + " to " + valueStr);
						} else {
							throw new CommandException("Invalid value for config " + cfgName + " of " + valueStr + "\nSpecify: true or false");
						}
					}
					if(config.defaultValue instanceof Float) {
						try {
							if(Float.valueOf(valueStr) != null) {
								Float floatV = Float.valueOf(valueStr);
								UserConfigManager.getManager().<Float>set(player.getGameProfile().getId(), (UserConfig<Float>) config, floatV);
								sendMessage(player, "Set " + cfgName + " to " + valueStr);
							} else {
								throw new CommandException("Invalid value for config " + cfgName + " of " + valueStr + "\nSpecify: A number");
							}
						} catch (NumberFormatException e) {
							throw new CommandException("Invalid value for config " + cfgName + " of " + valueStr + "\nSpecify: A number");
						}
					}
					if(config.defaultValue instanceof String) {

					}
				}
			}
		} else {
			throw new CommandException("You must be a player to use this command!");
		}
	}

	private static void sendMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

}

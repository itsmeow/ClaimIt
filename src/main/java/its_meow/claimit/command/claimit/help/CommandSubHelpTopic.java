package its_meow.claimit.command.claimit.help;

import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.UNDERLINE;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import its_meow.claimit.command.CommandCIBase;
import its_meow.claimit.util.text.CommandChatStyle;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubHelpTopic extends CommandCIBase {
    
    static {
        TopicHelpRegistry.addSingleLine("userconfigs", YELLOW + "User configs are configurable named values that are saved per-player. They can control anything and everything. They have types (boolean, number, string, etc).");
        TopicHelpRegistry.addSingleLine("memberperms", YELLOW + "Member permissions are a subclass of permission that can be assigned to a player in a group or claim. Different member perms grant different permissions and abilities.");
    }

    @Override
    public String getHelp(ICommandSender sender) {
        return "Gives information on various topics. Running with no args will give choices.";
    }

    @Override
    public String getName() {
        return "topic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/claimit help topic <topic>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            sendMessage(sender, GOLD + "" + UNDERLINE + "Available choices:"  + YELLOW + " /claimit help topic <choice>");
            for(String topic : TopicHelpRegistry.getTopics()) {
                sendSMessage(sender, YELLOW + topic, new CommandChatStyle("/claimit help topic " + topic, true, "Click to view info"));
            }
        } else if(args.length == 1) {
            String topicName = args[0];
            if(TopicHelpRegistry.getInfo(topicName).size() > 0) {
                sendMessage(sender, GOLD + "" + UNDERLINE + "Info for " + topicName);
                for(String line : TopicHelpRegistry.getInfo(topicName)) {
                    sendMessage(sender, line);
                }
            } else {
                throw new CommandException("No such topic!");
            }
        } else {
            throw new CommandException("Too many arguments! Usage: " + this.getUsage(sender));
        }
    }
    
    public static class TopicHelpRegistry {

        private static final ListMultimap<String, String> lines = MultimapBuilder.hashKeys().arrayListValues().build();

        public static void addSingleLine(String topicName, String info) {
            lines.removeAll(topicName);
            lines.put(topicName, info);
        }
        
        public static void addMultiLine(String topicName, String... infoLines) {
            lines.removeAll(topicName);
            for(String str : infoLines) {
                lines.put(topicName, str);
            }
        }
        
        public static List<String> getInfo(String topicName) {
            return lines.get(topicName);
        }
        
        public static Set<String> getTopics() {
            return lines.keySet();
        }

    }

}

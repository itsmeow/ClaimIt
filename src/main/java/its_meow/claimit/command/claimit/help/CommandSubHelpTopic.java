package its_meow.claimit.command.claimit.help;

import static net.minecraft.util.text.TextFormatting.*;

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
        TopicHelpRegistry.addSingleLine("toggleperms", YELLOW + "Toggle permissions are a subclass of permission that can be enabled or disabled per claim, and are stored per claim. There can also be toggle perms sharing names with member perms. Toggling these on makes that member permission publicly available. E.g. toggling the modify toggle will allow anyone to place things.");
        TopicHelpRegistry.addMultiLine("claiming", YELLOW + "Claiming is a fairly simple process.", 
                YELLOW + "First, retrieve shears or whatever the server has configured to be the claim tool",
                YELLOW + "Next, put it in your hand and find the two corners between which you want to claim.",
                YELLOW + "Now, right click the block at your first corner. If the first corner works it should say so in chat.",
                YELLOW + "Finally, right click the second corner. If you were allowed to make a claim and it was not overlapping, then it should say the claim was created!",
                YELLOW + "Use '" + GREEN + "/claimit claim info" + YELLOW + "' for more info! You can set your claim name with '" + GREEN + "/ci claim setname <name>" + YELLOW + "' while standing in your claim");
        TopicHelpRegistry.addMultiLine("permissions", YELLOW + "You've heard what the permissions are, (" + GREEN + "/claimit help permission" + YELLOW + ") but how do I let my friends place things and open doors?", 
                YELLOW + "Well, you've come to the right place!",
                YELLOW + "The first thing to do is identify the name of or move to the claim you wish to add a user to.",
                YELLOW + "Now, identify what permissions you want to add (modify for place/break, entity for livestock and mobs, use for doors and chests)",
                YELLOW + "Finally, mix all this info together and run a command (hint: hit tab for autocomplete): " + GREEN + "/claimit claim permission add <permission> <playername> [claimname]", 
                YELLOW + "Now your friends should have permissions! You can use the same command but with 'remove' instead of 'add' to remove troublemakers.");
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

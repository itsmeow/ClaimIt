package dev.itsmeow.claimit.command.claimit.help;

import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import dev.itsmeow.claimit.command.CommandCIBase;
import dev.itsmeow.claimit.util.text.CommandChatStyle;
import dev.itsmeow.claimit.util.text.FTC;
import dev.itsmeow.claimit.util.text.FTC.Form;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandSubHelpTopic extends CommandCIBase {

    static {
        TopicHelpRegistry.add("userconfigs", 
                new FTC[] { 
                    new FTC("User configs are configurable named values that are saved per-player. They can control anything and everything. They have types (boolean, number, string, etc).", YELLOW)
                }, 
                new FTC[] { 
                    new FTC("For more information: ", YELLOW), 
                    new FTC("/claimit help userconfig", GREEN)
                }
        );
        TopicHelpRegistry.add("memberperms", 
                new FTC[] { 
                    new FTC("Member permissions are a subclass of permission that can be assigned to a player in a group or claim. Different member perms grant different permissions and abilities.", YELLOW)
                }, 
                new FTC[] { 
                    new FTC("For more information: ", YELLOW), 
                    new FTC("/claimit help permission member", GREEN)
                }
        );
        TopicHelpRegistry.add("toggleperms", 
                new FTC[] { 
                    new FTC("Toggle permissions are a subclass of permission that can be enabled or disabled per claim, and are stored per claim. There can also be toggle perms sharing names with member perms.", YELLOW)
                },
                new FTC[] { 
                    new FTC("Toggling these on makes that member permission publicly available. E.g. toggling the modify toggle will allow anyone to place things.", YELLOW)
                }, 
                new FTC[] { 
                    new FTC("For more information: ", YELLOW), 
                    new FTC("/claimit help permission toggle", GREEN)
                }
        );
        TopicHelpRegistry.add("claiming", 
                new FTC[] { 
                    new FTC("Claiming is a fairly simple process.", YELLOW) 
                }, 
                new FTC[] { 
                    new FTC("First, retrieve shears or whatever the server has configured to be the claim tool", YELLOW) },
                new FTC[] { 
                    new FTC("Next, put it in your hand and find the two corners between which you want to claim.", YELLOW)
                },
                new FTC[] { 
                    new FTC("Now, right click the block at your first corner. If the first corner works it should say so in chat.", YELLOW)
                },
                new FTC[] { 
                    new FTC("Finally, right click the second corner. If you were allowed to make a claim and it was not overlapping, then it should say the claim was created!", YELLOW)
                },
                new FTC[] { 
                    new FTC("Use '", YELLOW), 
                    new FTC("/claimit claim info", GREEN), 
                    new FTC("' for more info! You can set your claim name with '", YELLOW), 
                    new FTC("/ci claim setname <name>", GREEN), 
                    new FTC("' while standing in your claim", YELLOW)
                }
        );
        TopicHelpRegistry.add("permissions", 
                new FTC[] { 
                    new FTC("You've heard what the permissions are, (", YELLOW),
                    new FTC("/claimit help permission", GREEN),
                    new FTC("), and what they do (see memberperms and toggleperms topics!), but how do I let my friends place things and open doors?", YELLOW)
                }, 
                new FTC[] { 
                    new FTC("Well, you've come to the right place!", YELLOW)
                },
                new FTC[] {
                    new FTC("The first thing to do is identify the name of or move to the claim you wish to add a user to.", YELLOW)
                },
                new FTC[] {
                    new FTC("Now, identify what permissions you want to add (modify for place/break, entity for livestock and mobs, use for doors and chests)", YELLOW)
                },
                new FTC[] {
                    new FTC("Finally, mix all this info together and run a command (hint: hit tab for autocomplete): ", YELLOW), new FTC("/claimit claim permission add <permission> <playername> [claimname]", GREEN)
                },
                new FTC[] {
                    new FTC("Now your friends should have permissions! You can use the same command but with 'remove' instead of 'add' to remove troublemakers.", YELLOW)
                }
        );
        TopicHelpRegistry.add("groups", 
                new FTC[] {
                    new FTC("Do you have a lot of friends? Tired of spamming the add command? Want people to be able to add others while gone?", YELLOW)
                },
                new FTC[] {
                    new FTC("Even if you haven't got a ton a friends, I've got the thing for you! Groups! With groups you can manage a user's permissions in multiple claims as well as share claims via groups.", YELLOW)
                },
                new FTC[] {
                        new FTC("The idea is you add members to a group first. Give them member permissions that you want them to have in ALL claims that are eventually added to the group.", YELLOW)
                },
                new FTC[] {
                        new FTC("Now that you have your members, add a claim to the group. This will give members their permissions in this claim. Keep in mind those with manage_perms will be able to meddle with claims, so be careful.", YELLOW)
                },
                new FTC[] {
                        new FTC("Also, you have to directly own a claim to add it to a group, since the owner automatically gets all permissions. Claims can be removed by the owner at any time.", YELLOW)
                }
        );
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
            sendMessage(sender, new FTC("Available choices:", GOLD, Form.UNDERLINE), new FTC(" /claimit help topic <choice>", YELLOW, Form.UNDERLINE));
            for(String topic : TopicHelpRegistry.getTopics()) {
                sendSMessage(sender, topic, new CommandChatStyle("/claimit help topic " + topic, true, "Click to view info").setColor(YELLOW));
            }
        } else if(args.length == 1) {
            String topicName = args[0];
            if(TopicHelpRegistry.getInfo(topicName).size() > 0) {
                sendMessage(sender, new FTC("Info for " + topicName, GOLD, Form.UNDERLINE));
                for(FTC[] line : TopicHelpRegistry.getInfo(topicName)) {
                    sendMessage(sender, line);
                }
            } else {
                throw new CommandException("No such topic!");
            }
        } else {
            throw new CommandException("Too many arguments! Usage: " + this.getUsage(sender));
        }
    }

    @Override
    public String getPermissionString() {
        return "claimit.help.topic";
    }

    public static class TopicHelpRegistry {

        private static final ListMultimap<String, FTC[]> lines = MultimapBuilder.hashKeys().arrayListValues().build();

        public static void add(String topicName, FTC[]... infoLines) {
            lines.removeAll(topicName);
            for(FTC[] str : infoLines) {
                lines.put(topicName, str);
            }
        }

        public static List<FTC[]> getInfo(String topicName) {
            return lines.get(topicName);
        }

        public static Set<String> getTopics() {
            return lines.keySet();
        }

    }

}

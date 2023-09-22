package dev.scyye.rosalina.commands.impl;

import dev.scyye.rosalina.Main;
import dev.scyye.rosalina.StringUtils;
import dev.scyye.rosalina.commands.MacroCommand;
import dev.scyye.rosalina.commands.TextCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Macro extends TextCommand {
	@Override
	public String getName() {
		return "macro";
	}

	@Override
	public String getHelp() {
		return "Macro commands. (Subcommands not implemented into the help gui yet)";
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		String command = args[0];

		switch (command) {
			case "run" -> {
				String user = "";
				String macro = "";
				if (StringUtils.contains(args[1], "1", "2", "3", "4", "5", "6", "7", "8", "9", "0")) {
					user = args[1];
					macro = args[2];
				} else {
					macro = args[1];
				}

				handleMacro(new String[]{macro, user}, event.getChannel(), event.getMessage());
				event.getMessage().delete().queue();
				break;
			}

			case "add" -> {
				String input = args[1];
				String output = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
				var macro = new MacroCommand.Macro(input, output);
				event.getChannel().sendMessage(String.format("""
						Added macro.
						**Input**:
						%s
						**Output**:
						%s
						
											
						**ID**:
						%s
						""", macro.getInput(), macro.getOutput(), macro.getId().toString())).queue();
				break;
			}

			case "remove" -> {
				String id = args[1];
				MacroCommand.Macro macro = MacroCommand.Macro.retrieveById(UUID.fromString(id));
				if (macro == null) {
					event.getMessage().reply("Unknown macro: `" + id + "` please provide a valid one.").queue();
					return;
				}
				Main.getInstance().getConfig().macros.remove(macro);
				event.getMessage().reply("Removed macro: `" + id + "`").queue();
				break;
			}

			case "list" -> {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("**Macros**")
						.setAuthor("Rosalina's Script Star");
				for (MacroCommand.Macro macro : Main.getInstance().getConfig().macros) {
					builder.addField(macro.getInput(), macro.getOutput() + "\n\n" + macro.getId(), macro.getOutput().length()<57);
				}
				event.getMessage().replyEmbeds(builder.build()).queue();
			}

			default -> {
				String user = "";
				String macro;
				if (StringUtils.contains(args[0], "1", "2", "3", "4", "5", "6", "7", "8", "9", "0")) {
					user = args[0];
					macro = args[1];
				} else {
					macro = args[0];
				}

				handleMacro(new String[]{macro, user}, event.getChannel(), event.getMessage());
				event.getMessage().delete().queue();
				break;
			}
		}
	}


	static void handleMacro(String[] args, MessageChannel channel, @Nullable Message message) {
		String s = "";
		if (args.length > 0)
			s = args[0];
		String user = "";
		User userObj = null;
		if (args.length > 1) {
			user = args[1];
			try {
				userObj = Main.getInstance().getJda().retrieveUserById(user).complete();
			} catch (Exception ignored) {
			}
		}

		if (s.isEmpty()) {
			assert message != null;
			message.reply("Error: Please provide a macro!").complete().delete().queueAfter(2, TimeUnit.SECONDS);
			message.delete().queue();

			return;
		}

		if (user.equals("everyone") || user.equals("here") || user.startsWith("&")) {
			channel.sendMessage("NICE TRY BOZO!!!").queue();
			return;
		}


		MacroCommand.Macro macro;

		try {
			macro = MacroCommand.Macro.retrieveById(UUID.fromString(s));
		} catch (IllegalArgumentException ignored) {}


		macro = MacroCommand.Macro.getByName(s);




		if (macro == null) {
			channel.sendMessage("Unknown macro: `" + s + "` please provide a valid one.").queue();
			return;
		}
		channel.sendMessage((user.isEmpty() || userObj == null ? "" : userObj.getAsMention() + ",\n") + macro.getOutput()).queue();
	}
}

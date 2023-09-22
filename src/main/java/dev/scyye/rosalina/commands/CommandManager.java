package dev.scyye.rosalina.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends ListenerAdapter {
	public static List<TextCommand> commands = new ArrayList<>();

	public static void add(TextCommand command) {
		commands.add(command);
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		String[] splitMessage = event.getMessage().getContentRaw().split(" ");
		if (!splitMessage[0].startsWith("!")) return;
		splitMessage[0] = splitMessage[0].replaceFirst("!", "");
		for (TextCommand command : commands) {
			if (command.getName().equalsIgnoreCase(splitMessage[0])) {
				System.out.println("Command found: " + command.getName());
				command.execute(event, Arrays.copyOfRange(splitMessage,
						1, splitMessage.length));
				return;
			}
		}
		event.getMessage().reply("Command not found. [`"+splitMessage[0]+"`]").queue();
	}
}

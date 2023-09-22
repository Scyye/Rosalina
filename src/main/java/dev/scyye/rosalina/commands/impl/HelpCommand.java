package dev.scyye.rosalina.commands.impl;

import dev.scyye.rosalina.commands.CommandManager;
import dev.scyye.rosalina.commands.TextCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class HelpCommand extends TextCommand {

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getHelp() {
		return "Displays this menu.";
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("**Help Menu**")
				.setAuthor("Scyye")
				.setColor(Color.CYAN)
		;

		for (TextCommand command : CommandManager.commands) {
			builder.addField(command.getName(), command.getHelp(), false);
		}

		event.getMessage().replyEmbeds(builder.build()).queue();
	}
}

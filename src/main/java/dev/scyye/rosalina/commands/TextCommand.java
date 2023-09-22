package dev.scyye.rosalina.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class TextCommand {
	public abstract String getName();
	public abstract String getHelp();
	public abstract void execute(MessageReceivedEvent event, String[] args);

	public String[] getAliases() {
		return new String[0];
	}
}

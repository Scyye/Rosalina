package dev.scyye.rosalina.commands.impl;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class TestCommand extends dev.scyye.rosalina.commands.TextCommand{
	@Override
	public String getName() {
		return "test";
	}

	@Override
	public String getHelp() {
		return "A simple test command for testing command functionality.";
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		event.getMessage().reply("Test command executed with args: " + Arrays.toString(args)).queue();
	}


}

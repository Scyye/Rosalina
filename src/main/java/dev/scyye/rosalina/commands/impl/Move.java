package dev.scyye.rosalina.commands.impl;

import dev.scyye.rosalina.MessageUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Move extends dev.scyye.rosalina.commands.TextCommand{
	@Override
	public String getName() {
		return "move";
	}

	@Override
	public String getHelp() {
		return "Moves a certain number of messages to a different channel.\n(NOT CURRENTLY WORKING)";
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		int numMessages;
		try {
			numMessages = Integer.parseInt(args[0]);
		} catch (NumberFormatException ignored) {
			event.getMessage().reply("Invalid number: " + args[0]).queue();
			return;
		}
		numMessages = Math.min(numMessages, 40);
		String targetChannel = args[1];
		TextChannel channel;
		if (targetChannel.startsWith("<")) {
			// channel is equal to the channel mentioned
			if (!(event.getMessage().getMentions().getChannels().get(0) instanceof TextChannel channel1)) {
				event.getMessage().reply("Invalid channel: " + targetChannel).queue();
				return;
			}
			channel = channel1;
		} else {
			channel = event.getJDA().getTextChannelById(targetChannel);
		}

		if (channel == null) {
			event.getMessage().reply("Invalid channel: " + targetChannel).queue();
			return;
		}
		AtomicReference<List<Message>> atomicReference = new AtomicReference<>(new ArrayList<>());
		event.getChannel().getHistory().retrievePast(numMessages).queue(atomicReference::set);

		List<Message> messages = atomicReference.get();

		for (var m : messages) {
			MessageUtils.sendWebhookMessage(channel, m.getContentDisplay(), new MessageUtils.MessageAuthor(m.getAuthor().getEffectiveName(),
					m.getAuthor().getEffectiveAvatarUrl()), m.getAttachments().toArray(new Message.Attachment[0]));
		}

		// delete `count` messages from the original channel
		event.getChannel().purgeMessages(messages);
	}
}

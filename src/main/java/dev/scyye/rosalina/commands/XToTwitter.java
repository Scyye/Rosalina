package dev.scyye.rosalina.commands;

import dev.scyye.rosalina.MessageUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class XToTwitter extends ListenerAdapter {

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		if (!event.getMessage().getContentRaw().contains("x.com")) return;
		MessageUtils.sendWebhookMessage(event.getChannel().asTextChannel(),
				event.getMessage().getContentRaw().replace("x.com", "twitter.com"),
				new MessageUtils.MessageAuthor(event.getAuthor().getName() + " (X.com sucks ass)", event.getAuthor().getEffectiveAvatarUrl()));
		event.getMessage().delete().queue();
	}
}

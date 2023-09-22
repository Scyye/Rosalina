package dev.scyye.rosalina.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;
import dev.scyye.rosalina.MessageUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

@Interaction
public class MoveCommand {
    @SlashCommand(value = "move", ephemeral = true)
    public void onMove(CommandEvent event, @Param(name = "count", value = "The number of messages to move") int count,
                       @Param(name = "channel", value = "where to move the messages")TextChannel channel) {
        count = Math.min(count, 10);
        // Get the previous `count` messages sent in a channel
        List<Message> messages = event.getChannel().getHistory().retrievePast(count).complete();

        // Reverse the order of objects in the `messages` list
        for (int i = 0; i < messages.size() / 2; i++) {
            Message temp = messages.get(i);
            messages.set(i, messages.get(messages.size() - i - 1));
            messages.set(messages.size() - i - 1, temp);
        }

        channel.sendMessage(event.getUser().getName() + " forwarded " + count + " messages from " + event.getChannel().getAsMention()).queue();

        for (var m : messages) {
            MessageUtils.sendWebhookMessage(channel, m.getContentDisplay(), new MessageUtils.MessageAuthor(m.getAuthor().getEffectiveName(),
                    m.getAuthor().getEffectiveAvatarUrl()), m.getAttachments().toArray(new Message.Attachment[0]));
        }

        event.reply("Forwarded: " + count + " messages to " + channel.getAsMention());

    }
}

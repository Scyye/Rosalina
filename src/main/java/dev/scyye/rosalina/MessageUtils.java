package dev.scyye.rosalina;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dev.scyye.rosalina.APIGetNotNullUtils.ensureGetWebhookByName;

public class MessageUtils {
    public static Message sendPrivateMessage(User user, String content) {
        return user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(content))
                .complete();
    }

    public static Message sendPrivateMessage(User user, MessageEmbed content) {
        return user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(content))
                .complete();
    }

    public static void sendTempMessage(MessageChannel channel, String content, long delay) {
        Message message = channel.sendMessage(content).complete();
        message.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
    }

    /**
     * NO PARAMETER CAN BE NULL, THEY CAN BE EMPTY, BUT NOT NULL.
     * @param channel The channel to send the message to
     * @param message The message content (can be empty)
     * @param author The webhook's username (can be any String)
     * @param attachments A list of attachments to add to the message (can be empty as long as "message" is provided)
     */

    @ParametersAreNonnullByDefault
    public static WebhookReturn sendWebhookMessage(TextChannel channel, String message, MessageAuthor author, Message.Attachment... attachments) {
        List<Message.Attachment> attachmentList = List.of(attachments);
        Webhook webhook = ensureGetWebhookByName(channel, author.name);

        WebhookClientBuilder webhookClientBuilder = new WebhookClientBuilder(webhook.getUrl());
        JDAWebhookClient client = webhookClientBuilder.buildJDA();

        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setUsername(author.name)
                .setAvatarUrl(author.url)
                ;

        if (!message.isEmpty())
            builder.setContent(message);

        if (!attachmentList.isEmpty())
            getFilesFromAttachments(attachmentList).forEach(builder::addFile);

        var m = client.send(builder.build());
        ReadonlyMessage readonlyMessage = null;

        try {
            readonlyMessage = m.get();
        } catch (Exception e) {
            e.printStackTrace();
        }


        assert readonlyMessage != null;
        return new WebhookReturn(readonlyMessage.   toWebhookMessage(), Long.toString(readonlyMessage.getId()), client) ;
    }

    public static class MessageAuthor {
        public String name;
        public String url;


        public MessageAuthor(String name, String url) {
            this.name=name;
            this.url=url;
        }
    }

    public static List<File> getFilesFromAttachments(List<Message.Attachment> attachments) {
        List<File> files = new ArrayList<>();
        attachments.forEach(attachment -> {
            InputStream stream = null;
            try {
                stream = attachment.getProxy().download().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String fileName = attachment.getFileName();

            String[] parts = fileName.split("\\.");
            String extension = parts[parts.length-1];

            File tempFile = null;
            try {
                tempFile=File.createTempFile(parts[0], "."+extension);
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert tempFile!=null;
            tempFile.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(tempFile)){
                assert stream != null;
                IOUtils.copy(stream, fos);
            } catch (IOException ignored) {}

            files.add(tempFile);
        });

        return files;
    }

    static class WebhookReturn {
        @NotNull
        public WebhookMessage message;
        @NotNull
        public String id;
        public JDAWebhookClient client;

        @ParametersAreNonnullByDefault
        public WebhookReturn(WebhookMessage message, String id, JDAWebhookClient client) {
            this.message = message;
            this.id = id;
            this.client=client;
        }
    }
}

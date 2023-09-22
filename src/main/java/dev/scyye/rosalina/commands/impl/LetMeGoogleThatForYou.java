package dev.scyye.rosalina.commands.impl;

import dev.scyye.rosalina.commands.TextCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LetMeGoogleThatForYou extends TextCommand {

	@Override
	public String getName() {
		return "lmgtfy";
	}

	@Override
	public String getHelp() {
		return "Returns a link to a LMGTFY search.";
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		if (event.getMessage().getReferencedMessage()==null && args.length == 0) {
			event.getMessage().reply("Please provide a search query. Or reply to a message.").queue();
			return;
		}
		String query;
		Member target = null;
		if (event.getMessage().getReferencedMessage()!=null&&args.length==0) {
			query = event.getMessage().getReferencedMessage().getContentRaw();
			target = event.getMessage().getReferencedMessage().getMember();
		} else {
			query = String.join(" ", args);
		}


		String response = "please google before asking questions. https://letmegooglethat.com/?q="+query.replace(" ", "+");

		if (target == null) {
			event.getMessage().reply(response).queue();
		} else {
			event.getMessage().getReferencedMessage().reply(response).queue();
		}

		event.getMessage().delete().queue();
	}
}

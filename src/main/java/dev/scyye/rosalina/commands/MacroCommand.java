package dev.scyye.rosalina.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Optional;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;
import dev.scyye.rosalina.Main;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Interaction
public class MacroCommand {

    @SlashCommand(value = "macro run", desc = "Runs a macro")
    public void onMacroRun(CommandEvent event,
                           @Param(name = "macro", value = "The macro to execute") String macro,
                           @Optional @Param(name = "target", value = "Who's this macro for?") User user) {
        //handleMacro(new String[]{macro, user == null ? "" : user.getId()}, event.getChannel(), null);
    }

    @SlashCommand(value = "macro add", desc = "Adds a macro")
    public void onMacroAdd(CommandEvent event,
                           @Param(name = "input", value = "The input for the macro") String input,
                           @Param(name = "output", value = "The output for the macro") String output) {
        var macro = new Macro(input, output);
        event.reply(String.format("""
                    Added macro.
                    **Input**:
                    %s
                    **Output**:
                    %s
                    
                                        
                    **ID**:
                    %s
                    """, macro.input, macro.output, macro.id.toString()));
        try {
            Main.rewrite();
        } catch (IOException e) {
            event.getChannel().sendMessage("Couldn't write to file:\n```\n" + Arrays.toString(e.getStackTrace()) + "\n```").queue();
        }
    }

    @SlashCommand(value = "macro remove", desc = "Removes a macro")
    public void onMacroRemove(CommandEvent event,
                              @Param(name = "id", value = "The ID of the macro to remove") String id) {
        var macro = Macro.retrieveById(UUID.fromString(id));
        if (macro == null) {
            event.reply("No macro with id: " + id);
            return;
        }
        Main.getInstance().getConfig().macros.remove(macro);
        event.reply("Removed macro:\n" + macro.getInput());
        try {
            Main.rewrite();
        } catch (IOException e) {
            event.getChannel().sendMessage("Couldn't write to file:\n```\n" + Arrays.toString(e.getStackTrace()) + "\n```").queue();
        }
    }

    @SlashCommand(value = "macro edit input", desc = "Edit the input of a macro")
    public void onMacroEditInput(CommandEvent event,
                                 @Param(name = "id", value = "Macro ID") String id,
                                 @Param(name = "value", value = "The new value") String value) {
        var macro = Macro.retrieveById(UUID.fromString(id));
        if (macro == null) {
            event.reply("Invalid Macro");
            return;
        }

        macro.input=value;

        event.reply(String.format("""
                Successfully changed input:
                %s
                """, macro.getInput()));
    }

    @SlashCommand(value = "macro edit output", desc = "Edit the output of a macro")
    public void onMacroEditOutput(CommandEvent event,
                                  @Param(name = "id", value = "Macro ID") String id,
                                  @Param(name = "value", value = "The new value") String value) {
        var macro = Macro.retrieveById(UUID.fromString(id));
        if (macro == null) {
            event.reply("Invalid Macro");
            return;
        }

        macro.output=value;

        event.reply(String.format("""
                Successfully changed input:
                %s
                """, macro.getOutput()));

        try {
            Main.rewrite();
        } catch (IOException e) {
            event.getChannel().sendMessage("Unable to write to file.\n" + Arrays.toString(e.getStackTrace())).queue();
        }
    }

    @SlashCommand(value = "macro info", desc = "View info about a macro")
    public void onMacroInfo(CommandEvent event,
                            @Param(name = "id-or-name", value = "Macro ID or name") String id) {
        var macro = Macro.retrieveById(UUID.fromString(id));

        if (macro==null) {
            macro=Macro.getByName(id);
        }
        if (macro == null) {
            event.reply("Invalid Macro " + macro);
            return;
        }
        var b = new EmbedBuilder()
                .addField("Input", macro.input, false)
                .addField("Output", macro.output, false)
                .setFooter("MACRO ID:   " + macro.id);

        event.reply(b);
    }

    @SlashCommand(value = "macro list", desc = "Lists all macros")
    public void onMacroList(CommandEvent event) {

    }


    public static class Macro {
        @Getter
        private String input;
        @Getter
        private String output;
        @Getter
        private UUID id;

        public Macro(String input, String output) {
            this.output = output;
            this.input = input;
            id = UUID.randomUUID();
            Main.getInstance().getConfig().macros.add(this);
        }

        public static Macro retrieveById(UUID id) {
            for (Macro macro : Main.getInstance().getConfig().macros) {
                if (macro.id == null)
                    continue;
                if (macro.id.equals(id)) return macro;
            }
            return null;
        }

        public static Macro getByName(String name) {
            for (Macro m : Main.getInstance().getConfig().macros) {
                if (m.input.equalsIgnoreCase(name)) {
                    return m;
                }
            }
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Macro macro &&
                    output.equals(macro.output) &&
                    input.equals(macro.input);
        }
    }
    public static class MacroAutoCompleteListener extends ListenerAdapter{
        @Override
        public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
            System.out.println(event.getName() + " " + event.getSubcommandName());
            if (event.getName().equalsIgnoreCase("macro") && event.getSubcommandName().equalsIgnoreCase("run")) {
                List<Command.Choice> options = new ArrayList<>();

                for (Macro macro : Main.getInstance().getConfig().macros) {
                    Command.Choice choice = new Command.Choice(macro.input, macro.input);
                    if (macro.input.startsWith(event.getFocusedOption().getValue()))
                        options.add(choice);
                }
                event.replyChoices(options).queue();
            }
        }
    }

}



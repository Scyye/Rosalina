package dev.scyye.rosalina;

import com.github.kaktushose.jda.commands.JDACommands;
import com.google.gson.Gson;
import dev.scyye.rosalina.commands.*;
import dev.scyye.rosalina.commands.impl.*;
import lombok.Getter;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Main extends ListenerAdapter {

    @Getter
    private JDA jda;

    @Getter
    static Main instance;

    @Getter
    Config config = new Gson().fromJson(Files.readString(Path.of("src", "main", "resources", "config.json")), Config.class);

    private Main() throws InterruptedException, IOException {
        jda = JDABuilder.createDefault(config.TOKEN, Arrays.asList(GatewayIntent.values()))
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.playing("3D World"))
                .addEventListeners(new XToTwitter(), new MacroCommand.MacroAutoCompleteListener(), new CommandManager())
                .build().awaitReady();

        System.out.println(jda.getSelfUser().getName() + " started");
        CommandManager.add(new TestCommand());
        CommandManager.add(new LetMeGoogleThatForYou());
        // TODO: Make !move actually work.
        CommandManager.add(new Move());
        CommandManager.add(new HelpCommand());
        CommandManager.add(new Macro());
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        instance = new Main();
        JDACommands.start(instance.jda, instance.getClass(), HelpCommand.class.getPackageName());
        rewrite();
    }

    public static void rewrite() throws IOException {
        Files.writeString(Path.of("src", "main", "resources", "config.json"), new Gson().toJson(instance.config, Config.class));
    }
}


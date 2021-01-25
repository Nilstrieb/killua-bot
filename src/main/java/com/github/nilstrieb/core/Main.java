package com.github.nilstrieb.core;

import com.github.nilstrieb.cofig.Config;
import com.github.nilstrieb.cofig.Secrets;
//import com.github.nilstrieb.commands.fun.DepartureCommand;
import com.github.nilstrieb.commands.fun.DepartureCommand;
import com.github.nilstrieb.commands.fun.QuoteCommand;
import com.github.nilstrieb.commands.fun.SayCommand;
import com.github.nilstrieb.commands.fun.trivia.TriviaCommand;
import com.github.nilstrieb.commands.info.InviteCommand;
import com.github.nilstrieb.commands.info.EvalCommand;
import com.github.nilstrieb.commands.info.HelpCommand;
import com.github.nilstrieb.commands.info.ToukaCommand;
import com.github.nilstrieb.commands.util.EmoteAddCommand;
import com.github.nilstrieb.listener.ChannelMessageListener;
import com.github.nilstrieb.listener.CommandListener;
import com.github.nilstrieb.listener.ReactionEventListener;
import com.github.nilstrieb.listener.StartUpListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.Compression;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(Secrets.TOKEN);
        builder.setCompression(Compression.ZLIB);
        builder.setActivity(Activity.watching("over Gon"));

        builder.addEventListeners(new StartUpListener(), new ChannelMessageListener(), new CommandListener(), new ReactionEventListener());

        JDA jda = builder.build();
        setupCommands();
        Config.setJda(jda);

        Thread t = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            while (!line.equals("exit") && !line.equals("quit")) {
                if (line.startsWith("send")) {
                    System.out.println("GuildID");
                    line = scanner.nextLine();
                    Guild guild = jda.getGuildById(line);
                    if (guild != null) {
                        System.out.println("TextChannelID");
                        line = scanner.nextLine();
                        TextChannel textChannel = guild.getTextChannelById(line);
                        if (textChannel != null) {
                            System.out.println("Message");
                            line = scanner.nextLine();
                            if (!line.equals("")) {
                                textChannel.sendMessage(line).queue();
                            }
                        }
                    }
                }
                line = scanner.nextLine();
            }
            System.exit(0);
        });
        t.start();
    }

    private static void setupCommands() {
        new HelpCommand();
        new EvalCommand();
        new ToukaCommand();
        new SayCommand();
        new InviteCommand();
        new QuoteCommand();
        new DepartureCommand();
        new TriviaCommand();
        new EmoteAddCommand();
    }
}

package com.github.nilstrieb.cofig;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;

import java.awt.*;

public class Config {
    public static final String PREFIX = "kil ";
    public static final Color DEFAULT_COLOR = new Color(229, 201, 255);

    public static final long THIS_ID = 801015254023798825L;
    public static final long NILS_ID = 414755070161453076L;

    public static EmbedBuilder getDefaultEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Config.DEFAULT_COLOR);
        return builder;
    }

    public static EmbedBuilder getDefaultEmbed(Event event) {
        User killua = event.getJDA().getUserById(THIS_ID);
        if (killua == null) {
            event.getJDA().retrieveUserById(THIS_ID).queue(user -> {
                System.out.println("[Config] " + user.getAsTag() + " successfully retrieved.");
            });
            System.err.println("[Config] This bot user not cached. Retrieving user...");

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Config.DEFAULT_COLOR)
                    .setFooter("KilluaBot");
            return builder;
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Config.DEFAULT_COLOR).
                    setThumbnail(killua.getAvatarUrl())
                    .setFooter("KilluaBot");
            return builder;
        }
    }
}

package com.github.nilstrieb.commands.handler;

import com.github.nilstrieb.cofig.Config;
import com.github.nilstrieb.util.MultiPageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Command {
    private final String name;
    private final String description;
    private final String exampleUsage;
    private final String arguments;
    private final String detailDescription;
    private final CommandParser parser = CommandParser.getInstance();


    /**
     * New command
     * @param name command name
     * @param description quick description
     * @param exampleUsage example usage without the prefix
     * @param arguments all arguments (() -> optional. <> -> required
     * @param detailDescription a detailed description
     */
    public Command(String name, String description, String exampleUsage, String arguments, String detailDescription) {
        this.name = name;
        this.description = description;
        this.exampleUsage = exampleUsage;
        this.arguments = arguments;
        this.detailDescription = detailDescription;
        CommandHandler.addCommand(name, this, false);
    }

    /**
     * New command
     * @param name command name
     * @param description quick description
     * @param exampleUsage example usage without the prefix
     * @param arguments all arguments (() -> optional. <> -> required
     */
    public Command(String name, String description, String exampleUsage, String arguments){
        this(name, description, exampleUsage, arguments, "");
    }

    /**
     * New command
     * @param name command name
     * @param description quick description
     */
    public Command(String name, String description) {
        this(name, description, name, "", "");
    }

    /**
     * Hidden command
     * @param name name
     * @param hidden should always be true
     */
    public Command(String name, boolean hidden) {
        this.name = name;
        this.description = "";
        this.exampleUsage = "";
        this.arguments = "";
        this.detailDescription = "";
        CommandHandler.addCommand(name, this, hidden);
    }

    /**
     * The method called by the CommandHandler
     * @param event The event
     * @param args The arguments (after the command and an optional whitespace)
     */
    public abstract void called(MessageReceivedEvent event, String args);

    protected void reply(MessageReceivedEvent event, String message) {
        if (!message.equals("")) {
            event.getTextChannel().sendMessage(message).queue();
        }
    }


    protected void reply(MessageReceivedEvent event, MessageEmbed embed) {
        if (!embed.isEmpty()) {
            event.getTextChannel().sendMessage(embed).queue();
        }
    }

    protected void reply(MessageReceivedEvent event, MessageEmbed... embeds) {
        if (!embeds[0].isEmpty()) {
            event.getTextChannel().sendMessage(embeds[0]).queue(message -> new MultiPageEmbed(message, embeds));
        }
    }

    protected void reply(MessageReceivedEvent event, String emote1, String emote2, MessageEmbed... embeds) {
        if (!embeds[0].isEmpty()) {
            event.getTextChannel().sendMessage(embeds[0]).queue(message -> new MultiPageEmbed(message, emote1, emote2, embeds));
        }
    }

    protected void deleteMsg(MessageReceivedEvent event, long delay) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        event.getMessage().delete().queue();
                    }
                }, delay
        );
    }

    protected void deleteMsg(MessageReceivedEvent event) {
        event.getMessage().delete().queue();
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getExampleUsage() {
        return Config.PREFIX + exampleUsage;
    }

    public String getArguments() {
        return arguments;
    }

    public String getDetailDescription() {
        return detailDescription;
    }
}

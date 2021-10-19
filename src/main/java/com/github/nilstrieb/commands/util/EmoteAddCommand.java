package com.github.nilstrieb.commands.util;

import com.github.nilstrieb.core.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EmoteAddCommand extends Command {

    private static final long MAX_EMOTE_SIZE = 258000;
    private static final int DEFAULT_SIZE = 400;

    public EmoteAddCommand() {
        super("emote", "Add a new image as an emote, gets rescaled automatically.",
                "emote Killua (with an attached image of killua)", "<emote name>",
                "Add a new emote to your server using this command. \nIf the image is too big for discord, Killua will automatically resize it for you!");
    }

    @Override
    public void called(String args) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        Member author = event.getMember();

        if (author == null) {
            System.err.println("Author of emote add Message is null");
            reply("Error while adding emote. Pleasy try again.");
            return;
        }

        if (!author.getPermissions().contains(Permission.MANAGE_EMOTES)) {
            reply("You don't have the permissions to do that.");
        } else if (emoteLimitReached()) {
            reply("Emote limit reached");
        } else if (messageContainsEmote()) {
            try {
                stealEmote();
            } catch (Exception e) {
                reply("Error while reading image. Please try again.");
            }
        } else if (attachments.isEmpty() || !attachments.get(0).isImage()) {
            reply("No image attached");
        } else if (args.length() < 3) {
            reply("Name must be at least 3 characters: " + args);
        } else if (!args.matches("\\w+")) {
            reply("Emote names are only allowed to contain characters, numbers and underscore");
        } else {
            try {
                Message.Attachment image = attachments.get(0);
                byte[] bytes = readImage(image);
                if (bytes.length > MAX_EMOTE_SIZE) {
                    bytes = resizeImage(bytes, image.getFileExtension(), DEFAULT_SIZE);
                }
                uploadEmote(bytes, args);
            } catch (IOException e) {
                reply("Error while reading image. Please try again.");
            }
        }
    }

    private void stealEmote() throws IOException {
        Emote emote = event.getMessage().getEmotes().get(0);
        URL url = new URL(emote.getImageUrl());
        byte[] data = readUrl(url);
        uploadEmote(data, emote.getName());
    }

    private void uploadEmote(byte[] bytes, String name) {
        Icon icon = Icon.from(bytes);
        event.getGuild().createEmote(name, icon).queue(emote -> reply("Successfully added emote: " + emote.getAsMention()));
    }


    private byte[] readImage(Message.Attachment image) throws IOException {
        String urlString = image.getUrl();
        URL url = new URL(urlString);
        return readUrl(url);
    }

    private byte[] readUrl(URL url) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] chunk = new byte[4096];
        int bytesRead;

        try (InputStream stream = url.openStream()) {
            while ((bytesRead = stream.read(chunk)) > 0) {
                out.write(chunk, 0, bytesRead);
            }
        }

        return out.toByteArray();
    }

    private boolean messageContainsEmote() {
        return !event.getMessage().getEmotes().isEmpty();
    }

    private boolean emoteLimitReached() {
        int max = event.getGuild().getMaxEmotes();
        int count = (int) event.getGuild().retrieveEmotes().complete().stream().filter(e -> !e.isAnimated()).count();

        return max == count;
    }

    private byte[] resizeImage(byte[] bytes, String format, int size) throws IOException {
        reply("Image size too big (" + bytes.length / 1000 + "kB). Resizing image...");
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        double ratio = (double) image.getHeight(null) / (double) image.getWidth(null);
        // Resize the image with the correct ratio
        AffineTransform trm = new AffineTransform();
        trm.scale(size / (double) image.getHeight(), (image.getHeight() * ratio) / image.getHeight());
        final AffineTransformOp operation = new AffineTransformOp(trm, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage newImage = new BufferedImage(size, (int) (size * ratio), BufferedImage.TYPE_INT_ARGB);
        newImage = operation.filter(image, newImage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newImage, format, baos);
        byte[] newBytes = baos.toByteArray();

        if(newBytes.length > MAX_EMOTE_SIZE)
            newBytes = resizeImage(newBytes, format, size - 100);

        return newBytes;
    }
}

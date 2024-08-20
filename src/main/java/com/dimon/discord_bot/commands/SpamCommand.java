package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SpamCommand implements ICommand {

    private final ConcurrentHashMap<Long, AtomicBoolean> spamStatus = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "spam";
    }

    @Override
    public String getDescription() {
        return "Spam a user with messages.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "target", "The user you want to spam", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member admin = event.getMember();
        String adminName = admin.getNickname() != null ? admin.getNickname() : admin.getUser().getName();

        if (adminName.equals("dima2287237")) {
            Member targetMember = event.getOption("target").getAsMember();

            if (targetMember != null) {
                AtomicBoolean shouldSpam = new AtomicBoolean(true);
                spamStatus.put(admin.getIdLong(), shouldSpam);

                for (int i = 0; i < 100 && shouldSpam.get(); i++) {
                    int finalI = i;
                    targetMember.getUser().openPrivateChannel().queue(channel ->
                            channel.sendMessage("This is spam message #" + (finalI + 1)).queue()
                    );

                    try {
                        Thread.sleep(200); // Adjust the delay if needed
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                spamStatus.remove(admin.getIdLong());
                event.reply("Spam operation completed for " + targetMember.getEffectiveName()).queue();
            } else {
                event.reply("Could not find the target user.").setEphemeral(true).queue();
            }
        } else {
            event.reply("You don't have permission to use this command.").setEphemeral(true).queue();
        }
    }

    public void stopSpam(SlashCommandInteractionEvent event) {
        Member admin = event.getMember();
        String adminName = admin.getNickname() != null ? admin.getNickname() : admin.getUser().getName();

        if (adminName.equals("dima2287237")) {
            AtomicBoolean shouldSpam = spamStatus.get(admin.getIdLong());

            if (shouldSpam != null) {
                shouldSpam.set(false);
                event.reply("Spam operation stopped.").queue();
            } else {
                event.reply("No spam operation is currently running.").setEphemeral(true).queue();
            }
        } else {
            event.reply("You don't have permission to use this command.").setEphemeral(true).queue();
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        // Handle message-based commands if necessary
    }
}
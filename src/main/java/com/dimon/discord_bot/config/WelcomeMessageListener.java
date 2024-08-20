package com.dimon.discord_bot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WelcomeMessageListener extends ListenerAdapter {
    private final JDA jda;

    @Autowired
    public WelcomeMessageListener(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String welcomeMessage = "Welcome to the server, " + event.getMember().getAsMention() + "! We're glad to have you here.";

        // Send a direct message (DM) to the user
        event.getUser().openPrivateChannel().queue(channel ->
                channel.sendMessage(welcomeMessage).queue()
        );

        // Send a message in the general channel or a specific welcome channel
        event.getGuild().getTextChannelsByName("загальний", true).get(0).sendMessage(welcomeMessage).queue();
    }
}

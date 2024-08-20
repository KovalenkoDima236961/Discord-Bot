package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.model.BirthdayEntity;
import com.dimon.discord_bot.repository.BirthdayRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BirthdayAnnouncer {

    private final BirthdayRepository birthdayRepository;
    private final JDA jda;

    @Autowired
    public BirthdayAnnouncer(BirthdayRepository birthdayRepository, JDA jda) {
        this.birthdayRepository = birthdayRepository;
        this.jda = jda;
    }


    @Scheduled(cron = "0 0 9 * * ?") // Runs every day at 9:00 AM server time
    public void announceBirthdays() {
        LocalDate today = LocalDate.now();
        List<BirthdayEntity> birthdays = birthdayRepository.findByBirthday(today);

        if (!birthdays.isEmpty()) {
            TextChannel channel = jda.getTextChannelsByName("бот", true).get(0); // Adjust this to your server's channel name
            StringBuilder message = new StringBuilder("🎉 Happy Birthday to:\n");

            for (BirthdayEntity birthday : birthdays) {
                Member member = jda.getGuilds().get(0).getMemberById(birthday.getUserId()); // Adjust for multi-guild support

                if (member != null) {
                    message.append("<@").append(member.getId()).append(">\n");

                    // Send direct message (DM)
                    String privateMessage = "🎉 Happy Birthday, " + member.getEffectiveName() + "! Have a great day!";
                    member.getUser().openPrivateChannel().queue(privateChannel ->
                            privateChannel.sendMessage(privateMessage).queue()
                    );
                }
            }

            channel.sendMessage(message.toString()).queue();
        }
    }

}

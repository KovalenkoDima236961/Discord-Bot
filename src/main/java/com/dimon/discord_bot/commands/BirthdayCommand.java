package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.model.BirthdayEntity;
import com.dimon.discord_bot.repository.BirthdayRepository;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BirthdayCommand implements ICommand {

    private final BirthdayRepository birthdayRepository;

    @Autowired
    public BirthdayCommand(BirthdayRepository birthdayRepository) {
        this.birthdayRepository = birthdayRepository;
    }

    @Override
    public String getName() {
        return "setbirthday";
    }

    @Override
    public String getDescription() {
        return "Set your birthday for the bot to remember.";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "date", "Your birthday in MM-DD format", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        String dateInput = event.getOption("date").getAsString();
        System.out.println(dateInput);

        try {
            LocalDate birthday = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("MM-dd"));
            birthday = birthday.withYear(LocalDate.now().getYear()); // Set the year to the current year

            BirthdayEntity birthdayEntity = new BirthdayEntity(member.getIdLong(), birthday);
            birthdayRepository.save(birthdayEntity);

            event.reply("Your birthday has been set to " + dateInput + "!").queue();
        } catch (Exception e) {
            event.reply("Invalid date format. Please use MM-DD format.").setEphemeral(true).queue();
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }
}

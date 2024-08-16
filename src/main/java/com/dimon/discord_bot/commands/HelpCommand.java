package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class HelpCommand implements ICommand {

    private final Map<String, ICommand> commands;

    @Autowired
    public HelpCommand(List<ICommand> commandList) {
        this.commands = commandList.stream().collect(Collectors.toMap(ICommand::getName, cmd-> cmd));
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows a list of all available commands and allows you to interactively execute them.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<Button> buttons = commands.values().stream()
                .map(command -> Button.primary(command.getName(), "/" + command.getName()))
                .collect(Collectors.toList());

        List<ActionRow> actionRows = buttons.stream()
                .collect(Collectors.groupingBy(button -> buttons.indexOf(button) / 5)) // Group buttons into rows of 5
                .values().stream()
                .map(ActionRow::of)
                .collect(Collectors.toList());

        event.reply("Click a button to execute a command:")
                .addComponents(actionRows)
                .queue();
    }

    public void execute(ButtonInteractionEvent event) {
        String commandName = event.getButton().getId();

        if (commands.containsKey(commandName)) {
            ICommand command = commands.get(commandName);
            // Assuming the command can handle a ButtonInteractionEvent similarly to a SlashCommandInteractionEvent
            command.execute(event);
        } else {
            event.reply("Unknown command: " + commandName).queue();
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {
//        StringBuilder helpMessage = new StringBuilder("Here are the available commands:\n");
//
//        for (ICommand command : commands.values()) {
//            helpMessage.append("**/")
//                    .append(command.getName())
//                    .append("** - ")
//                    .append(command.getDescription())
//                    .append("\n");
//        }
//
//        event.getChannel().sendMessage(helpMessage.toString()).queue();
    }
}

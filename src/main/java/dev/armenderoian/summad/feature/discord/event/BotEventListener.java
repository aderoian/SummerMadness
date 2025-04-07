package dev.armenderoian.summad.feature.discord.event;

import dev.armenderoian.summad.SummerMadness;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotEventListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        SummerMadness.LOGGER.info("Bot '{}' has logged in.", event.getJDA().getSelfUser().getName());
    }
}

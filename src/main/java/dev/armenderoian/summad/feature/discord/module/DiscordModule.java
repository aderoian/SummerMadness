package dev.armenderoian.summad.feature.discord.module;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;

public abstract class DiscordModule extends ListenerAdapter {

    protected JDA jda;
    protected Guild guild;
    protected Logger logger;
    protected boolean hasStarted = false;

    public void start(JDA jda, Guild guild, Logger logger) throws Exception {
        this.jda = jda;
        this.guild = guild;
        this.logger = logger;
        if (!hasStarted) {
            start();
            hasStarted = true;
        }
    }

    protected abstract void start() throws Exception;

    public abstract void stop() throws Exception;
}

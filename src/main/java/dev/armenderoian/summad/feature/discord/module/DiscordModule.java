package dev.armenderoian.summad.feature.discord.module;

import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;

public abstract class DiscordModule {

    protected JDA jda;
    protected Logger logger;
    protected boolean hasStarted = false;

    public void start(JDA jda, Logger logger) throws Exception {
        this.jda = jda;
        this.logger = logger;
        if (!hasStarted) {
            start();
            hasStarted = true;
        }
    }

    protected abstract void start() throws Exception;
    public abstract void stop() throws Exception;
}

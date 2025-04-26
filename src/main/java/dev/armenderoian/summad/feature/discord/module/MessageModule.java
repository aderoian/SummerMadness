package dev.armenderoian.summad.feature.discord.module;

import com.google.gson.GsonBuilder;
import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.discord.message.JoinMessage;
import dev.armenderoian.summad.feature.discord.message.PlayersMessage;
import dev.armenderoian.summad.feature.discord.message.persistent.LandingPageMessage;
import dev.armenderoian.summad.feature.discord.message.persistent.PersistentMessage;
import dev.armenderoian.summad.feature.discord.message.persistent.RulesMessage;
import dev.armenderoian.summad.util.ServerConfig;
import dev.armenderoian.summad.util.io.database.JSONProvider;
import dev.armenderoian.summad.util.io.database.RuntimeTypeAdapterFactory;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageModule extends DiscordModule {

    private PersistentMessageDatabase database;

    public static JoinMessage JOIN_MESSAGE = new JoinMessage();
    public static PlayersMessage PLAYERS_MESSAGE = new PlayersMessage();

    @Override
    protected void start() throws Exception {
        List<PersistentMessage> messages = List.of(
                new RulesMessage(ServerConfig.rulesMessageChannelId),
                new LandingPageMessage(ServerConfig.landingMessageChannelId)
        );

        RuntimeTypeAdapterFactory<PersistentMessage> typeAdapterFactory = RuntimeTypeAdapterFactory.of(PersistentMessage.class, "type");
        messages.forEach(message -> typeAdapterFactory.registerSubtype(message.getClass()));

        database = new PersistentMessageDatabase(typeAdapterFactory);
        database.open();

        for (var message : messages) {
            if (!database.hasMessage(message.getId())) {
                database.addMessage(message);
            }
        }
        for (var message : database.getMessages().values()) {
            message.trySendMessage(guild);
        }
    }

    @Override
    public void stop() throws Exception {
        database.close();
    }

    public void editMessages() {
        for (var message : database.getMessages().values()) {
            if (message.getMessageId() != null) {
                var channel = guild.getTextChannelById(message.getChannelId());
                if (channel != null) {
                    channel.retrieveMessageById(message.getMessageId()).queue(msg -> {
                        msg.editMessage(MessageEditData.fromCreateData(message.createMessage())).queue();
                    });
                }
            }
        }
    }

    public static class PersistentMessageDatabase extends JSONProvider<PersistentMessageDatabaseModel> {

        public PersistentMessageDatabase(RuntimeTypeAdapterFactory<PersistentMessage> typeAdapterFactory) {
            super(SummerMadness.DATA_PATH.resolve("messages.json"), true);
            gson = new GsonBuilder()
                    .registerTypeAdapterFactory(typeAdapterFactory)
                    .setPrettyPrinting()
                    .create();
        }

        @Override
        public Class<PersistentMessageDatabaseModel> getDataClass() {
            return PersistentMessageDatabaseModel.class;
        }

        public PersistentMessageDatabase() {
            super(SummerMadness.DATA_PATH.resolve("messages.json"));
        }

        public boolean hasMessage(String id) {
            return data.messages.containsKey(id);
        }

        public void addMessage(PersistentMessage message) {
            data.messages.put(message.getId(), message);
        }

        public void removeMessage(PersistentMessage message) {
            data.messages.remove(message.getId());
        }

        public PersistentMessage getMessage(String id) {
            return data.messages.get(id);
        }

        public Map<String, PersistentMessage> getMessages() {
            return data.messages;
        }
    }

    public static class PersistentMessageDatabaseModel implements Serializable {
        private final Map<String, PersistentMessage> messages = new HashMap<>();
    }
}

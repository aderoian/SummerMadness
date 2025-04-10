package dev.armenderoian.summad.feature.discord.module;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.util.io.database.JSONProvider;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class ProfileLinkModule extends DiscordModule {

    private final ProfileLinkDatabase database = new ProfileLinkDatabase();

    @Override
    protected void start() throws Exception {
        database.open();
    }

    @Override
    public void stop() throws Exception {
        database.close();
    }

    private static class ProfileLinkDatabase extends JSONProvider<ProfileLinkDatabaseModel> {

        public ProfileLinkDatabase() {
            super(SummerMadness.DATA_PATH.resolve("player_links.json"));
        }

        @Override
        public Class<ProfileLinkDatabaseModel> getDataClass() {
            return ProfileLinkDatabaseModel.class;
        }

        public Map<String, String> getPlayerLinks() {
            return data.players;
        }

        public boolean isPlayerLinked(UUID playerId) {
            return data.players.containsKey(playerId.toString());
        }

        public boolean isPlayerLinked(String discordId) {
            return data.players.containsValue(discordId);
        }

        public void linkPlayer(String discordId, UUID playerId) {
            data.players.put(playerId.toString(), discordId);
        }
    }

    private static class ProfileLinkDatabaseModel implements Serializable {
        private Map<String, String> players;
    }
}

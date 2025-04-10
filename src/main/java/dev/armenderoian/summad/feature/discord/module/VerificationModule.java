package dev.armenderoian.summad.feature.discord.module;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.util.ServerConfig;
import dev.armenderoian.summad.util.io.database.JSONProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.minecraft.server.WhitelistEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.List;

public class VerificationModule extends DiscordModule {

    private final VerifiedUserDatabase database = new VerifiedUserDatabase();

    private final String landingPageChannelId = ServerConfig.landingPageChannelId;
    private TextChannel landingChannel;
    private Message landingMessage;

    private final String memberManagementChannelId = ServerConfig.memberManagementChannelId;
    private TextChannel memberManagementChannel;

    private Role[] verificationRolesToAdd;
    private Role[] verificationRolesToRemove;


    @Override
    protected void start() throws Exception {
        database.open();

        landingChannel = jda.getTextChannelById(landingPageChannelId);
        if (landingChannel == null) {
            throw new IllegalStateException("Landing page channel not found");
        }

        landingChannel.getHistory().retrievePast(1).queue(history -> {
            if (history.isEmpty()) {
                sendWelcomeMessage();
            } else {
                landingMessage = history.getFirst();
            }
        }, throwable -> {
            logger.error("Error while retrieving history", throwable);
        });

        memberManagementChannel = jda.getTextChannelById(memberManagementChannelId);
        if (memberManagementChannel == null) {
            throw new IllegalStateException("Verification channel not found");
        }

        Objects.requireNonNull(jda.getGuildById(ServerConfig.discordGuildId)).updateCommands()
                        .addCommands(Commands.slash("verify", "Verify yourself to get access to the server")
                                .setContexts(InteractionContextType.GUILD)
                                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                                .addOption(OptionType.STRING, "username", "Your Minecraft Java username.", true)).queue();

        verificationRolesToAdd = Arrays.stream(ServerConfig.verificationRolesToAdd)
                .map(roleId -> Objects.requireNonNull(guild.getRoleById(roleId)))
                .toArray(Role[]::new);

        verificationRolesToRemove = Arrays.stream(ServerConfig.verificationRolesToRemove)
                .map(roleId -> Objects.requireNonNull(guild.getRoleById(roleId)))
                .toArray(Role[]::new);

        jda.addEventListener(this);
    }

    @Override
    public void stop() throws Exception {
        database.close();
    }

    private void sendWelcomeMessage() {

    }

    private void handleVerificationProcessInit(User user, JsonObject playerData) {
        var uuid = playerData.get("id").getAsString();
        var username = playerData.get("username").getAsString();
        var avatar = playerData.get("avatar").getAsString() + ".png"; // The icon api needs .png for some reason

        var messageEmbed = createMemberManagementEmbed(user, username, uuid, avatar, false, false, true);

        try (var message = new MessageCreateBuilder()
                .addEmbeds(messageEmbed)
                .addActionRow(
                        Button.success("verify_success", "Verify"),
                        Button.danger("verify_fail", "Deny")
                )
                .build()) {
            memberManagementChannel.sendMessage(message).queue(
                    response -> database.addPlayer(uuid, username, user.getId(), response.getId()),
                    throwable -> logger.error("Failed to send message", throwable)
            );
        }
    }

    private void handleVerificationStatusChange(String messageId, boolean verified) {
        memberManagementChannel.retrieveMessageById(messageId).queue(message -> {
            var embed = message.getEmbeds().getFirst();
            if (embed == null) {
                logger.error("No embed found in message");
                return;
            }

            var fields = embed.getFields();
            var userMention = Objects.requireNonNull(fields.getFirst().getValue());
            var user = Objects.requireNonNull(guild.getMemberById(userMention.substring(2, userMention.length() - 1))).getUser();

            if (verified) {
                for (int i = 0; i < verificationRolesToAdd.length; i++) {
                    guild.addRoleToMember(user, verificationRolesToAdd[i]).queue();
                    guild.removeRoleFromMember(user, verificationRolesToRemove[i]).queue();
                }
            } else {
                for (int i = 0; i < verificationRolesToAdd.length; i++) {
                    guild.removeRoleFromMember(user, verificationRolesToAdd[i]).queue();
                    guild.addRoleToMember(user, verificationRolesToRemove[i]).queue();
                }
            }

            var username = fields.get(1).getValue();
            var uuid = fields.get(2).getValue();

            var editedEmbed = createMemberManagementEmbed(user, username, uuid, Objects.requireNonNull(embed.getImage()).getUrl(), verified, false, false);
            message.editMessage(new MessageEditBuilder()
                    .setEmbeds(editedEmbed)
                    .setActionRow(verified ? List.of(
                            Button.success("whitelist_add", "Add to Whitelist"),
                            Button.danger("verify_revoke", "Revoke Verification")) :
                            List.of(Button.success("verify_success", "Verify")))
                    .build()).queue();

            var userData = database.findByUuid(uuid);
            if (userData == null) {
                logger.error("User data not found for uuid: {}", uuid);
                return;
            }

            if (verified) {
                database.verifyPlayer(uuid);
            } else {
                database.unVerifyPlayer(uuid);
            }
            updateWhitelistStatus(uuid, username, false); // Unwhitelist the player on verification change
        }, throwable -> {
            logger.error("Failed to retrieve message", throwable);
        });
    }

    private void handleWhitelistStatusChange(String messageId, boolean whitelist) {
        memberManagementChannel.retrieveMessageById(messageId).queue(message -> {
            var embed = message.getEmbeds().getFirst();
            if (embed == null) {
                logger.error("No embed found in message");
                return;
            }

            var fields = embed.getFields();
            var userMention = Objects.requireNonNull(fields.getFirst().getValue());
            var user = Objects.requireNonNull(guild.getMemberById(userMention.substring(2, userMention.length() - 1))).getUser();

            var username = fields.get(1).getValue();
            var uuid = fields.get(2).getValue();

            var editedEmbed = createMemberManagementEmbed(user, username, uuid, Objects.requireNonNull(embed.getImage()).getUrl(), true, whitelist, false);
            message.editMessage(new MessageEditBuilder()
                    .setEmbeds(editedEmbed)
                    .setActionRow(List.of(
                            whitelist ? Button.danger("whitelist_remove", "Remove from Whitelist") : Button.success("whitelist_add", "Add to Whitelist"),
                            Button.danger("verify_revoke", "Revoke Verification")))
                    .build()).queue();

            updateWhitelistStatus(uuid, username, whitelist);
        }, throwable -> {
            logger.error("Failed to retrieve message", throwable);
        });
    }

    private void updateWhitelistStatus(String uuid, String name, boolean whitelist) {
        var userData = database.findByUuid(uuid);
        if (userData == null) {
            logger.error("User data not found for uuid: {}", uuid);
            return;
        }

        var whiteListManager = SummerMadness.SERVER.getPlayerManager().getWhitelist();
        var gameProfile = new GameProfile(UUID.fromString(uuid), name);

        if (whitelist) {
            database.whitelistPlayer(uuid);
            if (!whiteListManager.isAllowed(gameProfile))
                whiteListManager.add(new WhitelistEntry(gameProfile));
        } else {
            database.unWhitelistPlayer(uuid);
            if (whiteListManager.isAllowed(gameProfile))
                whiteListManager.remove(gameProfile);
        }
    }

    private MessageEmbed createMemberManagementEmbed(User user, String username, String uuid, String avatar, boolean verified, boolean whitelisted, boolean pending) {
        String whitelist;
        if (verified) {
            whitelist = pending ? "Pending" : whitelisted ? "Whitelisted" : "Not Whitelisted";
        } else {
            whitelist = pending ? "*Waiting for verification*" : "N/A";
        }

        return new EmbedBuilder()
                .setTitle(username, "https://mcuuid.net/?q=" + uuid)
                .setColor(pending ? Color.YELLOW : verified ? Color.GREEN : Color.RED)
                .setImage(avatar)
                .setThumbnail(user.getAvatarUrl())
                .addField("User", user.getAsMention(), true)
                .addField("Username", username, true)
                .addField("UUID", uuid, false)
                .addField("Verification Status", !pending ? verified ? "Verified" : "Denied" : "Pending...", false)
                .addField("Whitelist Status", whitelist, true)
                .build();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("verify")) return;

        if (event.getType() != InteractionType.COMMAND) return;

        var option = event.getOption("username");
        if (option == null || option.getType() != OptionType.STRING) {
            event.reply("Invalid username.").setEphemeral(true).queue();
            return;
        }

        // Check if the user is already verified
        var user = event.getUser();
        if (user.isBot()) return;
        var discordId = user.getId();
        VerifiedUserDatabaseModel.VerifiedUserData userData = database.findByDiscordId(discordId);
        if (userData != null) {
            event.reply(userData.verified ? !userData.whitelisted ? "You are already verified, but not whitelisted." : "You are already verified and whitelisted." :
                    "You have begin the verification process. Please wait for your verification to be completed.").setEphemeral(true).queue();
            return;
        }

        // Begin verification process
        try (var client = HttpClient.newHttpClient()) {
            client.sendAsync(HttpRequest.newBuilder().GET().uri(URI.create("https://playerdb.co/api/player/minecraft/" + option.getAsString())).build(), HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() != 200) {
                            event.reply("Failed to fetch Minecraft account data. Please try again.").setEphemeral(true).queue();
                        } else {
                            var body = response.body();
                            var json = SummerMadness.GSON.fromJson(body, JsonObject.class);

                            var success = json.get("success").getAsBoolean();
                            if (!success) {
                                event.reply("Failed to find Minecraft account data. Please try again.").setEphemeral(true).queue();
                                return;
                            }

                            var data = json.get("data").getAsJsonObject().get("player").getAsJsonObject();
                            handleVerificationProcessInit(user, data);
                            event.reply("You have begin the verification process. Please wait for your verification to be completed.").setEphemeral(true).queue();
                        }
                    })
                    .exceptionally(e -> {
                        event.reply("Server error. Please try again later.").setEphemeral(true).queue();
                        logger.error("Failed to link accounts", e);
                        return null;
                    });
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        var user = event.getUser();
        if (user.isBot()) return;

        String buttonId = event.getInteraction().getButton().getId();
        if (buttonId == null) {
            event.reply("Something went wrong.").setEphemeral(true).queue();
            throw new IllegalStateException("Got null button id");
        }

        switch (buttonId) {
            case "verify_success":
                handleVerificationStatusChange(event.getInteraction().getMessageId(), true);
                break;
            case "verify_fail":
            case "verify_revoke":
                handleVerificationStatusChange(event.getInteraction().getMessageId(), false);
                break;
            case "whitelist_add":
                handleWhitelistStatusChange(event.getInteraction().getMessageId(), true);
                break;
            case "whitelist_remove":
                handleWhitelistStatusChange(event.getInteraction().getMessageId(), false);
                break;
        }

        event.deferEdit().queue();
    }

    private static class VerifiedUserDatabase extends JSONProvider<VerifiedUserDatabaseModel> {

        public VerifiedUserDatabase() {
            super(SummerMadness.DATA_PATH.resolve("verified_users.json"));
        }

        @Override
        public Class<VerifiedUserDatabaseModel> getDataClass() {
            return VerifiedUserDatabaseModel.class;
        }

        public @Nullable VerifiedUserDatabaseModel.VerifiedUserData findByUuid(String uuid) {
            return data.players.get(uuid);
        }

        public @Nullable VerifiedUserDatabaseModel.VerifiedUserData findByDiscordId(String discordId) {
            var id = data.discordLinks.get(discordId);
            if (id != null) {
                return data.players.get(id);
            } else return null;
        }

        public void addPlayer(String uuid, String username, String discordId, String managementMessageId) {
            data.players.put(uuid, new VerifiedUserDatabaseModel.VerifiedUserData(uuid, username, discordId, managementMessageId));
            data.discordLinks.put(discordId, uuid);
        }

        public void removePlayer(String discordId) {
            var uuid = data.discordLinks.get(discordId);
            if (uuid != null) {
                data.discordLinks.remove(discordId);
                data.players.remove(uuid);
            }
        }

        public void verifyPlayer(String uuid) {
            var playerData = data.players.get(uuid);
            if (playerData != null) {
                playerData.verified = true;
            }
        }

        public void unVerifyPlayer(String uuid) {
            var playerData = data.players.get(uuid);
            if (playerData != null) {
                playerData.verified = false;
            }
        }

        public void whitelistPlayer(String uuid) {
            var playerData = data.players.get(uuid);
            if (playerData != null) {
                playerData.whitelisted = true;
            }
        }

        public void unWhitelistPlayer(String uuid) {
            var playerData = data.players.get(uuid);
            if (playerData != null) {
                playerData.whitelisted = false;
            }
        }
    }

    private static class VerifiedUserDatabaseModel implements Serializable {
        private Map<String, VerifiedUserData> players = new HashMap<>();
        private Map<String, String> discordLinks = new HashMap<>();

        private static class VerifiedUserData implements Serializable {
            public final String uuid;
            public final String username;
            public final String discordId;
            public final String managementMessageId;
            public boolean verified;
            public boolean whitelisted;

            public VerifiedUserData(String uuid, String username, String discordId, String managementMessageId) {
                this.uuid = uuid;
                this.username = username;
                this.discordId = discordId;
                this.managementMessageId = managementMessageId;
                this.verified = false;
                this.whitelisted = false;
            }
        }
    }
}

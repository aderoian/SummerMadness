package dev.armenderoian.summad.registry;

import dev.armenderoian.summad.SummerMadness;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundContent {

    public static final SoundEvent BUGS_SONG = registerSoundevent("bugs_song_sound");
    public static final RegistryKey<JukeboxSong> BUGS_SONG_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(SummerMadness.MOD_ID, "bugs_song"));

    public static void registerSounds() {
        SummerMadness.LOGGER.info("Registering mod sounds for '" + SummerMadness.MOD_ID + "'.");
    }

    private static SoundEvent registerSoundevent(String name) {
        Identifier id = Identifier.of(SummerMadness.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}

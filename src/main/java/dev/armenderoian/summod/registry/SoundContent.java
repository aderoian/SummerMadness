package dev.armenderoian.summod.registry;

import dev.armenderoian.summod.SummerModded;
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
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(SummerModded.MOD_ID, "bugs_song"));

    public static void registerSounds() {
        SummerModded.LOGGER.info("Registering mod sounds for '" + SummerModded.MOD_ID + "'.");
    }

    private static SoundEvent registerSoundevent(String name) {
        Identifier id = Identifier.of(SummerModded.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}

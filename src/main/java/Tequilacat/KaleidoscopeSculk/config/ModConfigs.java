package Tequilacat.KaleidoscopeSculk.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * Common config file ({@code config/kaleidoscope_sculk-common.toml}) exposing the tunable
 * gameplay rules so players and pack authors can adjust them without editing the mod.
 */
public final class ModConfigs {

    public static final ModConfigSpec SPEC;

    // Soul Sail (Reap)
    public static final ModConfigSpec.IntValue REAP_COOLDOWN_SECONDS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> REAP_BLACKLIST;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Soul Sail (Reap) settings.").push("reaping");
        REAP_COOLDOWN_SECONDS = builder
                .comment("Reaping cooldown.")
                .defineInRange("cooldownSeconds", 600, 60, 1200);
        REAP_BLACKLIST = builder
                .comment("Entity ids that can never be reaped, e.g. \"minecraft:warden\".",
                        "Boss entities and other players' pets are always excluded.")
                .defineList("blacklist", List.<String>of(), () -> "", o -> o instanceof String);
        builder.pop();

        SPEC = builder.build();
    }

    private ModConfigs() {
    }
}

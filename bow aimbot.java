package com.prcyteras.prcyterasmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ModConfig {
    public boolean aimbotEnabled = true;
    public double scanRange = 32.0;
    public boolean targetMonstersOnly = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // Forge 1.20.1 için doğru config dizini yolu
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "prcyteras_config.json");

    public static ModConfig load() {
        if (!CONFIG_FILE.exists()) {
            ModConfig defaultConfig = new ModConfig();
            save(defaultConfig);
            return defaultConfig;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            return GSON.fromJson(reader, ModConfig.class);
        } catch (Exception e) {
            return new ModConfig();
        }
    }

    public static void save(ModConfig config) {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception ignored) {}
    }
}
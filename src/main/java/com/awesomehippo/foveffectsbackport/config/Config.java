package com.awesomehippo.foveffectsbackport.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {
    private static final String CATEGORY = "client";

    public static final int ROTN_CLAMP_MAX = 5;

    private static Configuration configuration;
    private static float fovEffectScale = 1.0F;
    private static int rotnClampLevel = 0;

    public static void load(File configFile) {
        configuration = new Configuration(configFile);
        configuration.load();

        fovEffectScale = (float) configuration.get(CATEGORY, "fovEffectScale", 1.0, null, 0.0, 1.0).getDouble();
        rotnClampLevel = configuration.get(CATEGORY, "rotnClampLevel", 0, null, 0, ROTN_CLAMP_MAX).getInt();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static float getFovEffectScale() {
        return fovEffectScale;
    }

    public static int getSliderPercent() {
        return Math.round(fovEffectScale * fovEffectScale * 100.0F);
    }

    // just like vanilla 1.20.1 : percent->sqrt
    public static void setSliderPercent(int percent) {
        int clamped = Math.max(0, Math.min(100, percent));
        fovEffectScale = (float) Math.sqrt(clamped / 100.0D);
    }

    public static int getRotnClampLevel() {
        return rotnClampLevel;
    }

    public static void setRotnClampLevel(int level) {
        rotnClampLevel = Math.max(0, Math.min(ROTN_CLAMP_MAX, level));
    }

    public static void save() {
        if (configuration == null) {
            return;
        }

        configuration.get(CATEGORY, "fovEffectScale", 1.0, null, 0.0, 1.0).set(fovEffectScale);
        configuration.get(CATEGORY, "rotnClampLevel", 0, null, 0, ROTN_CLAMP_MAX).set(rotnClampLevel);
        configuration.save();
    }
}

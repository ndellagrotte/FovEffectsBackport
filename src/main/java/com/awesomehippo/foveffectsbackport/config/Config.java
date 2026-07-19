package com.awesomehippo.foveffectsbackport.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {
    private static final String CATEGORY = "client";

    public static final int ROTN_CLAMP_MAX = 5;

    // written into the .cfg file above the property; newlines become separate # lines
    private static final String ROTN_CLAMP_COMMENT =
            "FOV Clamp: 6 levels. 0 = OFF; levels 1-5 stop slowing effects from shrinking\n"
          + "the FOV further than 1-5 levels of Slowness would (movement FOV)\n"
          + "multiplier floors: 1 = 0.925 (Slowness I), 2 = 0.850 (Slowness II), etc";

    private static Configuration configuration;
    private static float fovEffectScale = 1.0F;
    private static int rotnClampLevel = 0;

    public static void load(File configFile) {
        configuration = new Configuration(configFile);
        configuration.load();

        fovEffectScale = (float) configuration.get(CATEGORY, "fovEffectScale", 1.0, null, 0.0, 1.0).getDouble();
        rotnClampLevel = configuration.get(CATEGORY, "rotnClampLevel", 0, ROTN_CLAMP_COMMENT, 0, ROTN_CLAMP_MAX).getInt();

        // probably not necessary feature: migrate the on/off 'rotnMode'
        // toggle from dev builds
        if (configuration.getCategory(CATEGORY).containsKey("rotnMode")) {
            if (configuration.getCategory(CATEGORY).remove("rotnMode").getBoolean() && rotnClampLevel == 0) {
                rotnClampLevel = 1;
            }
            save();
        } else if (configuration.hasChanged()) {
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
        configuration.get(CATEGORY, "rotnClampLevel", 0, ROTN_CLAMP_COMMENT, 0, ROTN_CLAMP_MAX).set(rotnClampLevel);
        configuration.save();
    }
}

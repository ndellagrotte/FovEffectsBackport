package com.awesomehippo.foveffectsbackport.gui;

import com.awesomehippo.foveffectsbackport.FovEffectsBackport;
import com.awesomehippo.foveffectsbackport.config.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VideoSettingsHandler {
    private static final Logger LOGGER = LogManager.getLogger(FovEffectsBackport.MOD_ID);

    private static final int SLIDER_ID = 0xF0EFEC75; // basically a random ID
    private static final int ROTN_SLIDER_ID = 0xF0EFEC76; // basically not a random ID

    private static boolean warnedNoRowList = false;

    private GuiSlider slider;
    private int lastPercent = -1;

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiVideoSettings)) {
            slider = null;
            lastPercent = -1;
            return;
        }

        GuiVideoSettings screen = (GuiVideoSettings) event.getGui();

        // now fetched reflectively instead of via AT because OptiFine
        // replaces the whole video settings screen with its own class,
        // and a hard field reference will crash with NoSuchFieldError
        // on their layouts
        Object rowList;
        try {
            rowList = ObfuscationReflectionHelper.getPrivateValue(GuiVideoSettings.class, screen, "field_146501_h", "optionsRowList");
        } catch (Exception e) {
            rowList = null;
        }

        if (!(rowList instanceof GuiOptionsRowList)) {
            if (!warnedNoRowList) {
                warnedNoRowList = true;
                LOGGER.warn("Video settings screen has no vanilla options row list (OptiFine or another mod replaced it); the FOV Effects and FOV Clamp controls will not be shown. Both settings still work from the config file.");
            }
            slider = null;
            return;
        }

        lastPercent = Config.getSliderPercent();

        slider = new GuiSlider(
                SLIDER_ID,
                screen.width / 2 - 155,
                0,
                150,
                20,
                I18n.format("options.fovEffectScale") + " ",
                "%",
                0,
                100,
                lastPercent,
                false,
                true
        );

        // notched slider: the callback runs on every press/drag
        GuiSlider clampSlider = new GuiSlider(
                ROTN_SLIDER_ID,
                screen.width / 2 + 5,
                0,
                150,
                20,
                "",
                "",
                0,
                Config.ROTN_CLAMP_MAX,
                Config.getRotnClampLevel(),
                false,
                true,
                s -> {
                    int level = s.getValueInt();
                    s.setValue(level);
                    Config.setRotnClampLevel(level);
                    s.displayString = clampSliderText(level);
                }
        );
        clampSlider.displayString = clampSliderText(Config.getRotnClampLevel());

        GuiOptionsRowList rows = (GuiOptionsRowList) rowList;
        rows.options.add(new GuiOptionsRowList.Row(slider, clampSlider));
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.getGui() instanceof GuiVideoSettings) || slider == null) {
            return;
        }

        int current = slider.getValueInt();
        if (current != lastPercent) {
            lastPercent = current;
            Config.setSliderPercent(current);
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (!(event.getGui() instanceof GuiVideoSettings)) {
            return;
        }

        GuiButton button = event.getButton();
        if (button != null && button.enabled && button.id == 200) {
            Config.save();
        }
    }

    private static String clampSliderText(int level) {
        String value = level == 0
                ? I18n.format("options.off")
                : I18n.format("options.rotnClamp.slow") + " " + I18n.format("enchantment.level." + level);
        return I18n.format("options.rotnClamp") + ": " + value;
    }
}

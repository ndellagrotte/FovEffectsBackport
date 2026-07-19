package com.awesomehippo.foveffectsbackport.gui;

import com.awesomehippo.foveffectsbackport.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VideoSettingsHandler {
    private static final int SLIDER_ID = 0xF0EFEC75; // basically a random ID
    private static final int ROTN_BUTTON_ID = 0xF0EFEC76; // basically not a random ID

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

        // row-list entries never reach actionPerformed, so the toggle happens
        // in mousePressed (the only callback GuiOptionsRowList.Row delivers)
        GuiButton rotnButton = new GuiButton(
                ROTN_BUTTON_ID,
                screen.width / 2 + 5,
                0,
                150,
                20,
                rotnButtonText()
        ) {
            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (super.mousePressed(mc, mouseX, mouseY)) {
                    Config.setRotnMode(!Config.isRotnMode());
                    displayString = rotnButtonText();
                    return true;
                }
                return false;
            }
        };

        GuiOptionsRowList rows = (GuiOptionsRowList) screen.optionsRowList;
        rows.options.add(new GuiOptionsRowList.Row(slider, rotnButton));
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

    private static String rotnButtonText() {
        return I18n.format("options.rotnMode") + ": "
                + I18n.format(Config.isRotnMode() ? "options.on" : "options.off");
    }
}

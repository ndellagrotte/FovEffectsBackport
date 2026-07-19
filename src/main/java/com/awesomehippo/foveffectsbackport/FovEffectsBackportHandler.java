package com.awesomehippo.foveffectsbackport;

import com.awesomehippo.foveffectsbackport.config.Config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FovEffectsBackportHandler {

    // each clamp level floors the movement multiplier at the value that many
    // levels of Slowness would produce: speed ratio 1 - 0.15*level, mapped
    // through (ratio + 1.0) / 2.0, so floor = 1.0 - 0.075*level
    // Slow I -> 0.925, II -> 0.850, III -> 0.775, IV -> 0.700, V -> 0.625
    private static float rotnFloor(int level) {
        return 1.0F - 0.075F * level;
    }

    // main logic
    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent event) {
        float fov = event.getFov();

        // the rotn clamp floors the raw movement multiplier first, then the
        // FOV Effects slider attenuates the (already clamped) value as normal
        int clampLevel = Config.getRotnClampLevel();
        if (clampLevel > 0) {
            fov = applyRotnFloor(event.getEntity(), fov, rotnFloor(clampLevel));
        }

        float scale = Config.getFovEffectScale();
        if (scale < 1.0F) {
            fov = 1.0F + (fov - 1.0F) * scale;
        }

        if (fov != event.getFov()) {
            event.setNewfov(fov);
        }
    }

    // important: this only floors the movement-speed-derived component of the modifier.
    // the flying (x1.1) and bow-zoom factors folded into the event's fov pass through
    // unchanged, as do all positive multipliers (speed, uhhh whatever else makes your
    // FOV higher)
    private static float applyRotnFloor(EntityPlayer player, float fov, float floor) {
        float walkSpeed = player.capabilities.getWalkSpeed();
        if (walkSpeed == 0.0F) {
            // vanilla forces the whole modifier to 1.0 in this case
            return fov;
        }

        IAttributeInstance speed = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        float movement = (float) ((speed.getAttributeValue() / walkSpeed + 1.0D) / 2.0D);
        if (Float.isNaN(movement) || Float.isInfinite(movement) || movement <= 0.0F || movement >= floor) {
            return fov;
        }

        // divide the raw movement component out of the composed modifier and
        // re-apply the floored one
        return fov / movement * floor;
    }
}

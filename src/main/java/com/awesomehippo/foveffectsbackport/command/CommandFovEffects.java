package com.awesomehippo.foveffectsbackport.command;

import com.awesomehippo.foveffectsbackport.config.Config;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandFovEffects extends CommandBase {

    @Override
    public String getName() {
        return "foveffects";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.foveffects.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            Config.setSliderPercent(parseInt(args[0], 0, 100));
            Config.save();
        }

        sender.sendMessage(new TextComponentString(
                I18n.format("options.fovEffectScale") + ": " + Config.getSliderPercent() + "%"));
    }
}

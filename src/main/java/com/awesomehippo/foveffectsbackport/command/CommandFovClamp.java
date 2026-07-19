package com.awesomehippo.foveffectsbackport.command;

import java.util.List;

import com.awesomehippo.foveffectsbackport.config.Config;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandFovClamp extends CommandBase {

    @Override
    public String getName() {
        return "fovclamp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.fovclamp.usage";
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
            Config.setRotnClampLevel(parseLevel(args[0]));
            Config.save();
        }

        sender.sendMessage(new TextComponentString(clampLabel(Config.getRotnClampLevel())));
    }

    // accepts "off" or 0-5
    private static int parseLevel(String arg) throws CommandException {
        if ("off".equalsIgnoreCase(arg)) {
            return 0;
        }

        return parseInt(arg, 0, Config.ROTN_CLAMP_MAX);
    }

    // matches the video settings slider label
    private static String clampLabel(int level) {
        String value = level == 0
                ? I18n.format("options.off")
                : I18n.format("options.rotnClamp.slow") + " " + I18n.format("enchantment.level." + level);
        return I18n.format("options.rotnClamp") + ": " + value;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "off", "1", "2", "3", "4", "5");
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}

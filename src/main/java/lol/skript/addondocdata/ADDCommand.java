package lol.skript.addondocdata;

import lol.skript.addondocdata.syntax.DocSyntaxInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ADDCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String argPluginName = String.join(" ", args);
        Plugin addonPlugin = Bukkit.getPluginManager().getPlugin(argPluginName);
        if (addonPlugin == null) {
            sender.sendMessage(TextUtil.mm("<red>That plugin does not exist!"));
            return true;
        }

        AddonDataLoader loader = new AddonDataLoader(argPluginName, addonPlugin);
        for (DocSyntaxInfo info : loader.getSyntaxes()) {
            sender.sendMessage(info.toString());
        }

        return true;
    }
}

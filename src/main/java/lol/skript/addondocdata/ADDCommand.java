package lol.skript.addondocdata;

import lol.skript.addondocdata.syntax.DocSyntaxInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        if (loader.getSyntaxes().isEmpty()) return true;

        DocSyntaxInfo first = loader.getSyntaxes().get(0);
        try {
            String fileName = argPluginName + "-" + addonPlugin.getPluginMeta().getVersion() + ".json";
            File dataFolder = ADDMain.getInstance().getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            File outFile = new File(dataFolder, fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            String output = JsonExporter.convert(loader.getSyntaxes());
            writer.write(output);
            writer.close();

            sender.sendMessage(TextUtil.mm("<green>Successfully exported syntaxes to " + fileName));
        } catch (IOException e) {
            sender.sendMessage(TextUtil.mm("<dark_red>There was an error exporting the syntax file:\n<red>" + e.getMessage() + "\n<dark_red>See console for more details."));
            e.printStackTrace();
        }

        return true;
    }
}

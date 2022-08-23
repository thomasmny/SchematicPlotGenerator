package com.eintosti.schematicplotgenerator;

import com.eintosti.schematicplotgenerator.generator.PlotGenerator;
import com.eintosti.schematicplotgenerator.schematic.Schematic;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * @author einTosti
 */
public class SchematicPlotGenerator extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Plugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Plugin disabled");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        Schematic schematic = new Schematic(getSchematicFile());
        return new PlotGenerator(schematic);
    }

    public File getSchematicFile() {
        File dataFolder = this.getDataFolder();
        if (dataFolder.mkdirs()) {
            getLogger().log(Level.INFO, "Created directory: " + dataFolder);
        }

        File schematicDirectory = new File(dataFolder + File.separator + "schematic");
        if (schematicDirectory.mkdirs()) {
            getLogger().log(Level.INFO, "Created directory: " + schematicDirectory);
        }

        File[] listOfFiles = schematicDirectory.listFiles();
        if (listOfFiles == null) {
            return null;
        }

        return Arrays.stream(listOfFiles)
                .filter(File::isFile)
                .findFirst()
                .orElse(null);
    }
}

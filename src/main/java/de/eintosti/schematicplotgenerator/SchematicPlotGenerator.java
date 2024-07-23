package de.eintosti.schematicplotgenerator;

import de.eintosti.schematicplotgenerator.generator.PlotGenerator;
import de.eintosti.schematicplotgenerator.schematic.Schematic;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

public final class SchematicPlotGenerator extends JavaPlugin {

    public static final int PLOT_HEIGHT = 64;
    public static final int SPACE_BETWEEN_PLOTS = 30;

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
        File schematicFile = getSchematicFile();
        if (schematicFile == null) {
            throw new RuntimeException("Unable to find schematic for chunk generation. "
                    + "Please place a .schem file in the /schematics/ folder");
        }

        return new PlotGenerator(Schematic.of(schematicFile), PLOT_HEIGHT, SPACE_BETWEEN_PLOTS);
    }

    /**
     * Gets the first {@code .schem} file in the {@code /schematics/} folder.
     *
     * @return The schematic file, if any found, otherwise {@code null}
     */
    private File getSchematicFile() {
        File dataFolder = this.getDataFolder();
        if (dataFolder.mkdirs()) {
            getLogger().log(Level.INFO, "Created directory: %s", dataFolder);
        }

        File schematicDirectory = new File(dataFolder + File.separator + "schematic");
        if (schematicDirectory.mkdirs()) {
            getLogger().log(Level.INFO, "Created directory: %s", schematicDirectory);
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

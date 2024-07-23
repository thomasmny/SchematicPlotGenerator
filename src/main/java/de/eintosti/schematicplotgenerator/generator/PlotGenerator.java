package de.eintosti.schematicplotgenerator.generator;

import de.eintosti.schematicplotgenerator.schematic.Schematic;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlotGenerator extends ChunkGenerator {

    private final Schematic schematic;
    private final int plotHeight;
    private final int plotWidth;

    /**
     * Creates a new {@link PlotGenerator} instance.
     *
     * @param schematic         The schematic to paste in the world
     * @param plotHeight        The height at which the bottom of the schematic is placed
     * @param spaceBetweenPlots The amount of blocks between the longest side of two plots
     */
    public PlotGenerator(Schematic schematic, int plotHeight, int spaceBetweenPlots) {
        this.schematic = schematic;
        this.plotHeight = plotHeight;
        this.plotWidth = schematic.getLongestSide() + spaceBetweenPlots;
    }

    @NotNull
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        world.setSpawnFlags(false, false);

        world.setSpawnLimit(SpawnCategory.ANIMAL, 0);
        world.setSpawnLimit(SpawnCategory.AMBIENT, 0);
        world.setSpawnLimit(SpawnCategory.AXOLOTL, 0);
        world.setSpawnLimit(SpawnCategory.MONSTER, 0);
        world.setSpawnLimit(SpawnCategory.WATER_ANIMAL, 0);
        world.setSpawnLimit(SpawnCategory.WATER_UNDERGROUND_CREATURE, 0);

        return new ArrayList<>();
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int cX, int cZ, @NotNull ChunkGenerator.ChunkData chunkData) {
        int baseX = (cX << 4) % plotWidth + (cX < 0 ? plotWidth : 0);
        int baseZ = (cZ << 4) % plotWidth + (cZ < 0 ? plotWidth : 0);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < schematic.getHeight(); y++) {
                    int pasteX = (x + baseX) % plotWidth;
                    int pasteZ = (z + baseZ) % plotWidth;
                    BlockData blockData = schematic.getBlockData(pasteX, y, pasteZ);
                    if (blockData != null) {
                        chunkData.setBlock(x, y + plotHeight, z, blockData);
                    }
                }
            }
        }
    }

    @Nullable
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new BiomeProvider() {
            private static final Biome DEFAULT_BIOME = Biome.PLAINS;

            @NotNull
            @Override
            public Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                return DEFAULT_BIOME;
            }

            @NotNull
            @Override
            public List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                return Collections.singletonList(DEFAULT_BIOME);
            }
        };
    }
}

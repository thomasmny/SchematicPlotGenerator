package com.eintosti.schematicplotgenerator.generator;

import com.eintosti.schematicplotgenerator.schematic.Schematic;
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

/**
 * @author einTosti
 */
public class PlotGenerator extends ChunkGenerator {

    public static final int PLOT_HEIGHT = 64;
    public static final int SPACE_BETWEEN_PLOTS = 30;

    private final Schematic schematic;
    private final int plotWidth;

    public PlotGenerator(Schematic schematic) {
        this.schematic = schematic;
        this.plotWidth = schematic.getLongestSide() + SPACE_BETWEEN_PLOTS;
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
                for (int y = 0; y < (PLOT_HEIGHT + schematic.getHeight()); y++) {
                    int pasteX = (x + baseX) % plotWidth;
                    int pasteZ = (z + baseZ) % plotWidth;
                    BlockData blockData = schematic.getBlockData(pasteX, y, pasteZ);
                    if (blockData != null) {
                        chunkData.setBlock(x, y + PLOT_HEIGHT, z, blockData);
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

package com.eintosti.schematicplotgenerator.schematic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author einTosti
 */
public class Schematic {

    private final Map<BlockVector, BlockData> blockWrapper = new HashMap<>();

    private int height;
    private int longestSide;

    public Schematic(File file) {
        Map<Integer, BlockData> blocksMap = new HashMap<>();

        try {
            CompoundTag nbtData = NbtIo.readCompressed(file);

            short width = nbtData.getShort("Width");
            short length = nbtData.getShort("Length");
            this.height = nbtData.getShort("Height");
            this.longestSide = Math.max(width, length);

            byte[] dataByteArray = nbtData.getByteArray("BlockData");
            CompoundTag palette = nbtData.getCompound("Palette");

            palette.getAllKeys().forEach(rawState -> {
                int id = palette.getInt(rawState);
                BlockData data = Bukkit.createBlockData(rawState);
                blocksMap.put(id, data);
            });

            int index = 0;
            int i = 0;
            int value;
            int varIntLength;
            while (i < dataByteArray.length) {
                value = 0;
                varIntLength = 0;

                while (true) {
                    value |= (dataByteArray[i] & 127) << (varIntLength++ * 7);
                    if (varIntLength > 5) {
                        throw new IOException("VarInt too big (probably corrupted data)");
                    }
                    if ((dataByteArray[i] & 128) != 128) {
                        i++;
                        break;
                    }
                    i++;
                }

                short y = (short) (index / (width * length));
                short z = (short) ((index % (width * length)) / width);
                short x = (short) ((index % (width * length)) % width);

                BlockData blockData = blocksMap.get(value);
                if (blockData == null) {
                    continue;
                }

                this.blockWrapper.put(new BlockVector(x, y, z), blockData);

                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getHeight() {
        return height;
    }

    public int getLongestSide() {
        return longestSide;
    }

    @Nullable
    public BlockData getBlockData(int x, int y, int z) {
        return blockWrapper.get(new BlockVector(x, y, z));
    }
}
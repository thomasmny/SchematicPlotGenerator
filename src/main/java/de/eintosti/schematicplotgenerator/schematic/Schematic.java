package de.eintosti.schematicplotgenerator.schematic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Schematic {

    private final int height;
    private final int longestSide;
    private final Map<Vec3, BlockData> blockStorage;

    private Schematic(int height, int longestSide, Map<Vec3, BlockData> blockStorage) {
        this.height = height;
        this.longestSide = longestSide;
        this.blockStorage = blockStorage;
    }

    /**
     * Creates a new {@link Schematic} using the given file.
     *
     * @param file The schematic file ({@code .schem}) to read from
     * @return The schematic object
     */
    public static Schematic of(File file) {
        CompoundTag nbtData;
        try {
            nbtData = NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read schematic file", e);
        }

        short width = nbtData.getShort("Width");
        short length = nbtData.getShort("Length");
        short height = nbtData.getShort("Height");
        int longestSide = Math.max(width, length);

        byte[] dataByteArray = nbtData.getByteArray("BlockData");
        CompoundTag palette = nbtData.getCompound("Palette");

        Map<Integer, BlockData> blocksMap = new HashMap<>();
        palette.getAllKeys().forEach(rawState -> {
            int id = palette.getInt(rawState);
            BlockData data = Bukkit.createBlockData(rawState);
            blocksMap.put(id, data);
        });

        Map<Vec3, BlockData> blockStorage = new HashMap<>();

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
                    throw new RuntimeException(String.format(
                            "VarInt too big (probably corrupted data). Found length %s, should be max. 5",
                            varIntLength
                    ));
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

            blockStorage.put(new Vec3(x, y, z), blockData);
            index++;
        }

        return new Schematic(height, longestSide, blockStorage);
    }

    public int getHeight() {
        return height;
    }

    public int getLongestSide() {
        return longestSide;
    }

    @Nullable
    public BlockData getBlockData(int x, int y, int z) {
        return blockStorage.get(new Vec3(x, y, z));
    }
}
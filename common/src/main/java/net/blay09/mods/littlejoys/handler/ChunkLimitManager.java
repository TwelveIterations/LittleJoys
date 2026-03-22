package net.blay09.mods.littlejoys.handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.LittleJoys;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ChunkLimitManager extends SavedData {

    private static final Codec<ChunkLimitManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.strictUnboundedMap(Codec.STRING.xmap(Long::parseLong, String::valueOf), Codec.INT).fieldOf("FishingSpotChunks").forGetter(ChunkLimitManager::getFishingSpotCounts),
            ExtraCodecs.strictUnboundedMap(Codec.STRING.xmap(Long::parseLong, String::valueOf), Codec.INT).fieldOf("DigSpotChunks").forGetter(ChunkLimitManager::getDigSpotCounts)
    ).apply(instance, ChunkLimitManager::new));
    private static final SavedDataType<ChunkLimitManager> TYPE = new SavedDataType<>(
            id("chunk_limits"),
            () -> new ChunkLimitManager(Map.of(), Map.of()),
            CODEC,
            null // TODO this can't be null but mod loaders will save us soon I'm sure
    );
    private final Map<Long, Integer> fishingSpotCounts = new HashMap<>();
    private final Map<Long, Integer> digSpotCounts = new HashMap<>();

    public ChunkLimitManager(Map<Long, Integer> fishingSpotCounts, Map<Long, Integer> digSpotCounts) {
        this.fishingSpotCounts.putAll(fishingSpotCounts);
        this.digSpotCounts.putAll(digSpotCounts);
    }

    public static ChunkLimitManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    private Map<Long, Integer> getFishingSpotCounts() {
        return fishingSpotCounts;
    }

    private Map<Long, Integer> getDigSpotCounts() {
        return digSpotCounts;
    }

    public int getTotalFishingSpotsInChunk(BlockPos pos) {
        final var chunkPos = ChunkPos.containing(pos);
        final var chunkKey = chunkPos.pack();
        return fishingSpotCounts.getOrDefault(chunkKey, 0);
    }

    public int getTotalDigSpotsInChunk(BlockPos pos) {
        final var chunkPos = ChunkPos.containing(pos);
        final var chunkKey = chunkPos.pack();
        return digSpotCounts.getOrDefault(chunkKey, 0);
    }

    public void trackFishingSpot(BlockPos pos) {
        final var chunkPos = ChunkPos.containing(pos);
        final var chunkKey = chunkPos.pack();
        final var currentCount = fishingSpotCounts.getOrDefault(chunkKey, 0);
        fishingSpotCounts.put(chunkKey, currentCount + 1);
        setDirty();
    }

    public void trackDigSpot(BlockPos pos) {
        final var chunkPos = ChunkPos.containing(pos);
        final var chunkKey = chunkPos.pack();
        final var currentCount = digSpotCounts.getOrDefault(chunkKey, 0);
        digSpotCounts.put(chunkKey, currentCount + 1);
        setDirty();
    }

}
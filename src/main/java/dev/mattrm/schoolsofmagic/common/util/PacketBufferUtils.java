package dev.mattrm.schoolsofmagic.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.network.PacketBuffer;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketBufferUtils {
    public static <K, V> void writeMap(PacketBuffer buffer, Map<K, V> map, BiConsumer<K, PacketBuffer> keyTransformer, BiConsumer<V, PacketBuffer> valueTransformer) {
        buffer.writeVarInt(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keyTransformer.accept(entry.getKey(), buffer);
            valueTransformer.accept(entry.getValue(), buffer);
        }
    }

    public static <K, V> Map<K, V> readMap(PacketBuffer buffer, Function<PacketBuffer, K> keyTransformer, Function<PacketBuffer, V> valueTransformer) {
        int size = buffer.readVarInt();
        Map<K, V> map = Maps.newHashMap();

        for (int i = 0; i < size; i++) {
            map.put(keyTransformer.apply(buffer), valueTransformer.apply(buffer));
        }

        return map;
    }

    public static <T> void writeList(PacketBuffer buffer, List<T> list, BiConsumer<T, PacketBuffer> elementTransformer) {
        buffer.writeVarInt(list.size());
        for (T element : list) {
            elementTransformer.accept(element, buffer);
        }
    }

    public static <T> List<T> readList(PacketBuffer buffer, Function<PacketBuffer, T> elementTransformer) {
        int size = buffer.readVarInt();
        List<T> list = Lists.newArrayList();

        for (int i = 0; i < size; i++) {
            list.add(elementTransformer.apply(buffer));
        }

        return list;
    }
}

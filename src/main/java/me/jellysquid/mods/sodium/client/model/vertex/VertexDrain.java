package me.jellysquid.mods.sodium.client.model.vertex;

import net.minecraft.class_4588;

/**
 * A drain allows the instantiation of {@link VertexSink} and is implemented on outputs which take vertex data.
 */
public interface VertexDrain {
    /**
     * Returns a {@link VertexDrain} implementation on the provided {@link class_4588}. Since the interface
     * is always implemented on a given class_4588, this is simply implemented as a cast internally.
     * @param consumer The {@link class_4588}
     * @return A {@link VertexDrain}
     */
    static VertexDrain of(class_4588 consumer) {
        return (VertexDrain) consumer;
    }

    /**
     * Returns a {@link VertexSink} of type {@link T}, created from {@param factory}, which transforms and writes
     * vertices through this vertex drain.
     *
     * @param factory The factory to create a vertex sink using
     * @param <T> The vertex sink's type
     * @return A new {@link VertexSink} of type {@link T}
     */
    <T extends VertexSink> T createSink(VertexType<T> factory);
}

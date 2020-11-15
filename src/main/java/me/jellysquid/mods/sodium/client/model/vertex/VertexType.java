package me.jellysquid.mods.sodium.client.model.vertex;

import net.minecraft.class_4588;

/**
 * Provides factories which create a {@link VertexSink} for the given vertex format.
 *
 * @param <T> The {@link VertexSink} type this factory produces
 */
public interface VertexType<T extends VertexSink> {
    /**
     * Creates a {@link VertexSink} which can write into any {@link class_4588}. This is generally used when
     * a special implementation of {@link class_4588} is used that cannot be optimized for, or when
     * complex/unsupported transformations need to be performed using vanilla code paths.
     * @param consumer The {@link class_4588} to write into
     */
    T createFallbackWriter(class_4588 consumer);

    /**
     * If this vertex type supports {@link VertexTypeBlittable}, then this method returns this vertex type as a
     * blittable type, performing a safe cast.
     */
    default VertexTypeBlittable<T> asBlittable() {
        return null;
    }
}

package me.jellysquid.mods.sodium.client.model.vertex.fallback;

import me.jellysquid.mods.sodium.client.model.vertex.VertexSink;
import net.minecraft.class_4588;

/**
 * The base implementation for a {@link VertexSink} which writes to a black-boxed {@link class_4588}. This is the
 * fallback path used when direct-writing optimizations cannot be used because the drain has no accessible backing
 * memory. This implementation is very slow and should be avoided where possible.
 *
 * This sink does not support explicit batching/flushing and as such, all written vertices are immediately flushed
 * to the backing implementation.
 */
public abstract class VertexWriterFallback implements VertexSink {
    protected final class_4588 consumer;

    protected VertexWriterFallback(class_4588 consumer) {
        this.consumer = consumer;
    }

    @Override
    public void ensureCapacity(int count) {
        // NO-OP
    }

    @Override
    public void flush() {
        // NO-OP
    }
}

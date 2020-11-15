package me.jellysquid.mods.sodium.client.model.vertex.formats.quad;

import me.jellysquid.mods.sodium.client.model.vertex.VertexType;
import me.jellysquid.mods.sodium.client.model.vertex.VertexTypeBlittable;
import me.jellysquid.mods.sodium.client.model.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.writer.QuadVertexBufferWriterNio;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.writer.QuadVertexBufferWriterUnsafe;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.writer.QuadVertexWriterFallback;
import net.minecraft.class_4588;
import net.minecraft.client.render.VertexFormat;

public class QuadVertexType implements VertexType<QuadVertexSink>, VertexTypeBlittable<QuadVertexSink> {
    @Override
    public QuadVertexSink createFallbackWriter(class_4588 consumer) {
        return new QuadVertexWriterFallback(consumer);
    }

    @Override
    public QuadVertexSink createBufferWriter(VertexBufferView buffer, boolean direct) {
        return direct ? new QuadVertexBufferWriterUnsafe(buffer) : new QuadVertexBufferWriterNio(buffer);
    }

    @Override
    public VertexFormat getBufferVertexFormat() {
        return QuadVertexSink.VERTEX_FORMAT;
    }

    @Override
    public VertexTypeBlittable<QuadVertexSink> asBlittable() {
        return this;
    }
}

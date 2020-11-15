package me.jellysquid.mods.sodium.client.render.chunk.passes;

import net.minecraft.client.render.RenderLayer;

// TODO: Move away from using an enum, make this extensible
public enum BlockRenderPass {
    SOLID(RenderLayer.SOLID, false),
    CUTOUT(RenderLayer.CUTOUT, false),
    CUTOUT_MIPPED(RenderLayer.CUTOUT_MIPPED, false),
    TRANSLUCENT(RenderLayer.TRANSLUCENT, true);

    public static final BlockRenderPass[] VALUES = BlockRenderPass.values();
    public static final int COUNT = VALUES.length;

    private final RenderLayer layer;
    private final boolean translucent;

    BlockRenderPass(RenderLayer layer, boolean translucent) {
        this.layer = layer;
        this.translucent = translucent;
    }

    public boolean isTranslucent() {
        return this.translucent;
    }

    public void endDrawing() {
        this.layer.end();
    }

    public void startDrawing() {
        this.layer.begin();
    }
}

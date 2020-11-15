package me.jellysquid.mods.sodium.client.render.chunk.passes;

import net.minecraft.block.BlockRenderLayer;

// TODO: Move away from using an enum, make this extensible
public enum BlockRenderPass {
    SOLID(BlockRenderLayer.SOLID, false),
    CUTOUT(BlockRenderLayer.CUTOUT, false),
    CUTOUT_MIPPED(BlockRenderLayer.CUTOUT_MIPPED, false),
    TRANSLUCENT(BlockRenderLayer.TRANSLUCENT, true);

    public static final BlockRenderPass[] VALUES = BlockRenderPass.values();
    public static final int COUNT = VALUES.length;

    private final BlockRenderLayer layer;
    private final boolean translucent;

    BlockRenderPass(BlockRenderLayer layer, boolean translucent) {
        this.layer = layer;
        this.translucent = translucent;
    }

    public boolean isTranslucent() {
        return this.translucent;
    }

    public void endDrawing() {
        this.layer.method_22724();
    }

    public void startDrawing() {
        this.layer.method_22723();
    }
}

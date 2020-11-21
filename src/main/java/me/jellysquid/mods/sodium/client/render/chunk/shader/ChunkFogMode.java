package me.jellysquid.mods.sodium.client.render.chunk.shader;

import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderConstants;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.function.Function;

public enum ChunkFogMode {
    NONE(ChunkShaderFogComponent.None::new, ImmutableList.of()),
    LINEAR(ChunkShaderFogComponent.Linear::new, ImmutableList.of("USE_FOG", "USE_FOG_LINEAR")),
    EXP2(ChunkShaderFogComponent.Exp2::new, ImmutableList.of("USE_FOG", "USE_FOG_EXP2")),
    LINEAR_ZBUFFER(ChunkShaderFogComponent.Linear::new, ImmutableList.of("USE_FOG", "USE_FOG_LINEAR", "USE_FOG_ZBUFFER")),
    EXP2_ZBUFFER(ChunkShaderFogComponent.Exp2::new, ImmutableList.of("USE_FOG", "USE_FOG_EXP2", "USE_FOG_ZBUFFER"));

    private final Function<ChunkProgram, ChunkShaderFogComponent> factory;
    private final List<String> defines;

    ChunkFogMode(Function<ChunkProgram, ChunkShaderFogComponent> factory, List<String> defines) {
        this.factory = factory;
        this.defines = defines;
    }

    public Function<ChunkProgram, ChunkShaderFogComponent> getFactory() {
        return this.factory;
    }

    public List<String> getDefines() {
        return this.defines;
    }

    /**
     * Retrieves the current fog mode from the fixed-function pipeline.
     */
    public static ChunkFogMode getActiveMode() {
        if (!GL11.glGetBoolean(GL11.GL_FOG)) {
            return ChunkFogMode.NONE;
        }

        int mode = GL11.glGetInteger(GL11.GL_FOG_MODE);

        boolean useZBufferFog = SodiumClientMod.options().advanced.useZBufferFog;

        switch (mode) {
            case GL11.GL_EXP2:
            case GL11.GL_EXP:
                if (useZBufferFog) {
                    return ChunkFogMode.EXP2_ZBUFFER;
                } else {
                    return ChunkFogMode.EXP2;
                }
            case GL11.GL_LINEAR:
                if (useZBufferFog) {
                    return ChunkFogMode.LINEAR_ZBUFFER;
                } else {
                    return ChunkFogMode.LINEAR;
                }
            default:
                throw new UnsupportedOperationException("Unknown fog mode: " + mode);
        }
    }

    public void addConstants(ShaderConstants.Builder constants) {
        for (String define : this.defines) {
            constants.define(define);
        }
    }
}

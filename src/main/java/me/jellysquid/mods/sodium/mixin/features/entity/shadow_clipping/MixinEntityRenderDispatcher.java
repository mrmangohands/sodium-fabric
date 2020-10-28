package me.jellysquid.mods.sodium.mixin.features.entity.shadow_clipping;

//Fixme:
//@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
    /*@Redirect(method = "shadowVertex", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;vertex(Lnet/minecraft/client/util/math/Matrix4f;FFF)Lnet/minecraft/client/render/VertexConsumer;"))
    private static VertexConsumer preWriteVertex(VertexConsumer vertexConsumer, Matrix4f matrix, float x, float y, float z) {
        // FIX: Render the shadow slightly above the block to fix clipping issues
        // This happens in vanilla too, but is exacerbated by the Compact Vertex Format option.
        return vertexConsumer.vertex(matrix, x, y + 0.001f, z);
    }*/

}

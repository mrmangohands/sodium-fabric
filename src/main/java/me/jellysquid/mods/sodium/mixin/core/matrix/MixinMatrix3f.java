package me.jellysquid.mods.sodium.mixin.core.matrix;

import me.jellysquid.mods.sodium.client.util.Norm3b;
import me.jellysquid.mods.sodium.client.util.math.Matrix3fExtended;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix3f.class)
public class MixinMatrix3f implements Matrix3fExtended {
    @Final
    @Shadow
    private float[] components;

    @Override
    public float transformVecX(float x, float y, float z) {
        return this.components[0] * x + this.components[3] * y + this.components[6] * z;
    }

    @Override
    public float transformVecY(float x, float y, float z) {
        return this.components[1] * x + this.components[4] * y + this.components[7] * z;
    }

    @Override
    public float transformVecZ(float x, float y, float z) {
        return this.components[2] * x + this.components[5] * y + this.components[8] * z;
    }

    @Override
    public void rotate(Quaternion quaternion) {
        boolean x = quaternion.getB() != 0.0F;
        boolean y = quaternion.getC() != 0.0F;
        boolean z = quaternion.getD() != 0.0F;

        // Try to determine if this is a simple rotation on one axis component only
        if (x) {
            if (!y && !z) {
                this.rotateX(quaternion);
            } else {
                this.rotateXYZ(quaternion);
            }
        } else if (y) {
            if (!z) {
                this.rotateY(quaternion);
            } else {
                this.rotateXYZ(quaternion);
            }
        } else if (z) {
            this.rotateZ(quaternion);
        }
    }

    @Override
    public int computeNormal(Direction dir) {
        Vec3i faceNorm = dir.getVector();

        float x = faceNorm.getX();
        float y = faceNorm.getY();
        float z = faceNorm.getZ();

        float x2 = this.components[0] * x + this.components[3] * y + this.components[6] * z;
        float y2 = this.components[1] * x + this.components[4] * y + this.components[7] * z;
        float z2 = this.components[2] * x + this.components[5] * y + this.components[8] * z;

        return Norm3b.pack(x2, y2, z2);
    }

    private void rotateX(Quaternion quaternion) {
        float x = quaternion.getB();
        float w = quaternion.getA();

        float xx = 2.0F * x * x;

        float tcomponents4 = 1.0F - xx;
        float tcomponents8 = 1.0F - xx;

        float xw = x * w;
        float tcomponents5 = 2.0F * xw;
        float tcomponents7 = 2.0F * -xw;

        float components3 = this.components[3] * tcomponents4 + this.components[6] * tcomponents5;
        float components6 = this.components[3] * tcomponents7 + this.components[6] * tcomponents8;
        float components4 = this.components[4] * tcomponents4 + this.components[7] * tcomponents5;
        float components7 = this.components[4] * tcomponents7 + this.components[7] * tcomponents8;
        float components5 = this.components[5] * tcomponents4 + this.components[8] * tcomponents5;
        float components8 = this.components[5] * tcomponents7 + this.components[8] * tcomponents8;

        this.components[3] = components3;
        this.components[6] = components6;
        this.components[4] = components4;
        this.components[7] = components7;
        this.components[5] = components5;
        this.components[8] = components8;
    }

    private void rotateY(Quaternion quaternion) {
        float y = quaternion.getC();
        float w = quaternion.getA();

        float yy = 2.0F * y * y;

        float tcomponents0 = 1.0F - yy;
        float tcomponents8 = 1.0F - yy;

        float yw = y * w;

        float tcomponents2 = 2.0F * (-yw);
        float tcomponents6 = 2.0F * (+yw);

        float components0 = this.components[0] * tcomponents0 + this.components[6] * tcomponents2;
        float components6 = this.components[0] * tcomponents6 + this.components[6] * tcomponents8;
        float components1 = this.components[1] * tcomponents0 + this.components[7] * tcomponents2;
        float components7 = this.components[1] * tcomponents6 + this.components[7] * tcomponents8;
        float components2 = this.components[2] * tcomponents0 + this.components[8] * tcomponents2;
        float components8 = this.components[2] * tcomponents6 + this.components[8] * tcomponents8;

        this.components[0] = components0;
        this.components[6] = components6;
        this.components[1] = components1;
        this.components[7] = components7;
        this.components[2] = components2;
        this.components[8] = components8;
    }

    private void rotateZ(Quaternion quaternion) {
        float z = quaternion.getD();
        float w = quaternion.getA();

        float zz = 2.0F * z * z;

        float tcomponents0 = 1.0F - zz;
        float tcomponents4 = 1.0F - zz;

        float zw = z * w;

        float tcomponents1 = 2.0F * (0.0F + zw);
        float tcomponents3 = 2.0F * (0.0F - zw);

        float components0 = this.components[0] * tcomponents0 + this.components[3] * tcomponents1;
        float components3 = this.components[0] * tcomponents3 + this.components[3] * tcomponents4;
        float components1 = this.components[1] * tcomponents0 + this.components[4] * tcomponents1;
        float components4 = this.components[1] * tcomponents3 + this.components[4] * tcomponents4;
        float components2 = this.components[2] * tcomponents0 + this.components[5] * tcomponents1;
        float components5 = this.components[2] * tcomponents3 + this.components[5] * tcomponents4;

        this.components[0] = components0;
        this.components[3] = components3;
        this.components[1] = components1;
        this.components[4] = components4;
        this.components[2] = components2;
        this.components[5] = components5;
    }

    private void rotateXYZ(Quaternion quaternion) {
        float x = quaternion.getB();
        float y = quaternion.getC();
        float z = quaternion.getD();
        float w = quaternion.getA();

        float xx = 2.0F * x * x;
        float yy = 2.0F * y * y;
        float zz = 2.0F * z * z;

        float tcomponents0 = 1.0F - yy - zz;
        float tcomponents4 = 1.0F - zz - xx;
        float tcomponents8 = 1.0F - xx - yy;

        float xy = x * y;
        float yz = y * z;
        float zx = z * x;
        float xw = x * w;
        float yw = y * w;
        float zw = z * w;

        float tcomponents1 = 2.0F * (xy + zw);
        float tcomponents3 = 2.0F * (xy - zw);
        float tcomponents2 = 2.0F * (zx - yw);
        float tcomponents6 = 2.0F * (zx + yw);
        float tcomponents5 = 2.0F * (yz + xw);
        float tcomponents7 = 2.0F * (yz - xw);

        float components0 = this.components[0] * tcomponents0 + this.components[3] * tcomponents1 + this.components[6] * tcomponents2;
        float components3 = this.components[0] * tcomponents3 + this.components[3] * tcomponents4 + this.components[6] * tcomponents5;
        float components6 = this.components[0] * tcomponents6 + this.components[3] * tcomponents7 + this.components[6] * tcomponents8;
        float components1 = this.components[1] * tcomponents0 + this.components[4] * tcomponents1 + this.components[7] * tcomponents2;
        float components4 = this.components[1] * tcomponents3 + this.components[4] * tcomponents4 + this.components[7] * tcomponents5;
        float components7 = this.components[1] * tcomponents6 + this.components[4] * tcomponents7 + this.components[7] * tcomponents8;
        float components2 = this.components[2] * tcomponents0 + this.components[5] * tcomponents1 + this.components[8] * tcomponents2;
        float components5 = this.components[2] * tcomponents3 + this.components[5] * tcomponents4 + this.components[8] * tcomponents5;
        float components8 = this.components[2] * tcomponents6 + this.components[5] * tcomponents7 + this.components[8] * tcomponents8;

        this.components[0] = components0;
        this.components[3] = components3;
        this.components[6] = components6;
        this.components[1] = components1;
        this.components[4] = components4;
        this.components[7] = components7;
        this.components[2] = components2;
        this.components[5] = components5;
        this.components[8] = components8;
    }
}

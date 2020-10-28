package me.jellysquid.mods.sodium.mixin.core.matrix;

import me.jellysquid.mods.sodium.client.util.Norm3b;
import me.jellysquid.mods.sodium.client.util.math.Matrix3fExtended;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix3f.class)
public abstract class MixinMatrix3f implements Matrix3fExtended {
    @Shadow
    public abstract void set(int row, int column, float value);

    @Shadow
    public abstract float get(int row, int column);

    @Override
    public float transformVecX(float x, float y, float z) {
        return this.get(0, 0) * x + this.get(0, 1) * y + this.get(0, 2) * z;
    }

    @Override
    public float transformVecY(float x, float y, float z) {
        return this.get(1, 0) * x + this.get(1, 1) * y + this.get(1, 2) * z;
    }

    @Override
    public float transformVecZ(float x, float y, float z) {
        return this.get(2, 0) * x + this.get(2, 1) * y + this.get(2, 2) * z;
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

        float x2 = this.get(0, 0) * x + this.get(0, 1) * y + this.get(0, 2) * z;
        float y2 = this.get(1, 0) * x + this.get(1, 1) * y + this.get(1, 2) * z;
        float z2 = this.get(2, 0) * x + this.get(2, 1) * y + this.get(2, 2) * z;

        return Norm3b.pack(x2, y2, z2);
    }

    private void rotateX(Quaternion quaternion) {
        float x = quaternion.getB();
        float w = quaternion.getA();

        float xx = 2.0F * x * x;

        float ta11 = 1.0F - xx;
        float ta22 = 1.0F - xx;

        float xw = x * w;
        float ta21 = 2.0F * xw;
        float ta12 = 2.0F * -xw;

        float a01 = this.get(0, 1) * ta11 + this.get(0, 2) * ta21;
        float a02 = this.get(0, 1) * ta12 + this.get(0, 2) * ta22;
        float a11 = this.get(1, 1) * ta11 + this.get(1, 2) * ta21;
        float a12 = this.get(1, 1) * ta12 + this.get(1, 2) * ta22;
        float a21 = this.get(2, 1) * ta11 + this.get(2, 2) * ta21;
        float a22 = this.get(2, 1) * ta12 + this.get(2, 2) * ta22;

        this.set(0, 1, a01);
        this.set(0, 2, a02);
        this.set(1, 1, a11);
        this.set(1, 2, a12);
        this.set(2, 1, a21);
        this.set(2, 2, a22);
    }

    private void rotateY(Quaternion quaternion) {
        float y = quaternion.getC();
        float w = quaternion.getA();

        float yy = 2.0F * y * y;

        float ta00 = 1.0F - yy;
        float ta22 = 1.0F - yy;

        float yw = y * w;

        float ta20 = 2.0F * (-yw);
        float ta02 = 2.0F * (+yw);

        float a00 = this.get(0, 0) * ta00 + this.get(0, 2) * ta20;
        float a02 = this.get(0, 0) * ta02 + this.get(0, 2) * ta22;
        float a10 = this.get(1, 0) * ta00 + this.get(1, 2) * ta20;
        float a12 = this.get(1, 0) * ta02 + this.get(1, 2) * ta22;
        float a20 = this.get(2, 0) * ta00 + this.get(2, 2) * ta20;
        float a22 = this.get(2, 0) * ta02 + this.get(2, 2) * ta22;

        this.set(0, 0, a00);
        this.set(0, 2, a02);
        this.set(1, 0, a10);
        this.set(1, 2, a12);
        this.set(2, 0, a20);
        this.set(2, 2, a22);
    }

    private void rotateZ(Quaternion quaternion) {
        float z = quaternion.getD();
        float w = quaternion.getA();

        float zz = 2.0F * z * z;

        float ta00 = 1.0F - zz;
        float ta11 = 1.0F - zz;

        float zw = z * w;

        float ta10 = 2.0F * (0.0F + zw);
        float ta01 = 2.0F * (0.0F - zw);

        float a00 = this.get(0, 0) * ta00 + this.get(0, 1) * ta10;
        float a01 = this.get(0, 0) * ta01 + this.get(0, 1) * ta11;
        float a10 = this.get(1, 0) * ta00 + this.get(1, 1) * ta10;
        float a11 = this.get(1, 0) * ta01 + this.get(1, 1) * ta11;
        float a20 = this.get(2, 0) * ta00 + this.get(2, 1) * ta10;
        float a21 = this.get(2, 0) * ta01 + this.get(2, 1) * ta11;

        this.set(0, 0, a00);
        this.set(0, 1, a01);
        this.set(1, 0, a10);
        this.set(1, 1, a11);
        this.set(2, 0, a20);
        this.set(2, 1, a21);
    }

    private void rotateXYZ(Quaternion quaternion) {
        float x = quaternion.getB();
        float y = quaternion.getC();
        float z = quaternion.getD();
        float w = quaternion.getA();

        float xx = 2.0F * x * x;
        float yy = 2.0F * y * y;
        float zz = 2.0F * z * z;

        float ta00 = 1.0F - yy - zz;
        float ta11 = 1.0F - zz - xx;
        float ta22 = 1.0F - xx - yy;

        float xy = x * y;
        float yz = y * z;
        float zx = z * x;
        float xw = x * w;
        float yw = y * w;
        float zw = z * w;

        float ta10 = 2.0F * (xy + zw);
        float ta01 = 2.0F * (xy - zw);
        float ta20 = 2.0F * (zx - yw);
        float ta02 = 2.0F * (zx + yw);
        float ta21 = 2.0F * (yz + xw);
        float ta12 = 2.0F * (yz - xw);

        float a00 = this.get(0, 0) * ta00 + this.get(0, 1) * ta10 + this.get(0, 2) * ta20;
        float a01 = this.get(0, 0) * ta01 + this.get(0, 1) * ta11 + this.get(0, 2) * ta21;
        float a02 = this.get(0, 0) * ta02 + this.get(0, 1) * ta12 + this.get(0, 2) * ta22;
        float a10 = this.get(1, 0) * ta00 + this.get(1, 1) * ta10 + this.get(1, 2) * ta20;
        float a11 = this.get(1, 0) * ta01 + this.get(1, 1) * ta11 + this.get(1, 2) * ta21;
        float a12 = this.get(1, 0) * ta02 + this.get(1, 1) * ta12 + this.get(1, 2) * ta22;
        float a20 = this.get(2, 0) * ta00 + this.get(2, 1) * ta10 + this.get(2, 2) * ta20;
        float a21 = this.get(2, 0) * ta01 + this.get(2, 1) * ta11 + this.get(2, 2) * ta21;
        float a22 = this.get(2, 0) * ta02 + this.get(2, 1) * ta12 + this.get(2, 2) * ta22;

        this.set(0, 0, a00);
        this.set(0, 1, a01);
        this.set(0, 2, a02);
        this.set(1, 0, a10);
        this.set(1, 1, a11);
        this.set(1, 2, a12);
        this.set(2, 0, a20);
        this.set(2, 1, a21);
        this.set(2, 2, a22);
    }
}

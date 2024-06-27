package io.github.kosmx.bendylib.impl;

import net.minecraft.core.Direction;
import org.joml.*;

import java.lang.Math;

/**
 * Bending methods.
 * Interface to be usable via Mixin
 */
public interface IBendable {

    /**
     * Applies the transformation to every position in posSupplier
     * @param bendAxis axis for the bend
     * @param bendValue bend value
     * @param posSupplier iterable positions
     * @return the used transformation matrix
     */
    default Matrix4f applyBend(float bendAxis, float bendValue, IterableRePos posSupplier){
        Vector3f axis = new Vector3f((float) Math.cos(bendAxis), 0, (float) Math.sin(bendAxis));
        Matrix3f matrix3f = new Matrix3f().set(getBendDirection().getRotation());
        axis.mul(matrix3f);
        Matrix4f transformMatrix = new Matrix4f();

        transformMatrix.translate(getBendX(), getBendY(), getBendZ());
        //transformMatrix.mul(axis.getRadialQuaternion(bendValue));
        transformMatrix.rotate(bendValue, axis);
        transformMatrix.translate(-getBendX(), -getBendY(), -getBendZ());

        Vector3f directionUnit; //some temporarily variable;

        Plane basePlane = getBasePlane();
        Plane otherPlane = getOtherSidePlane();

        directionUnit = this.getBendDirection().step();
        directionUnit.cross(axis);
        //parallel to the bend's axis and to the cube's bend direction
        Plane bendPlane = new Plane(directionUnit, new Vector3f(this.getBendX(), this.getBendY(), this.getBendZ()));
        float halfSize = bendHeight()/2;

        boolean bl = isBendInverted();

        posSupplier.iteratePositions(iPosWithOrigin -> {
            Vector3f newPos = iPosWithOrigin.getOriginalPos();
            float distFromBend = bl ? -bendPlane.distanceTo(newPos) : bendPlane.distanceTo(newPos);
            float distFromBase = basePlane.distanceTo(newPos);
            float distFromOther = otherPlane.distanceTo(newPos);
            double s = Math.tan(bendValue/2)*distFromBend;
            Vector3f x = getBendDirection().step();
            if(Math.abs(distFromBase) < Math.abs(distFromOther)){
                x.mul((float) (-distFromBase/halfSize*s));
                newPos.add(x);
                Vector4f reposVector = new Vector4f(newPos, 1f);
                reposVector.mul(transformMatrix);
                newPos = new Vector3f(reposVector.x, reposVector.y, reposVector.z);
            }
            else {
                x.mul((float) (-distFromOther/halfSize*s));
                newPos.add(x);
            }
            iPosWithOrigin.setPos(newPos);
        });

        return transformMatrix;
    }

    default boolean isBendInverted() {
        return getBendDirection() == Direction.UP || getBendDirection() == Direction.SOUTH || getBendDirection() == Direction.EAST;
    }

    Direction getBendDirection();

    /**
     * center x
     * @return x
     */
    float getBendX();
    float getBendY();
    float getBendZ();
    Plane getBasePlane();
    Plane getOtherSidePlane();

    /**
     * Distance between the two opposite surface of the cuboid.
     * Calculate two plane distance is inefficient.
     * Try to override it (If you have size)
     * @return the size of the cube
     */
    default float bendHeight(){
        return this.getBasePlane().distanceTo(this.getOtherSidePlane());
    }

    /**
     * A plane in the 3d space
     * form a vector and a position
     * for distance calculation
     */
    final class Plane{
        public final Vector3f normal;
        private final float normDistance;

        public Plane(Vector3f normal, Vector3f position){
            this.normal = normal;
            this.normal.normalize();
            this.normDistance = -this.normal.dot(position);
        }

        private Plane(Vector3f normal, float normDistance) {
            this.normal = normal;
            this.normDistance = normDistance;
        }

        public Plane scaled(float scalar) {
            return new Plane(new Vector3f(normal), normDistance * scalar);
        }

        /**
         * This will return with the SIGNED distance
         * @param pos some pos
         * @return the distance between the pos and this plane
         */
        public float distanceTo(Vector3f pos){
            return normal.dot(pos) + normDistance;
        }

        /**
         * This will return with the SIGNED distance
         * @param otherPlane some plane
         * @return the distance between the two planes. 0 if not parallel
         */
        public float distanceTo(Plane otherPlane){
            Vector3f tmp = new Vector3f(this.normal);
            tmp.cross(otherPlane.normal);
            //if the lines are parallel
            if(tmp.dot(tmp) < 0.01){
                return this.normDistance + this.normal.dot(otherPlane.normal) * otherPlane.normDistance; //if the normals point to the opposite direction
            }
            else {
                return 0;
            }
        }
    }
}

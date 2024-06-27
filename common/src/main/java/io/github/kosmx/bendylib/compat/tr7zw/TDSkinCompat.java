package io.github.kosmx.bendylib.compat.tr7zw;

import dev.tr7zw.skinlayers.api.MeshTransformer;
import dev.tr7zw.skinlayers.api.SkinLayersAPI;
import io.github.kosmx.bendylib.ModelPartAccessor;
import io.github.kosmx.bendylib.MutableCuboid;
import io.github.kosmx.bendylib.impl.BendableCuboid;
import io.github.kosmx.bendylib.impl.IBendable;
import io.github.kosmx.bendylib.impl.IPosWithOrigin;
import io.github.kosmx.bendylib.impl.RememberingPos;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class TDSkinCompat {
    public static void init() throws ClassNotFoundException, NoClassDefFoundError {
        SkinLayersAPI.setupMeshTransformerProvider(modelPart -> {
            var sourceCuboidOptional = ModelPartAccessor.optionalGetCuboid(modelPart, 0);
            if (sourceCuboidOptional.isPresent()
                    && sourceCuboidOptional.get().getActiveMutator() != null
                    && sourceCuboidOptional.get().getActiveMutator().getB() instanceof BendableCuboid bendableSource) {


                class Bender extends BendyMeshTransformer implements MeshTransformer {

                    private Consumer<IPosWithOrigin> transform = null;

                    private Bender(BendableCuboid cuboid) {
                        super(cuboid);
                        applyBend(bendableSource.getBendAxis(), bendableSource.getBend(), consumer -> transform = consumer);
                        assert (transform != null);
                    }

                    /**
                     * @param vec3f quad normal vector
                     * @param vector4fs quad vertices
                     */
                    @Override
                    public void transform(Vector3f vec3f, Vector4f[] vector4fs) {
                        for (int i = 0; i < vector4fs.length; i++) {
                            var pos = new RememberingPos(new Vector3f(vector4fs[i].x, vector4fs[i].y, vector4fs[i].z));
                            transform.accept(pos);
                            vector4fs[i] = new Vector4f(pos.getPos(), 1);
                        }
                        vec3f.set(calculateNormal(vector4fs));

                    }

                    @Override
                    public void transform(ModelPart.Cube cuboid) {
                        var sourceCuboid = sourceCuboidOptional.get();
                        var mutator = sourceCuboidOptional.get().getActiveMutator();

                        if (cuboid instanceof MutableCuboid mutableCuboid) {
                            if (!mutableCuboid.hasMutator(mutator.getA())) {
                                mutableCuboid.registerMutator(mutator.getA(),
                                        data -> new BendableCuboid.Builder().setDirection(getBendDirection()).build(data,
                                                (sides, positions, minX, minY, minZ, maxX, maxY, maxZ, fixX, fixY, fixZ,
                                                 direction, basePlane, otherPlane, fullSize) ->
                                                        new ModifiedBendableCuboid(sides, positions, minX, minY, minZ, maxX, maxY, maxZ, fixX, fixY, fixZ, direction,
                                                                getBasePlane().scaled(16), getOtherSidePlane().scaled(16), bendHeight()*16)));
                            }

                            mutableCuboid.copyStateFrom(sourceCuboid);

                        }
                    }
                }
                return new Bender(bendableSource) {
                };
            }
            return new MeshTransformer() {
                @Override
                public void transform(Vector3f vec3f, Vector4f[] vector4fs) {
                    //empty
                }

                @Override
                public void transform(ModelPart.Cube cuboid) {
                    ((MutableCuboid) cuboid).getAndActivateMutator(null);
                }
            };
        });
    }


    public static Vector3f calculateNormal(Vector4f[] vertices) {
        Vector3f buf = new Vector3f(vertices[3].x, vertices[3].y, vertices[3].z);
        buf.negate();
        Vector3f vecB = new Vector3f(vertices[1].x, vertices[1].y, vertices[1].z);
        vecB.add(buf);
        buf = new Vector3f(vertices[2].x, vertices[2].y, vertices[2].z);
        buf.negate();
        Vector3f vecA = new Vector3f(vertices[0].x, vertices[0].y, vertices[0].z);
        vecA.add(buf);
        vecA.cross(vecB);
        //Return the cross product, if it's zero then return anything non-zero to not cause crash...
        return vecA.normalize().isFinite() ? vecA : Direction.NORTH.step();
    }

    private static class ModifiedBendableCuboid extends BendableCuboid {

        protected ModifiedBendableCuboid(Quad[] sides, RememberingPos[] positions, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float fixX, float fixY, float fixZ, Direction direction, Plane basePlane, Plane otherPlane, float fullSize) {
            super(sides, positions, minX, minY, minZ, maxX, maxY, maxZ, fixX, fixY, fixZ, direction, basePlane, otherPlane, fullSize);
        }

    }

    private static class BendyMeshTransformer implements IBendable {
        private final Direction bendDirection;
        private final float bendX, bendY, bendZ;
        private final Plane basePlane, otherSidePlane;

        private final float bendHeight;

        private BendyMeshTransformer(Direction bendDirection, float bendX, float bendY, float bendZ, Plane basePlane, Plane otherSidePlane, float bendHeight) {
            this.bendDirection = bendDirection;
            this.bendX = bendX / 16;
            this.bendY = bendY / 16;
            this.bendZ = bendZ / 16;
            this.basePlane = basePlane.scaled(1 / 16f);
            this.otherSidePlane = otherSidePlane.scaled(1 / 16f);
            this.bendHeight = bendHeight / 16;
        }

        private BendyMeshTransformer(BendableCuboid cuboid) {
            this(cuboid.getBendDirection(), cuboid.getBendX(), cuboid.getBendY(), cuboid.getBendZ(),
                    cuboid.getBasePlane(), cuboid.getOtherSidePlane(), cuboid.bendHeight());
        }

        @Override
        public float bendHeight() {
            return bendHeight;
        }

        @Override
        public Direction getBendDirection() {
            return bendDirection;
        }

        @Override
        public float getBendX() {
            return bendX;
        }

        @Override
        public float getBendY() {
            return bendY;
        }

        @Override
        public float getBendZ() {
            return bendZ;
        }

        @Override
        public Plane getBasePlane() {
            return basePlane;
        }

        @Override
        public Plane getOtherSidePlane() {
            return otherSidePlane;
        }
    }
}

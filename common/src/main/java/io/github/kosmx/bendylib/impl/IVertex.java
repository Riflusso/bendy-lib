package io.github.kosmx.bendylib.impl;

import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;

/**
 * To create custom Vertices
 * You can use {@link Vertex} or {@link RepositionableVertex}
 */
public interface IVertex {

    /**
     * get the vertex's position
     * @return pos
     */
    Vector3f getPos();

    /**
     * get texture u coordinate
     * @return texture U
     */
    float getU();

    /**
     * get texture v coordinate
     * @return texture V
     */
    float getV();

    /**
     * Keep the original position, set unique texture coordinates.
     * @param u Texture U
     * @param v Texture V
     * @return The remapped IVertex
     */
    IVertex remap(float u, float v);

    default ModelPart.Vertex toMojVertex(){
        return new ModelPart.Vertex(this.getPos(), getU(), getV());
    }

}

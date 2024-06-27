package io.github.kosmx.bendylib.impl.accessors;

import io.github.kosmx.bendylib.ModelPartAccessor;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;
import java.util.Map;

/**
 * Basic operation to access cuboid in ModelPart
 */
public interface IModelPartAccessor {

    List<ModelPart.Cube> getCuboids();

    Map<String, ModelPart> getChildren(); //easy to search in it :D

    void setWorkaround(ModelPartAccessor.Workaround workaround);

}

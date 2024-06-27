package io.github.kosmx.bendylib;

import io.github.kosmx.bendylib.impl.ICuboid;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;

public interface MutableCuboid {

    /**
     * Register a mutator to a cuboid
     * @param name registration name
     * @param builder ICuboid builder
     * @return is the registration success
     */
    boolean registerMutator(String name, ICuboidBuilder<ICuboid> builder);

    /**
     * Unregister a mutator
     * @param name registration name
     * @return could unregister something
     */
    boolean unregisterMutator(String name);

    /**
     * Get the active mutator and its identifier
     * @return null, if no active
     */
    @Nullable
    Tuple<String, ICuboid> getActiveMutator();

    /**
     * Check if mutator with key exists
     * @param key key
     * @return is the mutator registered (even if not active)
     */
    boolean hasMutator(String key);

    @Nullable
    ICuboidBuilder<ICuboid> getCuboidBuilder(String key);

    /**
     * Get a mutator
     * @param name mutator identifier
     * @return null, if no mutator exists or created
     */
    @Nullable
    @Deprecated
    //it can be removed in any future version
    ICuboid getMutator(String name);

    /**
     * Get a mutator and make it the active
     * @param name mutator identifier
     * @return null, if no registered
     */
    @Nullable
    ICuboid getAndActivateMutator(@Nullable String name);

    void copyStateFrom(MutableCuboid other);

}

package io.github.kosmx.bendylib.impl;

import io.github.kosmx.bendylib.ICuboidBuilder;
import io.github.kosmx.bendylib.MutableCuboid;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@Deprecated
@ApiStatus.Experimental
public class DummyCuboid implements MutableCuboid {

    private static boolean bl = true;

    public DummyCuboid(){
        if(bl) {
            System.out.println("A Dummy cuboid was created. that is not good, but can be worse");
            bl = false;
        }
    }
    @Override
    public boolean registerMutator(String name, ICuboidBuilder<ICuboid> builder) {
        return false;
    }

    @Override
    public boolean unregisterMutator(String name) {
        return true;
    }

    @Nullable
    @Override
    public Tuple<String, ICuboid> getActiveMutator() {
        return null;
    }

    @Override
    public boolean hasMutator(String key) {
        return false;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ICuboidBuilder<ICuboid> getCuboidBuilder(String key) {
        return null;
    }

    @Nullable
    @Override
    public ICuboid getMutator(String name) {
        return null;
    }

    @Nullable
    @Override
    public ICuboid getAndActivateMutator(@Nullable String name) {
        return null;
    }

    @Override
    public void copyStateFrom(MutableCuboid other) {

    }
}

package top.leonx.vanity.util;

import net.minecraft.world.World;
import top.leonx.vanity.entity.DummyLivingEntity;
import top.leonx.vanity.init.ModEntityTypes;

import java.lang.ref.WeakReference;

public class DummyLivingEntityHolder {
    private static WeakReference<DummyLivingEntity> dummyLivingEntity= new WeakReference<>(null);

    public static WeakReference<DummyLivingEntity> getDummyLivingEntity(World world)
    {
        if (dummyLivingEntity.get() == null)
        {
            dummyLivingEntity = new WeakReference<>(ModEntityTypes.DUMMY_LIVING_ENTITY.get().create(world));
        }
        return dummyLivingEntity;
    }
}

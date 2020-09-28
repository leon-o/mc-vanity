package top.leonx.vanity.entity;

import net.minecraft.entity.CreatureEntity;

public interface IHasFoodStats<T extends CreatureEntity & IHasFoodStats<?>> {
    GeneralFoodStats<T> getFoodStats();
    boolean shouldHeal();
    void addExhaustion(float exhaustion);
}

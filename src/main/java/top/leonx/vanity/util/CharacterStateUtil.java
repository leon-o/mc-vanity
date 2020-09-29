package top.leonx.vanity.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.entity.OutsiderEntity;

public class CharacterStateUtil {
    public static boolean hasCharacterState(LivingEntity entity)
    {
        return entity instanceof PlayerEntity || entity instanceof OutsiderEntity;
    }

    public static void initCharacterState(CharacterState state)
    {
        double random = Math.random();
        state.setGender(random<0.5d?Gender.MALE: Gender.FEMALE);

        state.setNeedInit(false);
    }
}

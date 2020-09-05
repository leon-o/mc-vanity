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
        state.setGender(Math.random()<0.5?CharacterState.Gender.MALE: CharacterState.Gender.FEMALE);

        state.setNeedInit(false);
    }
}

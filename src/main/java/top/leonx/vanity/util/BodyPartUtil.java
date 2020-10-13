package top.leonx.vanity.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartRegistry;
import top.leonx.vanity.bodypart.BodyPartStack;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.entity.AbstractOutsider;

import java.util.*;
import java.util.stream.Collectors;

public class BodyPartUtil {

    public static List<BodyPartStack> getRandomBodyPart(CharacterState state, BodyPartGroup... groups) {
        List<BodyPartStack> list   = new ArrayList<>();
        Gender              gender = state.getGender();
        for (BodyPartGroup group : groups) {
            if (Math.random() <= group.getEmptyRate(BodyPartCapability.BodyPartData.EMPTY)) continue;
            TreeMap<Float, BodyPart> rankTree  = new TreeMap<>();
            Set<BodyPart>            bodyParts = BodyPartRegistry.getBodyParts(group).stream().filter(t -> t.getSuitableGender().isSuitable(gender)).collect(Collectors.toSet());
            float                    lastRank  = 0;
            for (BodyPart bodyPart : bodyParts) {
                lastRank += bodyPart.getProperty().getCommonness();
                rankTree.put(lastRank, bodyPart);
            }
            double                     randomFac = Math.random() * lastRank;
            SortedMap<Float, BodyPart> tailMap   = rankTree.tailMap((float) randomFac);
            BodyPart                   bodyPart  = tailMap.get(tailMap.firstKey());
            list.add(new BodyPartStack(bodyPart, bodyPart.getRandomColor()));
        }
        BodyPartCapability.BodyPartData tmpData = new BodyPartCapability.BodyPartData();
        tmpData.getItemStacksList().addAll(list);
        List<BodyPartStack> result = new ArrayList<>();
        for (BodyPartStack bodyPartStack : list) {
            if (bodyPartStack.getItem().getProperty().precondition.apply(tmpData)) result.add(bodyPartStack);
        }
        result.forEach(t -> t.getItem().adjustWithContext(result, t));
        return result;
    }

    public static boolean hasBodyPart(LivingEntity entity) {
        return entity instanceof PlayerEntity || entity instanceof AbstractOutsider || entity instanceof ZombieEntity || entity instanceof VillagerEntity;
    }


}

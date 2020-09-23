package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.util.AIUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EquipArmorTask extends BehaviorTreeTask<OutsiderEntity> {

    private final List<ItemStack>       itemsSelected = new ArrayList<>();
    public        Comparator<ItemStack> equipmentComparator;
    boolean delayed = false;

    public EquipArmorTask() {
        this(Comparator.comparingDouble(t -> AIUtil.getModifiedArmor(1, t) + AIUtil.getModifiedArmorToughness(1, t)));
    }

    public EquipArmorTask(Comparator<ItemStack> equipmentComparator) {
        this.equipmentComparator = equipmentComparator;
    }

    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }

    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        itemsSelected.add(entity.inventory.findItemStack(t -> MobEntity.getSlotForItemStack(t) == EquipmentSlotType.HEAD, equipmentComparator));
        itemsSelected.add(entity.inventory.findItemStack(t -> MobEntity.getSlotForItemStack(t) == EquipmentSlotType.CHEST, equipmentComparator));
        itemsSelected.add(entity.inventory.findItemStack(t -> MobEntity.getSlotForItemStack(t) == EquipmentSlotType.LEGS, equipmentComparator));
        itemsSelected.add(entity.inventory.findItemStack(t -> MobEntity.getSlotForItemStack(t) == EquipmentSlotType.FEET, equipmentComparator));
    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if (itemsSelected.size() > 0) {
            ItemStack         selectedItem = itemsSelected.get(0);
            EquipmentSlotType slot         = MobEntity.getSlotForItemStack(selectedItem);


            if (!entity.getItemStackFromSlot(slot).equals(selectedItem) && !selectedItem.isEmpty()) {
                if (!delayed) {
                    setUpDelay(MathHelper.nextInt(entity.getRNG(), 8, 20));
                    delayed = true;
                    return;
                } else {
                    delayed = false;
                }
                entity.EquipItem(slot, selectedItem);
            }
            itemsSelected.remove(0);
        } else {
            submitResult(Result.SUCCESS);
        }
    }
}

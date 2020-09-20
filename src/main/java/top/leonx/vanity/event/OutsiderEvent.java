package top.leonx.vanity.event;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import top.leonx.vanity.entity.OutsiderEntity;

import java.lang.annotation.Annotation;
import java.util.UUID;

public class OutsiderEvent extends Event {
    private final OutsiderEntity entity;

    public OutsiderEvent(OutsiderEntity entity)
    {
        this.entity = entity;
    }

    public OutsiderEntity getEntity()
    {
        return entity;
    }

    @Cancelable
    public static class PickItemEvent extends OutsiderEvent {
        private final ItemStack itemStack;
        private final UUID      itemOwner;
        private final UUID    itemThrower;

        public PickItemEvent(OutsiderEntity entity, ItemStack itemStack, UUID itemOwner, UUID itemThrower) {
            super(entity);
            this.itemStack = itemStack;
            this.itemOwner = itemOwner;
            this.itemThrower = itemThrower;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public UUID getItemOwner() {
            return itemOwner;
        }

        public UUID getItemThrower() {
            return itemThrower;
        }
    }
}

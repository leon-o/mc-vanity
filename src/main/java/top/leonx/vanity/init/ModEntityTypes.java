package top.leonx.vanity.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.entity.OutsiderEntity;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, VanityMod.MOD_ID);
    public static final RegistryObject<EntityType<OutsiderEntity>> OUTSIDER_ENTITY_ENTITY_TYPE = ENTITY_TYPES.register("outsider",()-> EntityType.Builder.create(OutsiderEntity::new, EntityClassification.CREATURE).size(0.6f,
                                                                                                                                                                                                                      1.8f).setShouldReceiveVelocityUpdates(false).build("outsider"));
}

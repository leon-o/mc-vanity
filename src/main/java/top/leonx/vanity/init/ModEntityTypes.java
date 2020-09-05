package top.leonx.vanity.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import top.leonx.vanity.entity.OutsiderEntity;

public class ModEntityTypes {
    public static final EntityType<OutsiderEntity> OUTSIDER_ENTITY_ENTITY_TYPE = (EntityType<OutsiderEntity>) EntityType.Builder.create(OutsiderEntity::new, EntityClassification.CREATURE).size(0.6f,
                                                                                                                                                                                                 1.8f).setShouldReceiveVelocityUpdates(false).build("outsider").setRegistryName("outsider");
}

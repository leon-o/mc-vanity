package top.leonx.vanity.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.bodypart.BodyPartRegistryEntry;
import top.leonx.vanity.container.BedContainer;
import top.leonx.vanity.container.OutsiderDialogContainer;
import top.leonx.vanity.container.OutsiderInventoryContainer;
import top.leonx.vanity.container.VanityMirrorContainer;
import top.leonx.vanity.tileentity.VanityBedTileEntity;

public class ModContainerTypes {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, VanityMod.MOD_ID);

    public static final RegistryObject<ContainerType<VanityMirrorContainer>>  VANITY_MIRROR_CONTAINER =
            CONTAINER_TYPES.register("vanity_mirror",()->IForgeContainerType.create(VanityMirrorContainer::new)) ;

    public static final RegistryObject<ContainerType<OutsiderDialogContainer>> OUTSIDER_DIALOG =
            CONTAINER_TYPES.register("outsider_dialog",()->IForgeContainerType.create(OutsiderDialogContainer::new)) ;

    public static final RegistryObject<ContainerType<OutsiderInventoryContainer>> OUTSIDER_INVENTORY =
            CONTAINER_TYPES.register("outsider_inventory",()->IForgeContainerType.create(OutsiderInventoryContainer::new)) ;

    public static final RegistryObject<ContainerType<BedContainer>> VANITY_BED_CONTAINER =
            CONTAINER_TYPES.register("vanity_bed",()->IForgeContainerType.create(BedContainer::new));
}

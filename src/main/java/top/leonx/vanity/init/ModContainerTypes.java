package top.leonx.vanity.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import top.leonx.vanity.container.OutsiderContainer;
import top.leonx.vanity.container.VanityMirrorContainer;

public class ModContainerTypes {
    public static final ContainerType<VanityMirrorContainer> VANITY_MIRROR_CONTAINER =
            IForgeContainerType.create(VanityMirrorContainer::new);
    public static final ContainerType<OutsiderContainer> OUTSIDER  =IForgeContainerType.create(OutsiderContainer::new);

    public static final ContainerType<?>[] CONTAINER_TYPES=new ContainerType[]{
            VANITY_MIRROR_CONTAINER.setRegistryName("vanity_mirror"),OUTSIDER.setRegistryName("outsider")
    };
}

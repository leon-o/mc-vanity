package top.leonx.vanity.init;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.capability.CharacterStateCapabality;

public class ModCapabilityTypes {
    @CapabilityInject(CharacterState.class)
    public static Capability<CharacterState>                  CHARACTER_STATE;
    @CapabilityInject(BodyPartCapability.BodyPartData.class)
    public static Capability<BodyPartCapability.BodyPartData> BODY_PART;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(BodyPartCapability.BodyPartData.class, new BodyPartCapability.Storage(), new BodyPartCapability.BodyPartDataFactory());
        CapabilityManager.INSTANCE.register(CharacterState.class, new CharacterStateCapabality.Storage(), new CharacterStateCapabality.CharacterStateFactory());

    }
}

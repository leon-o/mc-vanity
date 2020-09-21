package top.leonx.vanity.init;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES_TYPES = new DeferredRegister<>(ForgeRegistries.PARTICLE_TYPES, VanityMod.MOD_ID);


    public static final RegistryObject<BasicParticleType> GREEN_HEART =PARTICLES_TYPES.register("green_heart", ()->new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> PINK_HEART =PARTICLES_TYPES.register("pink_heart", ()->new BasicParticleType(false));
}

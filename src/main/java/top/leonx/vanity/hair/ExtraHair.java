package top.leonx.vanity.hair;

public abstract class ExtraHair extends VanityHair {
    public ExtraHair(String registryName) {
        super(registryName, HairRegistry.HairType.EXTRA);
    }
}

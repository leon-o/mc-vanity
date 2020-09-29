package top.leonx.vanity.bodypart;

import java.util.Objects;
import java.util.function.Supplier;

public class BodyPartRegistryEntry implements Supplier<BodyPart> {
    Supplier<BodyPart> bodyPartCreate;
    String             name;

    public BodyPartRegistryEntry(Supplier<BodyPart> bodyPartCreate, String name) {
        this.bodyPartCreate = bodyPartCreate;
        this.name = name;
    }

    public void updateReference(BodyPart bodyPart)
    {
        value=bodyPart;
    }

    BodyPart value;
    @Override
    public BodyPart get() {
        BodyPart ret = this.value;
        Objects.requireNonNull(ret, () -> "Registry Object not present: " + this.name);
        return ret;
    }
}

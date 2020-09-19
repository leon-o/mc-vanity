package top.leonx.vanity.worldgen;

import net.minecraft.world.gen.feature.structure.IStructurePieceType;

public class ModStructurePieceTypes {
    public static final IStructurePieceType TEST = IStructurePieceType.register(TestPiece::new,"vanity:test");
}

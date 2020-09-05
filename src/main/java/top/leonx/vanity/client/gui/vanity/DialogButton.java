package top.leonx.vanity.client.gui.vanity;

import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;

public class DialogButton extends ToggleWidget {
    public static final ResourceLocation DIALOG_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/dialog.png");

    public DialogButton(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn, false);
        initTextureValues(0,80,64,32,DIALOG_TEX);
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        stateTriggered=true;
    }

    @Override
    public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
        stateTriggered=false;
    }
}

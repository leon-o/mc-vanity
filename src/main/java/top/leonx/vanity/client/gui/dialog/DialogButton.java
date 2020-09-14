package top.leonx.vanity.client.gui.dialog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;

public class DialogButton extends ToggleWidget {
    public static final ResourceLocation DIALOG_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/dialog.png");


    public IPressable onPress;
    private int color=0X333333;

    //String msg;
    public DialogButton(int xIn, int yIn, int widthIn, int heightIn,String msg,IPressable onPress) {
        super(xIn, yIn, widthIn, heightIn, false);
        this.setMessage(msg);
        this.onPress=onPress;
        initTextureValues(0,80,64,32,DIALOG_TEX);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        stateTriggered=true;
        if(onPress!=null)
            onPress.press(this);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        super.renderButton(mouseX, mouseY, partialTicks);
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.drawString(getMessage(),this.x+(width-fontRenderer.getStringWidth(getMessage()))/2f,this.y + (this.height - 8) / 2f,color);
        //this.drawCenteredString(fontRenderer, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        stateTriggered=false;
    }

    public interface IPressable
    {
        void press(DialogButton button);
    }
}

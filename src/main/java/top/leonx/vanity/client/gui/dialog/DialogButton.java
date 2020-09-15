package top.leonx.vanity.client.gui.dialog;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;

public class DialogButton extends ToggleWidget {
    public static final ResourceLocation DIALOG_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/dialog.png");


    public IPressable onPress;
    private int color=0X333333;
    private final static float widthInTex=48;
    private final static float heightInTex=24;
    //String msg;
    public DialogButton(int xIn, int yIn, int widthIn, int heightIn,String msg,IPressable onPress) {
        super(xIn, yIn, widthIn, heightIn, false);
        this.setMessage(msg);
        this.onPress=onPress;
        initTextureValues(0,96,64,32,DIALOG_TEX);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        stateTriggered=true;
        if(onPress!=null)
            onPress.press(this);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int i = this.xTexStart;
        int j = this.yTexStart;
        if (this.stateTriggered) {
            i += this.xDiffTex;
        }

        if (this.isHovered()) {
            j += this.yDiffTex;
        }
        float scaleFactorW=width/widthInTex;
        float scaleFactorH=height/heightInTex;
        blit(this.x, this.y, (int)(i*scaleFactorW), (int)(j * scaleFactorH), this.width, this.height,(int)(256*scaleFactorW),(int) (256*scaleFactorH));

        RenderSystem.enableDepthTest();
        FontRenderer fontRenderer = minecraft.fontRenderer;
        fontRenderer.drawString(getMessage(),this.x+(width-fontRenderer.getStringWidth(getMessage()))/2f,this.y + (this.height - 8) / 2f,color);
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

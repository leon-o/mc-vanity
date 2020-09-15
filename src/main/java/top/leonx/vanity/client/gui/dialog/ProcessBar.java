package top.leonx.vanity.client.gui.dialog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;

public class ProcessBar extends AbstractGui implements IRenderable {
    public static final ResourceLocation DIALOG_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/dialog.png");

    double maxValue=1;
    public double value;
    int x,y,width,height;
    public Type type;
    public ProcessBar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public ProcessBar setMaxValue(double maxValue)
    {
        this.maxValue=maxValue;
        return this;
    }
    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        Minecraft.getInstance().getTextureManager().bindTexture(DIALOG_TEX);
        int texYStart=80;
        if(type==Type.PINK)
            texYStart+=10;
        blit(x,y,128*width/72f,texYStart,width,height,256*width/72,256*height/5);
        blit(x, y, 128*width/72f, texYStart+5, (int) (value/maxValue*width), height, 256*width/72, 256*height/5);
    }

    public enum Type{
        GREEN,
        PINK
    }
}

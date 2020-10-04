package top.leonx.vanity.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.TransformationMatrix;

import java.util.function.Supplier;

public class Label extends LayoutElement {
    Supplier<String> messageGetter;
    int color;
    boolean center;
    public Label(Supplier<String> messageGetter,int x,int y,int width,int height, int color) {
        super(x, y, width, height);
        this.messageGetter = messageGetter;
        this.color = color;
    }
    public Label(String message,int x,int y,int width,int height,int color)
    {
        this(()->message,x, y, width, height,color);
    }
    public Label(Supplier<String> messageGetter,int x,int y,int width,int height) {
        this(messageGetter,x, y, width, height, 0x202020);
    }
    public Label(String message,int x,int y,int width,int height)
    {
        this(()->message,x, y, width, height,0x202020);
    }

    public Label setCenter(boolean center)
    {
        this.center=center;
        return this;
    }

    @Override
    public void init() {

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        String       msg            = messageGetter.get();

        //RenderSystem.pushMatrix();
        if(center)
            drawStringNoDepth(fontRenderer, msg, getAbsulateX()+(width-fontRenderer.getStringWidth(msg))/2f, getAbsulateY()+(height-8)/2f, color);
        else
            drawStringNoDepth(fontRenderer,msg, getAbsulateX(), getAbsulateY(), color);
        //RenderSystem.popMatrix();
    }

    private void drawStringNoDepth(FontRenderer fontRenderer, String msg, float x, float y, int color) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(0,0,100);
        IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        fontRenderer.renderString(msg, x, y, color, false, TransformationMatrix.identity().getMatrix(), buffer, false, 0, 0xf000f0);
        buffer.finish();
        RenderSystem.popMatrix();
    }
}

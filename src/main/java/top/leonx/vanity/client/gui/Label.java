package top.leonx.vanity.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IRenderable;

import java.util.function.Supplier;

public class Label implements IRenderable {
    Supplier<String> messageGetter;
    int color;
    int x,y;
    boolean center;
    public Label(Supplier<String> messageGetter, int color) {
        this.messageGetter = messageGetter;
        this.color = color;
    }
    public Label(String message,int color)
    {
        this(()->message,color);
    }
    public Label(Supplier<String> messageGetter) {
        this(messageGetter, 0x808080);
    }
    public Label(String message)
    {
        this(()->message,0x808080);
    }
    public Label setXY(int x,int y)
    {
        this.x=x;
        this.y=y;
        return this;
    }
    public Label setCenter(boolean center)
    {
        this.center=center;
        return this;
    }
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        String       msg            = messageGetter.get();
        if(center)
            fontRenderer.drawString(msg, this.x+fontRenderer.getStringWidth(msg)/2f, this.y, color);
        else
            fontRenderer.drawString(msg, this.x, this.y, color);
    }
}

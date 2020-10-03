package top.leonx.vanity.client.gui;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IRenderable;

public abstract class LayoutElement extends AbstractGui implements IRenderable {
    public int x;
    public int y;
    public int width;
    public int height;
    public int marginLeft=0;
    public int marginTop=0;
    public int marginRight=0;
    public int marginBottom=0;
    public LayoutElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void init()
    {

    }
}

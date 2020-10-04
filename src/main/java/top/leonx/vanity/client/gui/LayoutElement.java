package top.leonx.vanity.client.gui;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IRenderable;

import java.util.ArrayList;
import java.util.List;

public abstract class LayoutElement extends AbstractGui implements IRenderable {
    private int x;
    private int y;
    public int width;
    public int height;
    public int marginLeft=0;
    public int marginTop=0;
    public int marginRight=0;
    public int marginBottom=0;
    private final List<LayoutElement> children=new ArrayList<>();
    public LayoutElement parent;
    public int getAbsulateX()
    {
        return parent==null?x:parent.x+x;
    }

    public int getAbsulateY()
    {
        return parent==null?y:parent.y+y;
    }

    public LayoutElement setXY(int x,int y)
    {
        this.x=x;
        this.y=y;
        return this;
    }
    /**
     *
     * @param x Relative X
     * @param y Relative Y
     */
    public LayoutElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void init();

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        for (LayoutElement child : children) {
            child.render(mouseX, mouseY, partialTicks);
        }
    }

    public void addChild(LayoutElement element)
    {
        element.parent=this;
        children.add(element);
    }

    public void removeChild(LayoutElement element)
    {
        element.parent=null;
        children.remove(element);
    }
}

package top.leonx.vanity.client.gui.vanity;

/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nullable;

import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import top.leonx.vanity.VanityMod;

/**
 * This class is blatantly stolen from iChunUtils with permission.
 *
 * @author iChun
 */
public class AdjustSlider extends ExtendedButton
{
    private final static ResourceLocation IMG_LOCATION=new ResourceLocation(VanityMod.MOD_ID, "textures/gui/vanity.png");
    /** The value of this slider control. */
    public double sliderValue = 1.0F;

    public String dispString = "";

    /** Is this slider control being dragged. */
    public boolean dragging = false;
    public boolean showDecimal = true;

    public double minValue = 0.0D;
    public double maxValue = 5.0D;

    @Nullable
    public ISlider parent = null;

    public String suffix = "";

    public boolean drawString = true;

    public AdjustSlider(int xPos, int yPos, int width, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, IPressable handler)
    {
        this(xPos, yPos, width, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, handler, null);
    }

    public AdjustSlider(int xPos, int yPos, int width, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, IPressable handler, @Nullable ISlider par)
    {
        super(xPos, yPos, width, 16, prefix, handler);
        minValue = minVal;
        maxValue = maxVal;
        sliderValue = (currentVal - minValue) / (maxValue - minValue);
        dispString = prefix;
        parent = par;
        suffix = suf;
        showDecimal = showDec;
        String val;

        if (showDecimal)
        {
            val = String.format("%.3f", sliderValue * (maxValue - minValue) + minValue);
        }
        else
        {
            val = Integer.toString((int)Math.round(sliderValue * (maxValue - minValue) + minValue));
        }

        setMessage(dispString + val + suffix);

        drawString = drawStr;
        if(!drawString)
            setMessage("");
    }

    public AdjustSlider(int xPos, int yPos, String displayStr, double minVal, double maxVal, double currentVal, IPressable handler, ISlider par)
    {
        this(xPos, yPos, 150, displayStr, "", minVal, maxVal, currentVal, true, true, handler, par);
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    @Override
    public int getYImage(boolean par1)
    {
        return 0;
    }

    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        Minecraft    minecraft    = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
//        minecraft.getTextureManager().bindTexture(IMG_LOCATION);
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
//        int i = this.getYImage(this.isHovered());
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        this.blit(this.x, this.y+8, 96, 224 + i * 8, this.width, 8);
        GuiUtils.drawContinuousTexturedBox(IMG_LOCATION, this.x , this.y+8, 96, 224, this.width, 8, 80, 8, 0, 0, 0, 0, this.getBlitOffset());
        this.renderBg(minecraft, mouseX, mouseY);
        int j = getFGColor();
        this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y , j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    @Override
    protected void renderBg(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
                this.sliderValue = (par2 - (this.x + 4)) / (float)(this.width - 8);
                updateSlider();
            }

            GuiUtils.drawContinuousTexturedBox(IMG_LOCATION, this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y+8, 176, 224, 8, 8, 8, 8, 2, 3, 2, 2, this.getBlitOffset());
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.sliderValue = (mouseX - (this.x + 4)) / (this.width - 8);
        updateSlider();
        this.dragging = true;
    }

    public void updateSlider()
    {
        if (this.sliderValue < 0.0F)
        {
            this.sliderValue = 0.0F;
        }

        if (this.sliderValue > 1.0F)
        {
            this.sliderValue = 1.0F;
        }

        String val;

        if (showDecimal)
        {
            val = String.format("%.3f",sliderValue * (maxValue - minValue) + minValue);
        }
        else
        {
            val = Integer.toString((int)Math.round(sliderValue * (maxValue - minValue) + minValue));
        }

        if(drawString)
        {
            setMessage(dispString + val + suffix);
        }

        if (parent != null)
        {
            parent.onChangeSliderValue(this);
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    @Override
    public void onRelease(double mouseX, double mouseY)
    {
        this.dragging = false;
    }

    public int getValueInt()
    {
        return (int)Math.round(sliderValue * (maxValue - minValue) + minValue);
    }

    public double getValue()
    {
        return sliderValue * (maxValue - minValue) + minValue;
    }

    public void setValue(double d)
    {
        this.sliderValue = (d - minValue) / (maxValue - minValue);
    }

    public static interface ISlider
    {
        void onChangeSliderValue(AdjustSlider slider);
    }
}

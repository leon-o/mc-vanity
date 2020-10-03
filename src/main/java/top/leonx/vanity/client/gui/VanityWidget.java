package top.leonx.vanity.client.gui;

import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import top.leonx.vanity.VanityMod;

public abstract class VanityWidget extends LayoutElement implements IGuiEventListener {
    public static final ResourceLocation WIDGET_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/vanity_widget.png");

    public VanityWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void playDownSound(SoundHandler handler) {
        handler.play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}

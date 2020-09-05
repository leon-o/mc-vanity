package top.leonx.vanity.client.gui.vanity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.client.screen.VanityMirrorScreen;
import top.leonx.vanity.hair.IHasIcon;
import top.leonx.vanity.bodypart.BodyPartGroup;

@OnlyIn(Dist.CLIENT)
public class VanityTabToggleWidget extends ToggleWidget {
    private final BodyPartGroup group;
    private       float         animationTime;

    public VanityTabToggleWidget(BodyPartGroup group) {
        super(0, 0, 35, 27, false);
        this.group = group;
        this.initTextureValues(0, 224, 48, 0, VanityMirrorScreen.VANITY_TEX);
    }

    public void startAnimation(Minecraft mc) {
//        ClientRecipeBook clientrecipebook = mc.player.getRecipeBook();
//        List<RecipeList> list             = clientrecipebook.getRecipes(this.category);
//        if (mc.player.openContainer instanceof RecipeBookContainer) {
//            label25:
//            for(RecipeList recipelist : list) {
//                Iterator iterator = recipelist.getRecipes(clientrecipebook.isFilteringCraftable((RecipeBookContainer)mc.player.openContainer)).iterator();
//
//                while(true) {
//                    if (!iterator.hasNext()) {
//                        continue label25;
//                    }
//
//                    IRecipe<?> irecipe = (IRecipe)iterator.next();
//                    if (clientrecipebook.isNew(irecipe)) {
//                        break;
//                    }
//                }
//
//                this.animationTime = 15.0F;
//                return;
//            }
//
//        }
    }

    public void renderButton(int x, int y, float partialTicks) {
        if (this.animationTime > 0.0F) {
            float f = 1.0F + 0.1F * (float)Math.sin(this.animationTime / 15.0F * (float)Math.PI);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
            RenderSystem.scalef(1.0F, f, 1.0F);
            RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
        }

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

        int k = this.x;
        if (this.stateTriggered) {
            k -= 2;
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(k, this.y, i, j, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcon(minecraft.getItemRenderer());
        if (this.animationTime > 0.0F) {
            RenderSystem.popMatrix();
            this.animationTime -= partialTicks;
        }

    }

    private void renderIcon(ItemRenderer itemRenderer) {
        IHasIcon icon = this.group.getIcon();
        int  i    = this.stateTriggered ? -2 : 0;
            Minecraft.getInstance().getTextureManager().bindTexture(icon.getIconLocation());
            blit(this.x + 5 + i,this.y + 1,0,0,24,24,24,24);

    }

    public BodyPartGroup getGroup() {
        return group;
    }
}

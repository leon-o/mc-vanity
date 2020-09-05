package top.leonx.vanity.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.gui.vanity.*;
import top.leonx.vanity.container.VanityMirrorContainer;
import top.leonx.vanity.util.ColorUtil;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartStack;

import java.util.*;

public class VanityMirrorScreen extends ContainerScreen<VanityMirrorContainer> {

    public static final ResourceLocation            VANITY_TEX         = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/vanity.png");
    private final       WrapVanityItemPage          wrapVanityItemPage = new WrapVanityItemPage(this);
    private final       ColorSelectPage             colorSelectPage;
    private final       ExtendedButton              confirmButton;
    public              List<VanityTabToggleWidget> vanityTabs         = new ArrayList<>();
    public              int                         panelLeft;
    public  int                   panelTop;
    public  int                   currentColor;
    private VanityTabToggleWidget currentTab;
    public VanityMirrorScreen(VanityMirrorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        confirmButton = new ExtendedButton(0, height / 2, 40, 20, "APPLY", this::onConfirmBtnPress);
        colorSelectPage = new ColorSelectPage(this::onColorSelected);
    }

    @Override
    protected void init() {
        super.init();
        int halfW = width / 2;
        int halfH = height / 2;
        panelLeft = halfW + 24;
        panelTop = halfH - 112;
        vanityTabs.clear();
        BodyPartGroup.GROUPS.values().forEach(t -> vanityTabs.add(new VanityTabToggleWidget(t)));
        vanityTabs.forEach(t -> t.setStateTriggered(false));
        currentTab = vanityTabs.get(0);
        currentTab.setStateTriggered(true);
        wrapVanityItemPage.init(minecraft, panelLeft + 8, panelTop + 8);
        colorSelectPage.init(panelLeft + 8, panelTop + 197, 176, 16);

        //slider=new AdjustSlider(panelLeft+7,panelTop+100,96,"a","b",0,100,50,false,true,null);

        this.addButton(confirmButton);

        updatePages();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialsTick) {
        this.renderBackground();
        drawGuiContainerBackgroundLayer(partialsTick,mouseX,mouseY);
//        InventoryScreen.drawEntityOnScreen(100, height - 50, 80, 100 - mouseX, 60 - mouseY, minecraft.player);
        drawEntityOnScreen(100, height - 50, 80, 0, 0, minecraft.player);
        for (VanityTabToggleWidget vanityTabToggleWidget : this.vanityTabs) {
            vanityTabToggleWidget.render(mouseX, mouseY, partialsTick);
        }
        this.wrapVanityItemPage.render(panelLeft, panelTop, mouseX, mouseY, partialsTick);
        colorSelectPage.render(mouseX, mouseY, partialsTick);
        for (net.minecraft.client.gui.widget.Widget button : this.buttons) {
            button.render(mouseX, mouseY, partialsTick);
        }

    }
    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity livingEntity) {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float f1 = (float)Math.atan((double)(mouseY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale((float)scale, (float)scale, (float)scale);
        Quaternion quaternion  = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);
        float f2 = livingEntity.renderYawOffset;
        float f3 = livingEntity.rotationYaw;
        float f4 = livingEntity.rotationPitch;
        float f5 = livingEntity.prevRotationYawHead;
        float f6 = livingEntity.rotationYawHead;
        livingEntity.renderYawOffset = 180.0F + f * 20.0F;
        livingEntity.rotationYaw = 180.0F + f * 40.0F;
        livingEntity.rotationPitch = -f1 * 20.0F;
        livingEntity.rotationYawHead = livingEntity.rotationYaw;
        livingEntity.prevRotationYawHead = livingEntity.rotationYaw;
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        entityrenderermanager.renderEntityStatic(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 0xf00000);
        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);
        livingEntity.renderYawOffset = f2;
        livingEntity.rotationYaw = f3;
        livingEntity.rotationPitch = f4;
        livingEntity.prevRotationYawHead = f5;
        livingEntity.rotationYawHead = f6;
        RenderSystem.popMatrix();
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getInstance().getTextureManager().bindTexture(VANITY_TEX);

        blit(panelLeft, panelTop, 0, 0, 192, 224, 256, 256);


        this.updateTabs();
    }

    private void updateTabs() {
        int i = panelLeft /*this.xOffset*/ - 29;
        int j = panelTop + 3;
        int k = 27;
        int l = 0;

        for (VanityTabToggleWidget vanityTabToggleWidget : this.vanityTabs) {
//            RecipeBookCategories recipebookcategories = vanityTabToggleWidget.func_201503_d();
//            if (recipebookcategories != RecipeBookCategories.SEARCH && recipebookcategories != RecipeBookCategories.FURNACE_SEARCH) {
//                if (vanityTabToggleWidget.func_199500_a(this.recipeBook)) {
//                    vanityTabToggleWidget.setPosition(i, j + 27 * l++);
//                    vanityTabToggleWidget.startAnimation(this.mc);
//                }
//            } else {
            vanityTabToggleWidget.visible = true;
            vanityTabToggleWidget.setPosition(i, j + 27 * l++);
//            }
        }

    }

    private void updatePages() {
        Queue<BodyPartStack> selectedItems = container.selectedVanityItems.computeIfAbsent(currentTab.getGroup(), t -> new LinkedList<>());
        currentColor=selectedItems.size()==0? ColorUtil.COLORS.get(0):selectedItems.peek().getColor();

        wrapVanityItemPage.updateLists(currentTab.getGroup(), selectedItems, true);
        colorSelectPage.updateCurrentColor(currentColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int id) {
        for (VanityTabToggleWidget vanityTabToggleWidget : this.vanityTabs) {
            if (vanityTabToggleWidget.mouseClicked(mouseX, mouseY, id)) {
                if (this.currentTab != vanityTabToggleWidget) {
                    this.currentTab.setStateTriggered(false);
                    this.currentTab = vanityTabToggleWidget;
                    this.currentTab.setStateTriggered(true);
                    //this.updateCollections(true);
                    updatePages();
                }
            }
        }
        if (wrapVanityItemPage.onClick(mouseX, mouseY, id, 0, 0, 0, 0)) {
            container.applyToClientPlayer();
        }

        colorSelectPage.mouseClicked(mouseX, mouseY, id);
        return super.mouseClicked(mouseX, mouseY, id);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int id) {
        return wrapVanityItemPage.mouseReleased(mouseX, mouseY, id);
    }

    private void onColorSelected(Integer color) {
        currentColor = color;
        container.selectedVanityItems.get(currentTab.getGroup()).forEach(t -> t.setColor(color));
    }

    private void onConfirmBtnPress(Button button) {
        container.updateToServer();
    }

}

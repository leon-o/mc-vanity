package top.leonx.vanity.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractHairItem extends Item implements IDyeableArmorItem {
    public AbstractHairItem(Properties properties) {
        super(properties);
    }
    public abstract AbstractHairModel getHairModel();
    public abstract RenderType getRenderType();
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, LazyOptional.of(HairCurio::new));
            }
        };
    }

    public int getColor(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getChildTag("display");
        return compoundnbt != null && compoundnbt.contains("color", 99) ? compoundnbt.getInt("color") : 0xe1bea6;//0xF9E8E8;
    }
    public class HairCurio implements ICurio
    {
        @Override
        public boolean canRightClickEquip() {
            return true;
        }

        @Override
        public boolean hasRender(String identifier, LivingEntity livingEntity) {
            return true;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void render(String identifier, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getRenderManager().getRenderer(livingEntity);
            if (render instanceof LivingRenderer) {
                LivingRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer = (LivingRenderer) render;
                EntityModel<LivingEntity>                               model          = livingRenderer.getEntityModel();
                if (model instanceof BipedModel) {
                    BipedModel        bipedModel = (BipedModel) model;
                    AbstractHairModel hair       = getHairModel();

                    bipedModel.getModelHead().translateRotate(matrixStack);
                    IVertexBuilder buffer=renderTypeBuffer.getBuffer(getRenderType());
                    hair.applyPhysic(livingEntity, partialTicks);
                    LazyOptional<ItemStack> map = CuriosAPI.getCuriosHandler(livingEntity).map(handler -> {
                        CurioStackHandler stacks = handler.getCurioMap().get(identifier);
                        for(int i=0;i<stacks.getSlots();i++)
                        {
                            ItemStack itemStack = stacks.getStackInSlot(i);
                            if(itemStack.getItem().equals(AbstractHairItem.this))
                                return itemStack;
                        }
                        return new ItemStack(AbstractHairItem.this);
                    });
                    int color=0xFFFFFF;
                    if(map.isPresent())
                    {
                        color=getColor(map.orElseThrow(NullPointerException::new));
                    }
                    float r=(color>>>16) / 255f;
                    float g=((color & 0x00FF00)>>8) / 255f;
                    float b=(color & 0x0000FF) / 255f;
                    hair.render(matrixStack, buffer, light, OverlayTexture.NO_OVERLAY, r, g, b, 1f);
                }
            }
        }
    }
}

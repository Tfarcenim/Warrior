package tfar.warrior.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import tfar.warrior.Warrior;
import tfar.warrior.WarriorEntity;

public class WarriorOuterLayer extends RenderLayer<WarriorEntity,WarriorModel> {
    private static final ResourceLocation HUSK_LOCATION = new ResourceLocation(Warrior.MODID,"textures/entity/warrior/warrior.png");
        private final WarriorModel model;

        public WarriorOuterLayer(RenderLayerParent<WarriorEntity,WarriorModel> pRenderer, EntityModelSet p_174491_) {
            super(pRenderer);
            this.model = new WarriorModel(p_174491_.bakeLayer(ModelLayers.DROWNED_OUTER_LAYER));
        }

        public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, WarriorEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, HUSK_LOCATION, pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch, pPartialTicks, 1.0F, 1.0F, 1.0F);
        }
    }

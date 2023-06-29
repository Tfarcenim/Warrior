package tfar.warrior.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import tfar.warrior.Warrior;
import tfar.warrior.WarriorEntity;

public class WarriorRenderer extends HumanoidMobRenderer<WarriorEntity,WarriorModel> {

    private static final ResourceLocation WARRIOR_LOCATION = new ResourceLocation(Warrior.MODID,"textures/entity/warrior/warrior.png");


    public WarriorRenderer(EntityRendererProvider.Context p_174456_) {
        this(p_174456_, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public WarriorRenderer(EntityRendererProvider.Context pContext, ModelLayerLocation pZombieLayer, ModelLayerLocation pInnerArmor, ModelLayerLocation pOuterArmor) {
        super(pContext, new WarriorModel(pContext.bakeLayer(pZombieLayer)),.5f);
        this.addLayer(new HumanoidArmorLayer<>(this, new WarriorModel(pContext.bakeLayer(pInnerArmor)), new WarriorModel(pContext.bakeLayer(pOuterArmor))));
    }


    @Override
    public ResourceLocation getTextureLocation(WarriorEntity pEntity) {
        return WARRIOR_LOCATION;
    }
}

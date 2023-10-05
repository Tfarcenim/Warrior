package tfar.warrior.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.warrior.Warrior;
import tfar.warrior.WarriorEntity;

public class BiomeTagProvider extends BiomeTagsProvider {
    public BiomeTagProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, Warrior.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(WarriorEntity.BIOMES).addTag(BiomeTags.IS_OVERWORLD);
    }
}

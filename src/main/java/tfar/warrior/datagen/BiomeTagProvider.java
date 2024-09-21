package tfar.warrior.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.warrior.Warrior;
import tfar.warrior.WarriorEntity;

import java.util.concurrent.CompletableFuture;

public class BiomeTagProvider extends BiomeTagsProvider {
    public BiomeTagProvider(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookup,@Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, lookup, Warrior.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(WarriorEntity.BIOMES).addTag(BiomeTags.IS_OVERWORLD);
    }
}

package tfar.warrior;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

public class ModDatagen {

    public static void gather(GatherDataEvent e) {
        ExistingFileHelper existingFileHelper = e.getExistingFileHelper();
        DataGenerator generator = e.getGenerator();

        final RegistryAccess registryAccess = RegistryAccess.builtinCopy();
        final RegistryOps<JsonElement> jsonOps = RegistryOps.create(JsonOps.INSTANCE, registryAccess);

        final Registry<Biome> biomeReg = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
        HolderSet<Biome> biomes = new HolderSet.Named<>(biomeReg, WarriorEntity.BIOMES);
        final ForgeBiomeModifiers.AddSpawnsBiomeModifier modifier = new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes, List.of(new MobSpawnSettings.SpawnerData(WarriorEntity.WARRIOR, 10, 4, 4)));

        generator.addProvider(e.includeServer(), JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, Warrior.MODID, jsonOps,
                ForgeRegistries.Keys.BIOME_MODIFIERS, Map.of(new ResourceLocation(Warrior.MODID, Warrior.MODID), modifier)));

        generator.addProvider(e.includeServer(),new BiomeTagProvider(generator,existingFileHelper));
    }
}

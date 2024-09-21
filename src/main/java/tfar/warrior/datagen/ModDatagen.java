package tfar.warrior.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class ModDatagen {

    public static void gather(GatherDataEvent e) {
        ExistingFileHelper existingFileHelper = e.getExistingFileHelper();
        DataGenerator generator = e.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookup = e.getLookupProvider();
        boolean server = e.includeServer();

        //biomeModifier(generator,existingFileHelper,server);

        generator.addProvider(server,new BiomeTagProvider(output,lookup,existingFileHelper));
        generator.addProvider(server,ModLootTableProvider.create(output));
    }
}

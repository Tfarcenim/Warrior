package tfar.warrior;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Warrior.MODID)
public class Warrior {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "warrior";


    public Warrior() {
        // Register the setup method for modloading
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(this::setup);

        if (FMLEnvironment.dist.isClient()) {
            WarriorClient.initClient(bus);
        }
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, SERVER_SPEC);
        bus.addGenericListener(EntityType.class,this::register);
        bus.addGenericListener(Item.class,this::registerItem);
        bus.addListener(this::attribute);
        bus.addListener(this::readConfig);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::spawns);
    }

    private void register(RegistryEvent.Register<EntityType<?>> e) {
        e.getRegistry().register(WarriorEntity.WARRIOR.setRegistryName("warrior"));
    }

    private void registerItem(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(WarriorEntity.WARRIOR_SPAWN_EGG.setRegistryName("warrior_spawn_egg"));
    }

    private void spawns(BiomeLoadingEvent e) {
        MobSpawnSettingsBuilder spawns = e.getSpawns();

        List<MobSpawnSettings.SpawnerData> spawnerData = spawns.getSpawner(MobCategory.MONSTER);

        for (MobSpawnSettings.SpawnerData spawnerData1 : spawnerData) {
            if (spawnerData1.type == EntityType.ZOMBIE) {
                spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(WarriorEntity.WARRIOR,10,1,4));
                break;
            }
        }
    }

    private void attribute(EntityAttributeCreationEvent e) {
        e.put(WarriorEntity.WARRIOR, Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE).build());
    }

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }

    public static class ServerConfig {
        public static ForgeConfigSpec.ConfigValue<List<String>> weapon_list;

        public static final List<String> defaults = Lists.newArrayList(
                "crossbow|1",
                "bow|1",
                "iron_sword|1"
                );

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.push("server");
            weapon_list = builder.
                    comment("weighted list of spawned weapons [item|weight]")
                    .define("weapon_list", defaults);
            builder.pop();
        }
    }

    static WeightedRandomList<WeightedEntry.Wrapper<Item>> randomList;

    private void setup(final BiomeLoadingEvent event) {
        MobSpawnSettingsBuilder mobSpawnSettingsBuilder = event.getSpawns();
        if (mobSpawnSettingsBuilder.getEntityTypes().contains(EntityType.ZOMBIE)) {
            mobSpawnSettingsBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(WarriorEntity.WARRIOR,20,4,4));
        }
    }

    private void readConfig(ModConfigEvent e) {
        if (e.getConfig().getModId().equals(MODID)) {
            List<WeightedEntry.Wrapper<Item>> wrappers = new ArrayList<>();
            for (String s :ServerConfig.weapon_list.get() ) {
                String[] strings = s.split("\\|");
                Item item = Registry.ITEM.get(new ResourceLocation(strings[0]));
                int i = Integer.parseInt(strings[1]);
                wrappers.add(WeightedEntry.wrap(item,i));
            }
            randomList = WeightedRandomList.create(wrappers);
        }
    }
}

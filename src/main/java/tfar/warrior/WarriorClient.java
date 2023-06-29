package tfar.warrior;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import tfar.warrior.client.WarriorRenderer;

public class WarriorClient {

    public static void initClient(IEventBus bus) {
        bus.addListener(WarriorClient::registerRenderer);
    }

    private static void registerRenderer(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(WarriorEntity.WARRIOR, WarriorRenderer::new);
    }
}

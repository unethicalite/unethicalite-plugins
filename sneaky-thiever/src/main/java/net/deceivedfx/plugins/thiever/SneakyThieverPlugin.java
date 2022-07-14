package net.deceivedfx.plugins.thiever;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Item;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.LoopedPlugin;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.unethicalite.api.commons.Time.sleep;

@Extension
@PluginDescriptor(
        name = "Sneaky Thiever",
        description = "Sneakily get 99 thieving",
        enabledByDefault = false
)
@Singleton
@Slf4j
public class SneakyThieverPlugin extends LoopedPlugin {

    @Inject
    private SneakyThieverConfig thieverConfig;

    private static final String THIEVE_SUCCESS = "You steal";
    private static String stall;
    private static int itemsStolen;

    @Override
    protected void startUp() {
        itemsStolen = 0;
    }

    @Override
    protected void shutDown() {
        log.info("Thanks for using" + getName());
        log.info("You stole a total of :" + itemsStolen + " items. Great job!");
    }

    @Override
    protected int loop() {
        var local = Players.getLocal();
        var stallObject = TileObjects.getNearest(tileObject -> tileObject != null && tileObject.getName().contains(thieverConfig.stall().getName())
                && tileObject.hasAction("Steal-from") && tileObject.distanceTo(local) <= 3);

        if (!Inventory.isEmpty()) {
            log.info("dropping");
            Item droppable = Inventory.getFirst(item -> item != null && item.hasAction("Drop"));
            if (droppable != null) {
                droppable.drop();
                sleep(300, 500);
            }
        }

        if (stallObject == null) {
            log.debug("stall is not available");
            return 600;
        }

        if (local.isMoving() || local.isAnimating()) {
            return 333;
        }

        if (!local.isAnimating() && Inventory.isEmpty()) {
            stallObject.interact("Steal-from");
            log.info("stealing");
            Time.sleepUntil(local::isAnimating, 600);
        }
        return 400;
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        final String msg = event.getMessage();

        if (event.getType() == ChatMessageType.SPAM && (msg.contains(THIEVE_SUCCESS))) {
            itemsStolen++;
            log.info("items: " + itemsStolen);
        }
    }

    @Provides
    SneakyThieverConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SneakyThieverConfig.class);
    }
}

package net.deceivedfx.plugins.runecraft;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.plugins.runecraft.tasks.*;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.Notifier;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.pathfinder.model.BankLocation;
import net.unethicalite.api.plugins.Script;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Extension
@PluginDescriptor(
        name = "Sneaky Runecrafter",
        description = "Sneakily make millions",
        enabledByDefault = false
)
//@PluginDependency(SneakyUtils.class)
@Slf4j
public class SneakyRunecrafterPlugin extends Script {

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SneakyRunecrafterConfig config;

    @Inject
    private Notifier notifier;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ItemManager itemManager;


    @Inject
    SneakyRunecrafterOverlay overlay;

    @Inject
    ExecutorService executorService;

    Player player;
    MenuEntry targetMenu;
    boolean run;
    int tickDelay = 0;
    boolean threadFix = true;
    boolean setTalisman = false;
    boolean clickedRuins = false;
    int clickedRuinsResetCount = 0;
    public static String status = "";
    int runesCrafted = 0;
    int xpGained = 0;
    int initialLevel = 0;
    int mudRunePrice = 0;
    Instant botTimer;
    Set<Integer> BINDING_NECKLACE = Set.of(ItemID.BINDING_NECKLACE);
    Set<Integer> STAMINA_POTIONS = Set.of(ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4);
    LocalPoint beforeLoc = new LocalPoint(0, 0);
    WorldArea AREA = new WorldArea(3300, 3464, 10, 11, 0);
    TileObject ruins;
    TileObject altar;

    private static final ScriptTask[] TASKS = new ScriptTask[]{
            new DoBank(),
            new WalkRuins(),
            new CraftRunes(),
            new WalkBank()
    };

    @Provides
    SneakyRunecrafterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SneakyRunecrafterConfig.class);
    }

   /* @Override
    protected void startUp() {
        overlayManager.add(overlay);
        setTalisman = false;
        keyManager.registerKeyListener(hotkeyListener);
        executorService = Executors.newSingleThreadExecutor();
    }*/

    @Override
    protected void shutDown() {
        runesCrafted = 0;
        overlayManager.remove(overlay);
        botTimer = null;
        run = false;
        setTalisman = false;
        targetMenu = null;
        keyManager.unregisterKeyListener(hotkeyListener);
        executorService.shutdown();
    }

    @Override
    protected int loop() {
        player = client.getLocalPlayer();

        for (ScriptTask task : TASKS) {
            if (task.validate()) {
                // Perform the task and store the sleep value
                int sleep = task.execute();
                // If this task blocks the next task, return the sleep value and the internal loop will sleep for this amount of time
                if (task.blocking()) {
                    return sleep;
                }
            }
        }

        return 1000;
    }

    @Override
    public void onStart(String... args) {
        overlayManager.add(overlay);
        setTalisman = false;
        keyManager.registerKeyListener(hotkeyListener);
        executorService = Executors.newSingleThreadExecutor();
        botTimer = Instant.now();
        initialLevel = Skills.getLevel(Skill.RUNECRAFT);


    }

    public SneakyRunecrafterState getState() {
        var local = Players.getLocal();
        if (local.isAnimating() || local.isMoving()) {
            return SneakyRunecrafterState.IDLE;
        }

        if (!Inventory.contains(5521) && !Equipment.contains(item -> item.getId() == 5521)) {
            return SneakyRunecrafterState.WITHDRAW_NECKLACE;
        }

        if (!Inventory.contains(ItemID.WATER_TALISMAN)) {
            return SneakyRunecrafterState.WITHDRAW_TALISMAN;
        }

        if (Movement.getRunEnergy() <= 40 && BankLocation.VARROCK_EAST_BANK.getArea().contains(local)) {
            return SneakyRunecrafterState.WITHDRAW_STAMINA;
        }

        if (Inventory.contains(5521)) {
            return SneakyRunecrafterState.EQUIP_NECKLACE;
        }

        if (Inventory.contains(ItemID.WATER_TALISMAN) && Inventory.contains(ItemID.WATER_RUNE)
                && Inventory.contains(ItemID.PURE_ESSENCE) && Equipment.contains(5521)) {
            return SneakyRunecrafterState.ALTAR_WALK;
        }
        return SneakyRunecrafterState.IDLE;
    }

    public long getRunesPH() {
        Duration timeSinceStart = Duration.between(botTimer, Instant.now());
        if (!timeSinceStart.isZero()) {
            return (int) ((double) runesCrafted * (double) Duration.ofHours(1).toMillis() / (double) timeSinceStart.toMillis());
        }
        return 0;
    }

    private boolean isAtMysteriousRuin() {
        TileObject mysteriousRuins = TileObjects.getNearest(obj -> obj != null && obj.getId() == 34816 && obj.distanceTo(player) <= 15);
        return mysteriousRuins != null;
    }

    private boolean isInAltarRoom() {
        TileObject earthAltar = TileObjects.getNearest(obj -> obj != null && obj.getId() == ObjectID.ALTAR_34763 && obj.distanceTo(player) <= 15);
        if (earthAltar != null) {
            clickedRuins = false;
        }
        return earthAltar != null;
    }

    public HotkeyListener hotkeyListener = new HotkeyListener(() -> config.toggleKey()) {
        @Override
        public void hotkeyPressed() {
            run = !run;
            if (!run) {
                log.info("Stopped");
            }
            if (run) {
                log.info("Started");
                initialLevel = client.getRealSkillLevel(Skill.RUNECRAFT);
                botTimer = Instant.now();
            }
        }
    };

    private void walkToAltar() {
        //var local = Players.getLocal();
        Movement.walkTo(AREA.getRandom());
        // log.info("walking to altar");
        // Time.sleepUntil(this::isAtMysteriousRuin, 4000);
    }

}

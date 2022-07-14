package net.deceivedfx.plugins.runecraft.tasks;

import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.plugins.runecraft.SneakyRunecrafterPlugin;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

@Slf4j
public class CraftRunes implements ScriptTask {

    TileObject earthAltar;
    TileObject exitPortal;

    boolean clickedRuins = false;

    private static final int EARTH_ALTAR_MAP_REGION = 10571;

    @Override
    public boolean validate() {
        return isInAltarRoom();
    }

    @Override
    public int execute() {
        earthAltar = TileObjects.getNearest(obj -> obj != null && obj.getId() == ObjectID.ALTAR_34763);
        exitPortal = TileObjects.getNearest(obj -> obj != null && obj.getId() == ObjectID.PORTAL_34751);

        if (Inventory.contains(ItemID.WATER_TALISMAN)) {
            Inventory.getFirst(ItemID.WATER_TALISMAN).useOn(earthAltar);
            SneakyRunecrafterPlugin.status = "Crafting runes";
            log.info("using talisman on altar");
            Time.sleepUntil(() -> Inventory.contains(ItemID.MUD_RUNE), 6000);
            return 2000;
        }

        if (Inventory.contains(ItemID.MUD_RUNE) && !Inventory.contains(ItemID.WATER_TALISMAN)) {
            SneakyRunecrafterPlugin.status = "Leaving altar area";
            log.info("leaving altar room");
            exitPortal.interact("Use");
            Time.sleepUntil(() -> !isInAltarRoom(), 6000);
            return 2000;
        }
        return 800;
    }

    private boolean isInAltarRoom() {
        earthAltar = TileObjects.getNearest(obj -> obj != null && obj.getId() == ObjectID.ALTAR_34763 && obj.distanceTo(Players.getLocal()) <= 20);
        if (earthAltar != null) {
            clickedRuins = false;
        }
        return earthAltar != null;
    }
}

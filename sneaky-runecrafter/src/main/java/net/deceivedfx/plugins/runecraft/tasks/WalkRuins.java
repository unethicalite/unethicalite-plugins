package net.deceivedfx.plugins.runecraft.tasks;

import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.plugins.runecraft.SneakyRunecrafterPlugin;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

@Slf4j
public class WalkRuins implements ScriptTask {

    WorldArea AREA = new WorldArea(3302, 3468, 6, 6, 0);
    boolean clickedRuins = false;

    @Override
    public boolean validate() {
        return Inventory.contains(ItemID.WATER_RUNE)
                && Inventory.contains(ItemID.WATER_TALISMAN)
                && Inventory.contains(ItemID.PURE_ESSENCE)
                && Equipment.contains(5521)
                && !isInAltarRoom();
    }

    @Override
    public int execute() {
        if (Movement.isWalking())
        {
            return 600;
        }

        TileObject mysterious_ruins = TileObjects.getNearest(tileObject -> tileObject.getName().contains("Mysterious ruins") && tileObject.hasAction("Enter"));
        if (!Bank.isOpen() && !isAtMysteriousRuin())
        {
            SneakyRunecrafterPlugin.status = "Walking to ruins";
            Movement.walkTo(AREA);
            return 800;
        }

       if (mysterious_ruins != null) {
           mysterious_ruins.interact("Enter");
           log.info("click ruins");
           Time.sleepUntil(this::isInAltarRoom, 8000);
       }
        return 1000;
    }

    private boolean isAtMysteriousRuin() {
        TileObject mysteriousRuins = TileObjects.getNearest(obj -> obj.getId() == 34816 && obj.distanceTo(Players.getLocal()) <= 5);
        return mysteriousRuins != null;
    }

    private boolean isInAltarRoom() {
        TileObject earthAltar = TileObjects.getNearest(obj -> obj.getId() == ObjectID.ALTAR_34763);
        return earthAltar != null;
    }
}

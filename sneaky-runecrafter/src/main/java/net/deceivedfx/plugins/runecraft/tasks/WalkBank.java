package net.deceivedfx.plugins.runecraft.tasks;

import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.plugins.runecraft.SneakyRunecrafterPlugin;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.pathfinder.model.BankLocation;

@Slf4j
public class WalkBank implements ScriptTask {

    TileObject earthAltar;
    boolean clickedRuins = false;
    private static final WorldPoint bankTile = new WorldPoint(3254, 3420, 0);

    @Override
    public boolean validate() {
        return !isInAltarRoom() && Inventory.contains(ItemID.MUD_RUNE) && !BankLocation.VARROCK_EAST_BANK.getArea().contains(Players.getLocal());
    }

    @Override
    public int execute() {
        if (Movement.isWalking())
        {
            return 600;
        }

      if (!BankLocation.VARROCK_EAST_BANK.getArea().contains(Players.getLocal())) {
          SneakyRunecrafterPlugin.status = "Walking to bank";
          Movement.walkTo(BankLocation.VARROCK_EAST_BANK);
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

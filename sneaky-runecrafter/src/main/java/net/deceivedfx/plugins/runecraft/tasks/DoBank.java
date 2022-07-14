package net.deceivedfx.plugins.runecraft.tasks;

import lombok.extern.slf4j.Slf4j;
import net.deceivedfx.plugins.runecraft.SneakyRunecrafterPlugin;
import net.deceivedfx.utils.util.SneakyBankUtils;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.movement.pathfinder.model.BankLocation;

import javax.inject.Inject;

@Slf4j
public class DoBank implements ScriptTask {
    @Inject
    private SneakyRunecrafterPlugin runecrafterPlugin;
    @Inject
    private SneakyBankUtils sneakyBankUtils;

    TileObject bankBooth;

    @Override
    public boolean validate() {
        return BankLocation.VARROCK_EAST_BANK.getArea().contains(Players.getLocal()) && !canRunecraft();
    }

    @Override
    public int execute() {
        bankBooth = TileObjects.getNearest(tileObject -> tileObject.getName().contains("Bank booth"));
        var local = Players.getLocal();

        switch (getProcess()) {
            case OPEN:
                openBank();
                break;
            case DEPOSIT:
                Bank.depositAll("Mud rune");
                break;
            case WITHDRAW_NECKLACE:
                withdrawNecklace();
                break;
            case EQUIP_NECKLACE:
                equipNecklace();
                break;
            case WITHDRAW_RUNES:
                withdrawRunes();
                break;
            case WITHDRAW_TALISMAN:
                withdrawTalisman();
                break;
            case WITHDRAW_ESSENCE:
                withdrawEssence();
                Bank.close();
                break;
            /*case CLOSE:
                SneakyRunecrafterPlugin.status = "Close bank";
                log.info("close bank");
                Bank.close();*/
            case IDLE:

                break;
        }

        /*if (Bank.isOpen()) {
            if (Inventory.contains(ItemID.MUD_RUNE)) {
                log.info("deposit runes");
                Bank.depositAll(ItemID.MUD_RUNE);
                Time.sleepUntil(() ->!Inventory.contains(ItemID.MUD_RUNE), 1000);
                return 2000;
            }

            if (!Equipment.contains(ItemID.BINDING_NECKLACE)) {
                log.info("withdraw binding");
                if (Bank.contains(ItemID.BINDING_NECKLACE)) {
                    Bank.withdraw(ItemID.BINDING_NECKLACE, 1, Bank.WithdrawMode.ITEM);
                    Time.sleepUntil(() -> Inventory.contains(ItemID.BINDING_NECKLACE), 1000);
                    return 2000;
                }
            }

            if (Inventory.contains(ItemID.BINDING_NECKLACE)) {
                log.info("equipping binding");
                Inventory.getFirst(ItemID.BINDING_NECKLACE).interact("Wear");
                Time.sleepUntil(() -> Equipment.contains(ItemID.BINDING_NECKLACE), 1000);
                bankBooth.interact("Bank");
                return 3000;
            }

            if (Movement.getRunEnergy() >= 45) {
                if (!Inventory.contains(item -> item.getName().contains("Stamina"))) {
                    Bank.withdraw("Stamina potion(", 1, Bank.WithdrawMode.ITEM);
                    Time.sleepUntil(() -> Inventory.contains(item -> item.getName().contains("Stamina")), 1000);
                } else if (Inventory.contains(item -> item.getName().contains("Stamina"))) {
                    Inventory.getFirst("Stamina potion(").interact("Drink");
                }
                return 5000;
            }

            if (Varbits.STAMINA_EFFECT <= 20) {
                Bank.depositAll("Stamina potion(", "Vial");
                Time.sleepUntil(() -> !Inventory.contains("Stamina potion(") || !Inventory.contains("Vial"), 1000);
                return 2000;
            }

            if (!Inventory.contains(ItemID.WATER_RUNE)) {
                if (Bank.contains(ItemID.WATER_RUNE)) {
                    log.info("withdraw water runes");
                    Bank.withdrawAll(ItemID.WATER_RUNE, Bank.WithdrawMode.ITEM);
                    Time.sleep(444,777);
                }
                return 3000;
            }

            if (!Inventory.contains(ItemID.WATER_TALISMAN)) {
                if (Bank.contains(ItemID.WATER_TALISMAN)) {
                    log.info("withdraw talisman");
                    Bank.withdraw(ItemID.WATER_TALISMAN, 1, Bank.WithdrawMode.ITEM);
                    Time.sleepUntil(() -> Inventory.contains(ItemID.WATER_TALISMAN), 1000);
                }
                return 3000;
            }

            if (!Inventory.contains(ItemID.PURE_ESSENCE)) {
                if (Bank.contains(ItemID.PURE_ESSENCE)) {
                    log.info("withdraw essence");
                    Bank.withdrawAll(ItemID.PURE_ESSENCE, Bank.WithdrawMode.ITEM);
                    Time.sleepUntil(() -> Inventory.contains(ItemID.PURE_ESSENCE), 1000);
                }
                return 3000;
            }
        }

        if (!Bank.isOpen()) {
            if (bankBooth != null) {
                bankBooth.interact("Bank");
                log.info("clicked the bank booth");
                Time.sleepUntil(Bank::isOpen, 1000);
                return 1000;
            }
        }

        if (Inventory.contains(ItemID.WATER_RUNE)
                && Inventory.contains(ItemID.WATER_TALISMAN)
                && Inventory.contains(ItemID.PURE_ESSENCE)
                && Equipment.contains(5521)) {
            Bank.close();
            return 3000;
        }*/
        return 1000;
    }

    private enum bankProcessState {
        OPEN,
        DEPOSIT,
        WITHDRAW_NECKLACE,
        EQUIP_NECKLACE,
        WITHDRAW_STAMINA,
        DRINK_STAMINA,
        WITHDRAW_RUNES,
        WITHDRAW_TALISMAN,
        WITHDRAW_ESSENCE,
        IDLE,
        CLOSE
    }

    private bankProcessState getProcess() {
        var local = Players.getLocal();
        if (!Bank.isOpen() && !canRunecraft()) {
            return bankProcessState.OPEN;
        }

        if (Inventory.contains(ItemID.MUD_RUNE)) {
            return bankProcessState.DEPOSIT;
        }

        if (!Equipment.contains(5521) && !Inventory.contains(5521)) {
            return bankProcessState.WITHDRAW_NECKLACE;
        }

        if (!Equipment.contains(5521) && Inventory.contains(5521)) {
            return bankProcessState.EQUIP_NECKLACE;
        }

        if (!Inventory.contains(ItemID.WATER_RUNE)) {
            return bankProcessState.WITHDRAW_RUNES;
        }

        if (Inventory.contains(ItemID.WATER_RUNE) && !Inventory.contains(ItemID.WATER_TALISMAN)) {
            return bankProcessState.WITHDRAW_TALISMAN;
        }

        if (Inventory.contains(ItemID.WATER_RUNE) && Inventory.contains(ItemID.WATER_TALISMAN) && !Inventory.contains(ItemID.PURE_ESSENCE)) {
            return bankProcessState.WITHDRAW_ESSENCE;
        }

        if (Inventory.contains(ItemID.WATER_RUNE) && Inventory.contains(ItemID.WATER_TALISMAN) && Inventory.contains(ItemID.PURE_ESSENCE) && Equipment.contains(5521)) {
            return bankProcessState.CLOSE;
        }
        return bankProcessState.IDLE;
    }

    private boolean canRunecraft() {
        if (Inventory.contains(ItemID.WATER_RUNE) && Inventory.contains(ItemID.WATER_TALISMAN) && Inventory.contains(ItemID.PURE_ESSENCE)
        && Equipment.contains(5521)) {
            return true;
        }
        return false;
    }

    private void openBank() {
        if (bankBooth == null) {
            return;
        }
        if (Reachable.isInteractable(bankBooth)) {
            log.info("open bank");
            SneakyRunecrafterPlugin.status = "Opening bank";
            bankBooth.interact("Bank");
            Time.sleepUntil(Bank::isOpen, 4000);
        }
    }

    private void withdrawNecklace() {
        Item necklace = Bank.getFirst(ItemID.BINDING_NECKLACE);
        if (necklace == null) {
            log.info("no necklaces");
        }

        if (Bank.contains(ItemID.BINDING_NECKLACE)) {
            log.info("withdraw necklace");
            SneakyRunecrafterPlugin.status = "Withdraw necklace";
            Bank.withdraw(ItemID.BINDING_NECKLACE, 1, Bank.WithdrawMode.ITEM);
            Time.sleepUntil(() -> Inventory.contains(ItemID.BINDING_NECKLACE), 2000);
        }
    }

    private void equipNecklace() {
        Item necklace = Inventory.getFirst(ItemID.BINDING_NECKLACE);
        if (necklace == null) {
            log.info("no necklaces");
        }

        if (Inventory.contains(ItemID.BINDING_NECKLACE)) {
            log.info("equip necklace");
            SneakyRunecrafterPlugin.status = "Equipping necklace";
            Inventory.getFirst(ItemID.BINDING_NECKLACE).interact("Wear");
            Time.sleepUntil(() -> Equipment.contains(ItemID.BINDING_NECKLACE), 2000);
        }
    }

    private void withdrawRunes() {
        Item waterRune = Bank.getFirst(ItemID.WATER_RUNE);
        if (waterRune == null) {
            log.info("no water runes");
        }

        if (Bank.contains(ItemID.WATER_RUNE)) {
            log.info("withdraw water runes");
            SneakyRunecrafterPlugin.status = "Withdraw runes";
            Bank.withdrawAll(ItemID.WATER_RUNE, Bank.WithdrawMode.ITEM);
            Time.sleepUntil(() -> Inventory.contains(ItemID.WATER_RUNE), 2000);
        }
    }

    private void withdrawTalisman() {
        Item talisman = Bank.getFirst(ItemID.WATER_TALISMAN);
        if (talisman == null) {
            log.info("no water talisman");
        }

        if (Bank.contains(ItemID.WATER_TALISMAN)) {
            log.info("withdraw water talisman");
            SneakyRunecrafterPlugin.status = "Withdraw talisman";
            Bank.withdraw(ItemID.WATER_TALISMAN,1, Bank.WithdrawMode.ITEM);
            Time.sleepUntil(() -> Inventory.contains(ItemID.WATER_TALISMAN), 2000);
        }
    }

    private void withdrawEssence() {
        Item essence = Bank.getFirst(ItemID.PURE_ESSENCE);
        if (essence == null) {
            log.info("no essence");
        }

        if (Bank.contains(ItemID.PURE_ESSENCE)) {
            log.info("withdraw essence");
            SneakyRunecrafterPlugin.status = "Withdraw essence";
            Bank.withdrawAll(ItemID.PURE_ESSENCE, Bank.WithdrawMode.ITEM);
            Time.sleepUntil(() -> Inventory.contains(ItemID.PURE_ESSENCE), 2000);
        }
    }
}

package org.rev317.min.api.methods;

import org.parabot.core.Context;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.input.Keyboard;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.rev317.min.Loader;
import org.rev317.min.api.wrappers.Item;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.SceneObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Everel, Matt123337, JKetelaar
 * Custom code: BANKERS arrays have different values
 */
public class Bank {

    public static final int[] BANKERS = { 394, 395, 396, 397, 398, 399, 400, 766, 1479, 1480, 2114, 2116, 2117, 2119,
            2182, 2292, 2293, 2368, 2369, 2633, 2897, 2898, 3318, 3887, 3888, 4054, 4055, 5453, 5454, 5455, 5456, 6859,
            6860, 6861, 6862, 6863, 6864, 6939, 6940, 6941, 6942, 6969, 6970, 7057, 7058, 7059, 7060, 7077, 7078, 7079,
            7080, 7081, 7082, 3227, 3003, 6084, 4762, 3843 };

    public static final int[] BANKS = { 10768, 12798, 12799, 12800, 12801, 6084, 6943, 6945, 6946, 7409, 7410, 10083,
            10517, 11338, 14367, 14368, 16642, 16700, 18491, 20325, 20326, 20327, 20328, 22819, 24101, 24347, 25808,
            27254, 27260, 27263, 27265, 27267, 27292, 27718, 27719, 27720, 27721, 28429, 28430, 28431, 28432, 28433,
            28546, 28547, 28548, 28549, 2693, 4483, 10562, 14382, 14886, 16695, 16696, 19051, 21301, 26707, 28594,
            28595, 28816, 28861, 29321, 20127, 16187, 16189, 1150, 20323, 20324 };

    private static HashMap<String, Integer> settings = Context.getInstance().getServerProviderInfo().getSettings();

    /**
     * Gets nearest banker
     *
     * @return nearest banker
     */
    public static Npc getBanker() {
        return Npcs.getClosest(BANKERS);
    }

    /**
     * Gets nearest bank booths
     *
     * @return bank booths
     */
    public static SceneObject[] getNearestBanks() {
        return SceneObjects.getNearest(BANKS);
    }

    /**
     * Gets nearest bank booths
     *
     * @return bank booth
     */
    public static SceneObject getBank() {
        SceneObject[] banks = getNearestBanks();
        if (banks != null && banks[0] != null) {
            return banks[0];
        }
        return null;
    }

    /**
     * Opens bank using banker or bank booth
     *
     * @return <b>true</b> if successfully interacted
     */
    public static boolean open() {
        if (isOpen()) {
            return false;
        }

        SceneObject bank   = getBank();
        Npc         banker = getBanker();

        if (bank != null) {
            bank.interact(SceneObjects.Option.USE);
            return true;
        } else if (banker != null) {
            banker.interact(Npcs.Option.BANK);
            return true;
        }

        return false;
    }

    /**
     * Deposits all items
     */
    public static void depositAll() {
        Menu.clickButton(settings.get("button_deposit_all"));
    }

    /**
     * Withdraws items at bank based on given parameters
     *
     * @param id
     * @param amount
     */
    public static void withdraw(int id, int amount, int sleep) {
        if (!isOpen()) {
            return;
        }

        Item b = getItem(id);
        if (b == null) {
            return;
        }

        if (amount == 1) {
            b.transform(Items.Option.TRANSFORM_ONE, settings.get("item_interface_id"));
        } else if (amount == 5) {
            b.transform(Items.Option.TRANSFORM_FIVE, settings.get("item_interface_id"));
        } else if (amount == 10) {
            b.transform(Items.Option.TRANSFORM_TEN, settings.get("item_interface_id"));
        } else if (amount == 0) {
            b.transform(Items.Option.TRANSFORM_ALL, settings.get("item_interface_id"));
        } else {
            b.transform(Items.Option.TRANSFORM_X, settings.get("item_interface_id"));
            Time.sleep(1500 + sleep);
            Keyboard.getInstance().sendKeys("" + amount);
        }
    }

    /**
     * Gets bank item with given id
     *
     * @param id
     *
     * @return bank item
     */
    public static Item getItem(int id) {
        if (!isOpen()) {
            return null;
        }

        Item[] items;
        if ((items = Bank.getBankItems()) != null) {
            for (Item i : items) {
                if (i.getId() == id) {
                    return i;
                }
            }
        }

        return null;
    }

    /**
     * Counts the amount of items with given id in bank
     *
     * @param id
     *
     * @return count
     */
    public static int getCount(int id) {
        if (!isOpen()) {
            return 0;
        }
        Item item;

        return ((item = getItem(id)) != null ? item.getStackSize() : 0);
    }

    /**
     * Opens the bank
     *
     * @param bank booth
     */
    public static void open(final SceneObject bank) {
        if (isOpen()) {
            return;
        }
        if (bank.getLocation().distanceTo() > 8) {
            bank.getLocation().walkTo();
            Time.sleep(new SleepCondition() {
                @Override
                public boolean isValid() {
                    return bank.distanceTo() < 8;
                }
            }, 5000);
            return;
        }

        bank.interact(SceneObjects.Option.USE);
    }

    /**
     * Closes the bank interface
     */
    public static void close() {
        if (!isOpen()) {
            return;
        }

        Menu.sendAction(200, -1, -1, settings.get("button_close_bank"));
    }

    /**
     * Deposits all items except the given ids
     *
     * @param exceptions the item indexes that will be ignored.
     */
    public static void depositAllExcept(int... exceptions) {
        if (Bank.isOpen()) {
            final ArrayList<Integer> ignored = new ArrayList<>();
            for (int i : exceptions) {
                ignored.add(i);
            }

            for (Item i : Inventory.getItems()) {
                if (!ignored.contains(i.getId())) {
                    while (Bank.isOpen() && Inventory.getCount(i.getId()) > 0) {
                        i.transform(Items.Option.TRANSFORM_ALL, settings.get("inventory_parent_id"));
                        ignored.add(i.getId());
                        final int previous = Inventory.getCount(true);
                        Time.sleep(new SleepCondition() {
                            @Override
                            public boolean isValid() {
                                return Inventory.getCount(true) != previous;
                            }
                        }, 500);
                    }
                }
            }
        }
    }

    /**
     * Gets all bank item ids in bank
     *
     * @return bank items
     */
    public static int[] getBankItemIDs() {
        if (!isOpen()) {
            return null;
        }

        return Loader.getClient().getInterfaceCache()[settings.get("item_interface_id")].getItems();
    }

    /**
     * Gets all stack sizes in bank
     *
     * @return stack sizes
     */
    public static int[] getBankStacks() {
        if (!isOpen()) {
            return null;
        }

        return Loader.getClient().getInterfaceCache()[settings.get("item_interface_id")].getStackSizes();
    }

    /**
     * Gets all bank items in bank
     *
     * @return bank items
     */
    public static Item[] getBankItems() {
        if (!isOpen()) {
            return null;
        }

        ArrayList<Item> items  = new ArrayList<Item>();
        int[]           ids    = getBankItemIDs();
        int[]           stacks = getBankStacks();
        if (ids != null && stacks != null) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] > 0) {
                    items.add(new Item(ids[i], stacks[i], i));
                }
            }
        }

        return items.toArray(new Item[items.size()]);
    }

    /**
     * Counts total amount of items in bank
     *
     * @return total amount of items
     */
    public static int getBankCount() {
        if (!isOpen()) {
            return 0;
        }

        int[] items;
        return ((items = getBankItemIDs()) != null ? items.length : 0);
    }

    /**
     * Determines if bank is open
     *
     * @return <b>true</b> if bank is open
     */
    public static boolean isOpen() {
        return Loader.getClient().getOpenInterfaceId() == settings.get("bank_interface_id");
    }
}

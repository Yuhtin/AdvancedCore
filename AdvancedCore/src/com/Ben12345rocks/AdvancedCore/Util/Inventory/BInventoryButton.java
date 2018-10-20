/* Obtained from https://www.spigotmc.org/threads/libish-inventory-api-kinda.49339/
 */

package com.Ben12345rocks.AdvancedCore.Util.Inventory;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Ben12345rocks.AdvancedCore.Util.Inventory.BInventory.ClickEvent;
import com.Ben12345rocks.AdvancedCore.Util.Item.ItemBuilder;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PlayerUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class BInventoryButton.
 */
public abstract class BInventoryButton {

	/** The item. */
	private ItemBuilder builder;

	/** The slot. */
	private int slot = -1;

	private HashMap<String, Object> data = new HashMap<String, Object>();

	public BInventoryButton(ItemBuilder item) {
		setBuilder(item);
		slot = item.getSlot();
	}

	public BInventoryButton(ItemStack item) {
		setBuilder(new ItemBuilder(item));
	}

	/**
	 * Instantiates a new b inventory button.
	 *
	 * @param name
	 *            the name
	 * @param lore
	 *            the lore
	 * @param item
	 *            the item
	 */
	public BInventoryButton(String name, String[] lore, ItemStack item) {
		setBuilder(new ItemBuilder(item).setName(name).setLore(lore));
	}

	public BInventoryButton addData(String key, Object object) {
		getData().put(key, object);
		return this;
	}

	/**
	 * @return the builder
	 */
	public ItemBuilder getBuilder() {
		return builder;
	}

	/**
	 * @return the data
	 */
	public HashMap<String, Object> getData() {
		return data;
	}

	public Object getData(String key) {
		return data.get(key);
	}

	public Object getData(String key, Object defaultValue) {
		if (data.containsKey(key)) {
			return data.get(key);
		}
		return defaultValue;
	}

	/**
	 * Gets the item.
	 *
	 * @return the item
	 *
	 * @deprecated Use getItem(Player player)
	 */
	@Deprecated
	public ItemStack getItem() {
		return builder.toItemStack();
	}

	public ItemStack getItem(Player player) {
		return builder.toItemStack(player);
	}

	public Object getMeta(Player player, String str) {
		return PlayerUtils.getInstance().getPlayerMeta(player, str);
	}

	/**
	 * Gets the slot.
	 *
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	public void onClick(ClickEvent event, BInventory inv) {
		this.inv = inv;
		onClick(event);
	}

	private BInventory inv;

	/**
	 * @return the inv
	 */
	public BInventory getInv() {
		return inv;
	}

	/**
	 * On click.
	 *
	 * @param clickEvent
	 *            the click event
	 */
	public abstract void onClick(ClickEvent clickEvent);

	public void sendMessage(Player player, String msg) {
		player.sendMessage(StringUtils.getInstance().colorize(msg));
	}

	/**
	 * @param builder
	 *            the builder to set
	 */
	public void setBuilder(ItemBuilder builder) {
		this.builder = builder;
	}

	public void setItem(ItemBuilder builder) {
		this.builder = builder;
	}

	/**
	 * Sets the item.
	 *
	 * @param item
	 *            the new item
	 */
	public void setItem(ItemStack item) {
		builder = new ItemBuilder(item);
	}

	public void setMeta(Player player, String str, Object ob) {
		PlayerUtils.getInstance().setPlayerMeta(player, str, ob);
	}

	/**
	 * Sets the slot.
	 *
	 * @param slot
	 *            the new slot
	 */
	public void setSlot(int slot) {
		this.slot = slot;
	}

}
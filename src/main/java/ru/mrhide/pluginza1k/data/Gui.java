package ru.mrhide.pluginza1k.data;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.mrhide.pluginza1k.chats.Chat;

import java.util.Collection;
import java.util.List;


public class Gui {
    @Getter
    private Inventory gui;
    private String name;
    private List<Integer> slotsAggregate;
    private Collection<Chat> chats;
    @Getter
    private ItemStack nullItem;

    public Gui(String name, List<Integer> slotsAggregate, Collection<Chat> chats, ItemStack itemStack, int mutedListSlot, ItemStack mutedListItem) {
        this.name = name;
        this.slotsAggregate = slotsAggregate;
        this.chats = chats;
        this.nullItem = itemStack;
        this.gui = Bukkit.createInventory(null, 54, name);

        chats.forEach(chat -> {
            setSlots(chat.getItemSlot(), chat.getItem());
        });

        slotsAggregate.forEach(this::setEmptySlots);

        setSlots(mutedListSlot, mutedListItem);

    }

    private void setEmptySlots(int slot) {
        gui.setItem(slot, nullItem);
    }

    private void setSlots(int slot, ItemStack itemStack) {
        gui.setItem(slot, itemStack);
    }
}
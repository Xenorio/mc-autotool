package chneau.autotool

import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.MiningToolItem
import net.minecraft.item.SwordItem

class SelectFirst : Select {
    override fun selectTool(inventory: PlayerInventory, bState: BlockState): Int {
        var targetItem = bState.getBlock().asItem()

        for (i in 0..Select.HOTBAR_SIZE) {
            var item = inventory.main.get(i).getItem()
            if (item is MiningToolItem == false) continue
            if (item.getMiningSpeedMultiplier(ItemStack(targetItem), bState) > 1) return i
        }
        return -1
    }

    override fun selectWeapon(inventory: PlayerInventory): Int {

        for (i in 0..Select.HOTBAR_SIZE) {
            var item = inventory.main.get(i).getItem()
            if (item is SwordItem == false) return i
        }
        return -1
    }
}

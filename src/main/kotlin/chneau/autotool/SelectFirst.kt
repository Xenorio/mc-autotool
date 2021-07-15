package chneau.autotool

import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.MiningToolItem
import net.minecraft.item.SwordItem

class SelectFirst : Select {
    override fun selectTool(inventory: PlayerInventory, blockState: BlockState): Int {
        val targetItem = blockState.block.asItem()

        for (i in 0..Select.HOTBAR_SIZE) {
            val item = inventory.main.get(i).item
            if (item is MiningToolItem == false) continue
            if (item.getMiningSpeedMultiplier(ItemStack(targetItem), blockState) > 1) return i
        }
        return -1
    }

    override fun selectWeapon(inventory: PlayerInventory): Int {
        for (i in 0..Select.HOTBAR_SIZE) {
            val item = inventory.main.get(i).item
            if (item is SwordItem == false) return i
        }
        return -1
    }
}

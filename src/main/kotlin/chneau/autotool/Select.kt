package chneau.autotool

import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerInventory

interface Select {
    companion object {
        val HOTBAR_SIZE: Int = PlayerInventory.getHotbarSize()
    }

    fun selectTool(inventory: PlayerInventory, bState: BlockState): Int

    fun selectWeapon(inventory: PlayerInventory): Int
}

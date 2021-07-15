package chneau.autotool

import net.minecraft.block.BlockState
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack

class SelectBest : Select {

    override fun selectTool(inventory: PlayerInventory, bState: BlockState): Int {
        var bestSpeed = 1f
        var bestIndex = -1
        val targetItem = bState.getBlock().asItem()
        val itemStack = ItemStack(targetItem)
        for (i in 0..Select.HOTBAR_SIZE) {
            val item = inventory.main.get(i).getItem()
            val speed = item.getMiningSpeedMultiplier(itemStack, bState)
            if (bestSpeed < speed) {
                bestSpeed = speed
                bestIndex = i
            }
        }
        return bestIndex
    }

    override fun selectWeapon(inventory: PlayerInventory): Int {
        var bestDPS = 4.0
        var bestIndex = -1
        for (i in 0..Select.HOTBAR_SIZE) {
            val item = inventory.main.get(i).getItem()
            val mm = item.getAttributeModifiers(EquipmentSlot.MAINHAND)
            val atkDmgObj = mm.get(EntityAttributes.GENERIC_ATTACK_DAMAGE)
            val atkDmg = 1 + atkDmgObj.map({ x -> x.getValue() }).sum()
            val atkSpdObj = mm.get(EntityAttributes.GENERIC_ATTACK_SPEED)
            val atkSpd = 4 + atkSpdObj.map({ x -> x.getValue() }).sum()
            val dps = atkDmg * atkSpd
            if (bestDPS < dps) {
                bestDPS = dps
                bestIndex = i
            }
        }
        return bestIndex
    }
}

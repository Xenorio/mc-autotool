package chneau.autotool

import net.minecraft.block.BlockState
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack

class SelectBest : Select {

    override fun selectTool(inventory: PlayerInventory, blockState: BlockState): Int {
        var bestSpeed = 1f
        var bestIndex = -1
        val targetItem = blockState.block.asItem()
        val itemStack = ItemStack(targetItem)
        for (i in 0..Select.HOTBAR_SIZE) {
            val item = inventory.main.get(i).item
            val speed = item.getMiningSpeedMultiplier(itemStack, blockState)
            if (bestSpeed >= speed) continue
            bestSpeed = speed
            bestIndex = i
        }
        return bestIndex
    }

    override fun selectWeapon(inventory: PlayerInventory): Int {
        var bestDPS = 4.0
        var bestIndex = -1
        for (i in 0..Select.HOTBAR_SIZE) {
            val item = inventory.main.get(i).item
            val mm = item.getAttributeModifiers(EquipmentSlot.MAINHAND)
            val atkDmgObj = mm.get(EntityAttributes.GENERIC_ATTACK_DAMAGE)
            val atkDmg = 1 + atkDmgObj.map({ x -> x.value }).sum()
            val atkSpdObj = mm.get(EntityAttributes.GENERIC_ATTACK_SPEED)
            val atkSpd = 4 + atkSpdObj.map({ x -> x.value }).sum()
            val dps = atkDmg * atkSpd
            if (bestDPS >= dps) continue
            bestDPS = dps
            bestIndex = i
        }
        return bestIndex
    }
}

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
        var targetItem = bState.getBlock().asItem()
        var itemStack = ItemStack(targetItem)
        for (i in 0..Select.HOTBAR_SIZE) {
            var item = inventory.main.get(i).getItem()
            var speed = item.getMiningSpeedMultiplier(itemStack, bState)
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
            var item = inventory.main.get(i).getItem()
            var mm = item.getAttributeModifiers(EquipmentSlot.MAINHAND)
            var atkDmg =
                    1 +
                            mm.get(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                                    .map({ x -> x.getValue() })
                                    .sum()
            var atkSpd =
                    4 +
                            mm.get(EntityAttributes.GENERIC_ATTACK_SPEED)
                                    .map({ x -> x.getValue() })
                                    .sum()
            var dps = atkDmg * atkSpd
            if (bestDPS < dps) {
                bestDPS = dps
                bestIndex = i
            }
        }
        return bestIndex
    }
}

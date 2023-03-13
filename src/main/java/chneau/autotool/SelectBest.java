package chneau.autotool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class SelectBest implements Select {
    @Override
    public int selectTool(PlayerInventory inventory, BlockState blockState) {
        var bestSpeed = 1.;
        var bestIndex = -1;
        var targetItem = blockState.getBlock().asItem();
        var itemStack = new ItemStack(targetItem);
        for (var i = 0; i < HOTBAR_SIZE; i++) {
            var item = inventory.main.get(i).getItem();
            var speed = item.getMiningSpeedMultiplier(itemStack, blockState);
            if (bestSpeed < speed) {
                bestSpeed = speed;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    @Override
    public int selectWeapon(PlayerInventory inventory) {
        var bestDPS = 4.;
        var bestIndex = -1;
        for (var i = 0; i < HOTBAR_SIZE; i++) {
            var item = inventory.main.get(i).getItem();
            var mm = item.getAttributeModifiers(EquipmentSlot.MAINHAND);
            var atkAttribute = mm.get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            var atkDmg = 1 + atkAttribute.stream().mapToDouble(EntityAttributeModifier::getValue).sum();
            var speedAttribute = mm.get(EntityAttributes.GENERIC_ATTACK_SPEED);
            var atkSpd = 4 + speedAttribute.stream().mapToDouble(EntityAttributeModifier::getValue).sum();
            var dps = atkDmg * atkSpd;
            if (bestDPS < dps) {
                bestDPS = dps;
                bestIndex = i;
            }
        }
        return bestIndex;
    }
}
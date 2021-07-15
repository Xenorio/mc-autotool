package chneau.autotool

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World

class Autoswap : UseBlockCallback {

    fun register() {
        UseBlockCallback.EVENT.register(this)
    }

    override fun interact(p: PlayerEntity, w: World, h: Hand, bhr: BlockHitResult): ActionResult {
        if (!Util.isCurrentPlayer(p)) return ActionResult.PASS
        if (h != Hand.MAIN_HAND) return ActionResult.PASS
        var itemStack = p.getInventory().main.get(p.getInventory().selectedSlot)
        var maxCount = itemStack.getMaxCount()
        var count = itemStack.getCount()
        if (count == maxCount) return ActionResult.PASS
        p.getInventory().removeStack(1, 2)
        return ActionResult.PASS
    }
}

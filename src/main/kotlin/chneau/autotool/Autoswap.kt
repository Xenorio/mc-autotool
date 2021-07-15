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

    override fun interact(
            player: PlayerEntity,
            world: World,
            hand: Hand,
            blockHitResult: BlockHitResult
    ): ActionResult {
        if (!Util.isCurrentPlayer(player)) return ActionResult.PASS
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS
        val itemStack = player.inventory.main.get(player.inventory.selectedSlot)
        val maxCount = itemStack.maxCount
        val count = itemStack.count
        if (count == maxCount) return ActionResult.PASS
        player.inventory.removeStack(1, 2)
        return ActionResult.PASS
    }
}

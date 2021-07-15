package chneau.autotool

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class Autotool(select: Select = SelectBest()) : AttackBlockCallback, AttackEntityCallback, EndTick {
    var last = -1
    val select = select

    fun register() {
        AttackBlockCallback.EVENT.register(this)
        AttackEntityCallback.EVENT.register(this)
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    override fun interact(
            player: PlayerEntity,
            world: World,
            hand: Hand,
            blockPod: BlockPos,
            direction: Direction
    ): ActionResult {
        if (!Util.isCurrentPlayer(player)) return ActionResult.PASS
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS
        if (last == -1) last = player.inventory.selectedSlot
        val bState = world.getBlockState(blockPod)
        val tool = select.selectTool(player.inventory, bState)
        if (tool == -1 || player.inventory.selectedSlot == tool) return ActionResult.PASS
        updateServer(tool)
        return ActionResult.PASS
    }

    override fun interact(
            player: PlayerEntity,
            world: World,
            hand: Hand,
            e: Entity,
            ehr: EntityHitResult?
    ): ActionResult {
        if (!Util.isCurrentPlayer(player)) return ActionResult.PASS
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS
        if (last == -1) last = player.inventory.selectedSlot
        val sword = select.selectWeapon(player.inventory)
        if (sword == -1 || player.inventory.selectedSlot == sword) return ActionResult.PASS
        last = sword
        updateServer(sword)
        return ActionResult.PASS
    }

    override fun onEndTick(client: MinecraftClient) {
        val player = client.player
        if (player == null || client.crosshairTarget == null || player.inventory == null) return
        if (!Util.isCurrentPlayer(player)) return
        updateLast(player.inventory, client.mouse.wasLeftButtonClicked())
    }

    fun updateLast(inventory: PlayerInventory, lbClicked: Boolean) {
        if (lbClicked) {
            if (last == -1) last = inventory.selectedSlot
        } else {
            if (last != -1) updateServer(last)
            last = -1
        }
    }

    fun updateServer(pos: Int) {
        val instance = MinecraftClient.getInstance()
        val player = instance.player
        if (player == null) return
        player.inventory.selectedSlot = pos
        if (player.networkHandler == null) return
        player.networkHandler.sendPacket(UpdateSelectedSlotC2SPacket(pos))
    }
}

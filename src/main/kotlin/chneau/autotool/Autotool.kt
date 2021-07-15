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

class Autotool(select: Select) : AttackBlockCallback, AttackEntityCallback, EndTick {
    var last = -1
    var select = select

    fun register() {
        AttackBlockCallback.EVENT.register(this)
        AttackEntityCallback.EVENT.register(this)
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    override fun interact(
            p: PlayerEntity,
            w: World,
            h: Hand,
            bp: BlockPos,
            d: Direction
    ): ActionResult {
        if (!Util.isCurrentPlayer(p)) return ActionResult.PASS
        if (h != Hand.MAIN_HAND) return ActionResult.PASS
        if (last == -1) last = p.getInventory().selectedSlot
        var bState = w.getBlockState(bp)
        var tool = select.selectTool(p.getInventory(), bState)
        if (tool == -1 || p.getInventory().selectedSlot == tool) return ActionResult.PASS
        updateServer(tool)
        return ActionResult.PASS
    }

    override fun interact(
            p: PlayerEntity,
            w: World,
            h: Hand,
            e: Entity,
            ehr: EntityHitResult?
    ): ActionResult {
        if (!Util.isCurrentPlayer(p)) return ActionResult.PASS
        if (h != Hand.MAIN_HAND) return ActionResult.PASS
        if (last == -1) last = p.getInventory().selectedSlot
        var sword = select.selectWeapon(p.getInventory())
        if (sword == -1 || p.getInventory().selectedSlot == sword) return ActionResult.PASS
        last = sword
        updateServer(sword)
        return ActionResult.PASS
    }

    override fun onEndTick(c: MinecraftClient) {
        var p = c.player
        if (p == null || c.crosshairTarget == null || p.getInventory() == null) return
        if (!Util.isCurrentPlayer(p)) return
        updateLast(p.getInventory(), c.mouse.wasLeftButtonClicked())
    }

    fun updateLast(i: PlayerInventory, lbClicked: Boolean) {
        if (lbClicked == false) {
            if (last != -1) updateServer(last)
            last = -1
        } else {
            if (last == -1) last = i.selectedSlot
        }
    }

    fun updateServer(pos: Int) {
        var instance = MinecraftClient.getInstance()
        var p = instance.player
        if (p == null) return
        p.getInventory().selectedSlot = pos
        if (p.networkHandler == null) return
        p.networkHandler.sendPacket(UpdateSelectedSlotC2SPacket(pos))
    }
}

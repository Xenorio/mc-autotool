package chneau.autotool

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick
import net.minecraft.client.MinecraftClient
import net.minecraft.item.SwordItem
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult.Type

class Autoattack : EndTick {
    var lastAttack = System.currentTimeMillis()

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    override fun onEndTick(client: MinecraftClient) {
        val player = client.player
        if (player == null) return
        if (!Util.isCurrentPlayer(player)) return
        val inventory = player.inventory
        if (inventory == null) return
        val target = client.crosshairTarget
        if (target == null) return
        val itemMainHand = inventory.main.get(inventory.selectedSlot).item
        if (target.type != Type.ENTITY) return
        if (itemMainHand !is SwordItem) return
        val now = System.currentTimeMillis()
        if (now - lastAttack < 625) return
        client.interactionManager?.attackEntity(player, (target as EntityHitResult).entity)
        player.resetLastAttackedTicks()
        player.swingHand(Hand.MAIN_HAND)
        lastAttack = now
    }
}

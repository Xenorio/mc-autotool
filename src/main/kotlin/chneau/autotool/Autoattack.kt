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

    override fun onEndTick(c: MinecraftClient) {
        var p = c.player
        if (p == null || c.crosshairTarget == null || p.getInventory() == null) return
        if (!Util.isCurrentPlayer(p)) return
        var itemMainHand = p.getInventory().main.get(p.getInventory().selectedSlot).getItem()
        if (c.crosshairTarget!!.getType() == Type.ENTITY) {
            if (itemMainHand !is SwordItem) return
            var now = System.currentTimeMillis()
            if (now - lastAttack < 625) return
            c.interactionManager!!.attackEntity(
                    p,
                    (c.crosshairTarget as EntityHitResult).getEntity()
            )
            p.resetLastAttackedTicks()
            p.swingHand(Hand.MAIN_HAND)
            lastAttack = now
        }
    }
}

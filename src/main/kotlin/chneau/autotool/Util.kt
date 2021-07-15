package chneau.autotool

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class Util {
    companion object {
        var wee = 0.0000001

        fun getTargetedBlock(c: MinecraftClient): BlockPos {
            val cameraPos = c.cameraEntity!!.getCameraPosVec(1f)
            var pos = c.crosshairTarget!!.getPos()
            val x = if (pos.x - cameraPos.x > 0) wee else -wee
            val y = if (pos.y - cameraPos.y > 0) wee else -wee
            val z = if (pos.z - cameraPos.z > 0) wee else -wee
            pos = pos.add(x, y, z)
            val blockPos = BlockPos(pos)
            return blockPos
        }

        fun isCurrentPlayer(other: PlayerEntity?): Boolean {
            val instance = MinecraftClient.getInstance()
            val player = instance.player
            if (player == null) return false
            if (other == null) return false
            return player.equals(other)
        }
    }
}

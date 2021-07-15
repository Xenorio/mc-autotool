package chneau.autotool

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class Util {
    companion object {
        var wee = 0.0000001 // big enough to select the block you are looking at

        fun getTargetedBlock(client: MinecraftClient): BlockPos {
            val cameraPos = client.cameraEntity!!.getCameraPosVec(1f)
            var pos = client.crosshairTarget!!.pos
            val x = if (pos.x - cameraPos.x > 0) wee else -wee
            val y = if (pos.y - cameraPos.y > 0) wee else -wee
            val z = if (pos.z - cameraPos.z > 0) wee else -wee
            pos = pos.add(x, y, z)
            val blockPos = BlockPos(pos)
            return blockPos
        }

        fun isCurrentPlayer(other: PlayerEntity?): Boolean {
            if (other == null) return false
            val instance = MinecraftClient.getInstance()
            val player = instance.player
            if (player == null) return false
            return player.equals(other)
        }
    }
}

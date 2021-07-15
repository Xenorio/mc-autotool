package chneau.autotool

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick
import net.minecraft.block.Blocks
import net.minecraft.block.CropBlock
import net.minecraft.block.NetherWartBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.MiningToolItem
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult.Type
import net.minecraft.util.math.BlockPos

class Autofarm : EndTick {

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    override fun onEndTick(c: MinecraftClient) {
        val p = c.player
        if (p == null || c.crosshairTarget == null || p.getInventory() == null) return
        if (!Util.isCurrentPlayer(p)) return
        val inventory = p.getInventory()
        val itemMainHand = inventory.main.get(inventory.selectedSlot).getItem()
        if (c.crosshairTarget!!.getType() == Type.BLOCK) {
            val isSeed = itemMainHand is AliasedBlockItem
            val isTool = itemMainHand is MiningToolItem
            if (!(isSeed || isTool)) return
            val networkHandler = c.getNetworkHandler()
            if (networkHandler == null) return
            val blockPos = Util.getTargetedBlock(c)
            val bhr = c.crosshairTarget as BlockHitResult
            harvest(c, networkHandler, blockPos, bhr)
            harvest(c, networkHandler, blockPos.east(), bhr)
            harvest(c, networkHandler, blockPos.east().north(), bhr)
            harvest(c, networkHandler, blockPos.west(), bhr)
            harvest(c, networkHandler, blockPos.west().south(), bhr)
            harvest(c, networkHandler, blockPos.south(), bhr)
            harvest(c, networkHandler, blockPos.south().east(), bhr)
            harvest(c, networkHandler, blockPos.north(), bhr)
            harvest(c, networkHandler, blockPos.north().west(), bhr)
            if (isSeed) {
                var bp = bhr.getBlockPos()
                plant(c, networkHandler, bp, bhr)
                plant(c, networkHandler, bp.east(), bhr)
                plant(c, networkHandler, bp.east().north(), bhr)
                plant(c, networkHandler, bp.west(), bhr)
                plant(c, networkHandler, bp.west().south(), bhr)
                plant(c, networkHandler, bp.south(), bhr)
                plant(c, networkHandler, bp.south().east(), bhr)
                plant(c, networkHandler, bp.north(), bhr)
                plant(c, networkHandler, bp.north().west(), bhr)
                bp = bp.down()
                plant(c, networkHandler, bp, bhr)
                plant(c, networkHandler, bp.east(), bhr)
                plant(c, networkHandler, bp.east().north(), bhr)
                plant(c, networkHandler, bp.west(), bhr)
                plant(c, networkHandler, bp.west().south(), bhr)
                plant(c, networkHandler, bp.south(), bhr)
                plant(c, networkHandler, bp.south().east(), bhr)
                plant(c, networkHandler, bp.north(), bhr)
                plant(c, networkHandler, bp.north().west(), bhr)
            }
        }
    }

    fun plant(
            c: MinecraftClient,
            networkHandler: ClientPlayNetworkHandler,
            blockPos: BlockPos,
            bhr: BlockHitResult
    ) {
        val above = c.world!!.getBlockState(blockPos.up()).getBlock()
        if (!(above.equals(Blocks.AIR) || checkBlockIsHarvestable(c, blockPos.up()))) return
        val block = c.world!!.getBlockState(blockPos).getBlock()
        if (!(block.equals(Blocks.FARMLAND) || block.equals(Blocks.SOUL_SAND))) return
        networkHandler.sendPacket(
                PlayerInteractBlockC2SPacket(
                        Hand.MAIN_HAND,
                        BlockHitResult(bhr.getPos(), bhr.getSide(), blockPos, bhr.isInsideBlock())
                )
        )
    }

    fun harvest(
            c: MinecraftClient,
            networkHandler: ClientPlayNetworkHandler,
            blockPos: BlockPos,
            bhr: BlockHitResult
    ) {
        if (!checkBlockIsHarvestable(c, blockPos)) return
        networkHandler.sendPacket(
                PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                        blockPos,
                        bhr.getSide()
                )
        )
    }

    fun checkBlockIsHarvestable(c: MinecraftClient, blockPos: BlockPos): Boolean {
        val state = c.world!!.getBlockState(blockPos)
        val block = state.getBlock()
        val maxAge: Int
        val age: Int
        if (block is NetherWartBlock) {
            maxAge = 3
            age = state.get(NetherWartBlock.AGE)
        } else if (block is CropBlock) {
            maxAge = block.getMaxAge()
            age = state.get(block.getAgeProperty())
        } else return false
        if (age != maxAge) return false
        return true
    }
}

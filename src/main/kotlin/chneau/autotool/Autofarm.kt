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

    override fun onEndTick(client: MinecraftClient) {
        val player = client.player
        if (player == null) return
        if (client.crosshairTarget == null || player.inventory == null) return
        if (!Util.isCurrentPlayer(player)) return
        val inventory = player.inventory
        val itemMainHand = inventory.main.get(inventory.selectedSlot).item
        if (client.crosshairTarget?.type == Type.BLOCK) {
            val isSeed = itemMainHand is AliasedBlockItem
            val isTool = itemMainHand is MiningToolItem
            if (!(isSeed || isTool)) return
            val networkHandler = client.getNetworkHandler()
            if (networkHandler == null) return
            val blockPos = Util.getTargetedBlock(client)
            val bhr = client.crosshairTarget as BlockHitResult
            harvest(client, networkHandler, blockPos, bhr)
            harvest(client, networkHandler, blockPos.east(), bhr)
            harvest(client, networkHandler, blockPos.east().north(), bhr)
            harvest(client, networkHandler, blockPos.west(), bhr)
            harvest(client, networkHandler, blockPos.west().south(), bhr)
            harvest(client, networkHandler, blockPos.south(), bhr)
            harvest(client, networkHandler, blockPos.south().east(), bhr)
            harvest(client, networkHandler, blockPos.north(), bhr)
            harvest(client, networkHandler, blockPos.north().west(), bhr)
            if (isSeed) {
                var bp = bhr.blockPos
                plant(client, networkHandler, bp, bhr)
                plant(client, networkHandler, bp.east(), bhr)
                plant(client, networkHandler, bp.east().north(), bhr)
                plant(client, networkHandler, bp.west(), bhr)
                plant(client, networkHandler, bp.west().south(), bhr)
                plant(client, networkHandler, bp.south(), bhr)
                plant(client, networkHandler, bp.south().east(), bhr)
                plant(client, networkHandler, bp.north(), bhr)
                plant(client, networkHandler, bp.north().west(), bhr)
                bp = bp.down()
                plant(client, networkHandler, bp, bhr)
                plant(client, networkHandler, bp.east(), bhr)
                plant(client, networkHandler, bp.east().north(), bhr)
                plant(client, networkHandler, bp.west(), bhr)
                plant(client, networkHandler, bp.west().south(), bhr)
                plant(client, networkHandler, bp.south(), bhr)
                plant(client, networkHandler, bp.south().east(), bhr)
                plant(client, networkHandler, bp.north(), bhr)
                plant(client, networkHandler, bp.north().west(), bhr)
            }
        }
    }

    fun plant(
            client: MinecraftClient,
            networkHandler: ClientPlayNetworkHandler,
            blockPos: BlockPos,
            bhr: BlockHitResult
    ) {
        var world = client.world
        if (world == null) return
        val above = world.getBlockState(blockPos.up()).block
        if (!(above.equals(Blocks.AIR) || checkBlockIsHarvestable(client, blockPos.up()))) return
        val block = world.getBlockState(blockPos).block
        if (!(block.equals(Blocks.FARMLAND) || block.equals(Blocks.SOUL_SAND))) return
        val blockHitResult = BlockHitResult(bhr.pos, bhr.side, blockPos, bhr.isInsideBlock())
        networkHandler.sendPacket(PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult))
    }

    fun harvest(
            client: MinecraftClient,
            networkHandler: ClientPlayNetworkHandler,
            blockPos: BlockPos,
            bhr: BlockHitResult
    ) {
        if (!checkBlockIsHarvestable(client, blockPos)) return
        val action = PlayerActionC2SPacket.Action.START_DESTROY_BLOCK
        networkHandler.sendPacket(PlayerActionC2SPacket(action, blockPos, bhr.side))
    }

    fun checkBlockIsHarvestable(client: MinecraftClient, blockPos: BlockPos): Boolean {
        val state = client.world!!.getBlockState(blockPos)
        val block = state.block
        val maxAge: Int
        val age: Int
        if (block is NetherWartBlock) {
            maxAge = 3
            age = state.get(NetherWartBlock.AGE)
        } else if (block is CropBlock) {
            maxAge = block.maxAge
            age = state.get(block.ageProperty)
        } else return false

        return age == maxAge
    }
}

package chneau.autotool;

import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Autofarm implements ClientTickCallback {

    public void register() {
        ClientTickCallback.EVENT.register(this);
    }

    @Override
    public void tick(MinecraftClient c) {
        ClientPlayerEntity p = c.player;
        if (p == null || c.crosshairTarget == null || p.inventory == null)
            return;
        if (!Util.isCurrentPlayer(p))
            return;
        PlayerInventory inventory = p.inventory;
        Item itemMainHand = inventory.main.get(inventory.selectedSlot).getItem();
        if (c.crosshairTarget.getType() == Type.BLOCK) {
            boolean isSeed = itemMainHand instanceof AliasedBlockItem;
            boolean isTool = itemMainHand instanceof MiningToolItem;
            if (!(isSeed || isTool))
                return;
            ClientPlayNetworkHandler networkHandler = c.getNetworkHandler();
            if (networkHandler == null)
                return;
            BlockPos blockPos = Util.getTargetedBlock(c);
            BlockState state = c.world.getBlockState(blockPos);
            Block block = state.getBlock();
            BlockHitResult bhr = (BlockHitResult) c.crosshairTarget;
            if (isSeed && (block == Blocks.FARMLAND || block == Blocks.SOUL_SAND)) { // planting
                networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr));
                p.swingHand(Hand.MAIN_HAND);
                return;
            }
            int maxAge = 0;
            int age = 0;
            if (block instanceof NetherWartBlock) {
                maxAge = 3;
                age = state.get(NetherWartBlock.AGE);
            } else if (block instanceof CropBlock) {
                CropBlock cropBlock = (CropBlock) block;
                maxAge = cropBlock.getMaxAge();
                age = state.get(cropBlock.getAgeProperty());
            } else
                return;
            if (age != maxAge)
                return;
            networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                    blockPos, bhr.getSide()));
            if (isSeed) {
                networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND,
                new BlockHitResult(bhr.getPos(), Direction.UP, blockPos.down(), true)));
            }
            p.swingHand(Hand.MAIN_HAND);
        }
    }

}

package io.github.randommcsomethin.moisturization.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Callback for tilling a block (i.e. turning dirt into farmland).
 * This is hooked AFTER the block is tilled, so keep that in mind!
 *
 * <p>Upon return:
 * <ul><li>SUCCESS cancels further processing and, on the client, sends a packet to the server.
 * <li>PASS falls back to further processing.
 * <li>FAIL cancels further processing and does not send a packet to the server.</ul>
 */
public interface TillBlockCallback {
    Event<TillBlockCallback> EVENT = EventFactory.createArrayBacked(TillBlockCallback.class,
            (listeners) -> (player, world, hand, blockPos) -> {
                for (TillBlockCallback listener : listeners) {
                    ActionResult result = listener.interact(player, world, hand, blockPos);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos blockPos);
}

package com.mewo.hbmenhanced.containers;

//import com.hbm.util.fauxpointtwelve.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class labBlockContainer extends Container {
    public labBlockContainer(InventoryPlayer inventory, World world, int x, int y, int z) {
        bindPlayerInventory(inventory);
        //TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        addSlotToContainer(new SlotInput(inventory, 37, 56, 17));
        addSlotToContainer(new SlotOutput(inventory, 38, 116, 35));
    }
    private void bindPlayerInventory(InventoryPlayer playerInventory) {
        // Player inventory (first row)
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142)); // Hotbar
        }

        // Player inventory (rest of the rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        // Optional: Add logic to handle shift-clicking items
        return null;
    }
    class SlotOutput extends Slot {
        public SlotOutput(IInventory inventory, int index, int xPos, int yPos) {
            super(inventory, index, xPos, yPos);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false; // Disable placing items in the output slot
        }
    }

    // Input slot (you can add custom logic if needed)
    class SlotInput extends Slot {
        public SlotInput(IInventory inventory, int index, int xPos, int yPos) {
            super(inventory, index, xPos, yPos);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return true; // Allow placing any item in the input slot
        }
    }
}

package com.mewo.hbmenhanced.ResearchBlock;

import com.mewo.hbmenhanced.ResearchBlocks.Tier1.TileEntityT1;
import com.mewo.hbmenhanced.ResearchBlocks.Tier2.TileEntityT2;
import com.mewo.hbmenhanced.ResearchBlocks.Tier3.TileEntityT3;
import com.mewo.hbmenhanced.ResearchManager.PointManager;
import com.mewo.hbmenhanced.Util.ResearchUtil.MaterialInfo;
import com.mewo.hbmenhanced.Util.ResearchUtil.ResearchItemUtil;
import com.mewo.hbmenhanced.Util.ResearchUtil.ResearchRegistry;
import com.mewo.hbmenhanced.Util.ResearchValue;
import com.mewo.hbmenhanced.Util.Result;
import com.mewo.hbmenhanced.Util.getItemValues;
import com.mewo.hbmenhanced.hbmenhanced;
import com.mewo.hbmenhanced.items.ItemResearchPoint;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.Map;

// TODO: Research May Produce Waste Or Fail Research
// TODO: Tier 1 May Only Research Low Level Items
// TODO: Make researchers only research materials, not other items
// TODO: Make researchers give boosts to items from the previous tier
// TODO: Make research take longer, but more rewarding
// TODO: Make research repeatable, but not overpowered
// TODO: Try to keep research fun instead of a chore

public class Research {
    public void Tier1(TileEntityT1 te) {
        if (te.core == null) return;
        if (!te.core.canResearch(te)) return;

        int mainSlot = te.MAIN_SLOT;
        int outputSlot = te.OUTPUT_SLOT;
        int fuelSlot = te.FUEL_SLOT;
        ItemStack[] inventory = te.inventory;

        ItemStack input = inventory[mainSlot];
        ItemStack output = inventory[outputSlot];
        ItemStack fuel = inventory[fuelSlot];

        if (output != null) {
            // output blocked -> stop burning/researching
            te.isResearching = false;
            te.isBurning = false;
            return;
        }

        if (input == null) {
            te.isResearching = false;
            te.researchProgress = 0;
            te.maxResearchProgress = 0;
            te.isBurning = false;
            return;
        }

        // start preparing research time only when we haven't started progress yet
        if (!te.isResearching || te.researchProgress == 0 && te.maxResearchProgress == 0) {
            MaterialInfo info = ResearchRegistry.getInfo(input.getItem());
            if (info != null) {
                ResearchValue value = ResearchItemUtil.getResearchPoints(info);
                if (value != null) {
                    te.maxResearchProgress = value.getResearchTime();
                    te.researchProgress = 0;
                } else {
                    System.out.println("Value is null for input: " + input);
                    // abort start
                    te.isResearching = false;
                    te.maxResearchProgress = 0;
                    return;
                }
            } else {
                System.out.println("No MaterialInfo for input: " + input);
                te.isResearching = false;
                te.maxResearchProgress = 0;
                return;
            }
        }

        te.isResearching = true;

        // fuel handling: only set burning if burn time > 0
        if (te.currentBurnTime <= 0) te.isBurning = false;
        if (fuel != null && !te.isBurning) {
            int burn = TileEntityFurnace.getItemBurnTime(fuel);
            if (burn > 0) {
                te.isBurning = true;
                te.totalBurnTime = burn;
                te.currentBurnTime = te.totalBurnTime;
                fuel.stackSize--;
                inventory[fuelSlot] = (fuel.stackSize <= 0) ? null : fuel;
            } else {
                // not valid fuel - don't consume
            }
        }

        if (te.currentBurnTime > 0) {
            te.currentBurnTime--;
            te.researchProgress++;

            if (te.researchProgress >= te.maxResearchProgress) {
                te.researchProgress = 0;
                te.maxResearchProgress = 0;

                input = te.inventory[mainSlot];
                if (input != null) {
                    MaterialInfo info = ResearchRegistry.getInfo(input.getItem());
                    if (info != null) {
                        ResearchValue researchValue = ResearchItemUtil.getResearchPoints(info);
                        if (researchValue != null) {
                            // Consume the input
                            input.stackSize--;
                            te.inventory[mainSlot] = (input.stackSize <= 0) ? null : input;

                            // Create the research point output
                            ItemStack itemStack = new ItemStack(hbmenhanced.researchPoint, 1);
                            getItemValues.setValues(researchValue, itemStack, te.team, input);

                            te.inventory[outputSlot] = itemStack;
                        } else {
                            System.out.println("researchValue null at completion");
                        }
                    }
                }
                te.isResearching = false; // finished
                te.isBurning = (te.currentBurnTime > 0);
            }
        } else {
            te.isBurning = false;
        }
    }



    public void Tier2(TileEntityT2 te) {
        if (te.core == null) return;
        if (!te.core.canResearch(te)) return;
        int mainSlot = te.MAIN_SLOT;
        int outputSlot = te.OUTPUT_SLOT;
        ItemStack[] inventory = te.inventory;

        ItemStack input = inventory[mainSlot];
        ItemStack output = inventory[outputSlot];
        if (output != null) {
            te.isBurning = false;
            te.isResearching = false;
            return;
        }

        if (input == null) {
            te.isBurning = false;
            te.isResearching = false;
            return;
        }

        // constants for clarity
        final int fluidCost = 1000; // ml / units used to start a research cycle
        final int powerPerTick = te.core.getPowerUsage(100);

        // If not yet started, attempt to start (but don't subtract per-tick power here)
        if (!te.isResearching) {
            if (te.currentEnergy >= powerPerTick && te.tank.getFill() >= fluidCost) {
                // reserve the fluid immediately, but do NOT subtract the per-tick energy now.
                te.tank.setFill(te.tank.getFill() - fluidCost);

                MaterialInfo info = ResearchRegistry.getInfo(input.getItem());
                if (info != null) {
                    ResearchValue value = ResearchItemUtil.getResearchPoints(info);
                    if (value != null) {
                        te.maxResearchProgress = value.getResearchTime();
                        te.researchProgress = 0;
                        te.isResearching = true;
                        te.isBurning = true;
                        // do NOT subtract te.currentEnergy here; we'll subtract each tick below.
                    } else {
                        System.out.println("Value or info is null");
                    }
                } else {
                    System.out.println("Value or info is null");
                }
            } else {
                te.isBurning = false;
            }
        }

        // per-tick processing: subtract energy and increment progress
        if (te.isResearching) {
            if (te.currentEnergy >= powerPerTick) {
                te.currentEnergy -= powerPerTick;
                te.researchProgress++;
                te.isBurning = true;

                if (te.researchProgress >= te.maxResearchProgress) {
                    te.researchProgress = 0;
                    te.maxResearchProgress = 0;
                    input = te.inventory[mainSlot];
                    if (input != null) {
                        MaterialInfo info = ResearchRegistry.getInfo(input.getItem());
                        if (info != null) {
                            ResearchValue researchValue = ResearchItemUtil.getResearchPoints(info);
                            if (researchValue != null) {
                                // Consume the input
                                input.stackSize--;
                                te.inventory[mainSlot] = (input.stackSize <= 0) ? null : input;

                                // Create the research point output
                                ItemStack itemStack = new ItemStack(hbmenhanced.researchPoint, 1);
                                getItemValues.setValues(researchValue, itemStack, te.team, input);

                                te.inventory[outputSlot] = itemStack;
                            } else {
                                System.out.println("researchValue null at completion");
                            }
                        }
                    }
                    te.isResearching = false;
                    te.isBurning = false;
                }
            } else {
                // Not enough energy this tick -> pause (keep isResearching=true so it can resume)
                te.isBurning = false;
            }
        }
    }


    public void Tier3(TileEntityT3 te) {
        if (te.core == null) return;
        if (!te.core.canResearch(te)) return;

        int mainSlot = te.MAIN_SLOT;
        int outputSlot = te.OUTPUT_SLOT;

        ItemStack[] inventory = te.inventory;
        ItemStack input = inventory[mainSlot];
        ItemStack output = inventory[outputSlot];

        if (output != null) {
            // output blocked -> nothing to do
            te.isResearching = false;
            te.researchProgress = 0;
            te.maxResearchProgress = 0;
            return;
        }
        if (input == null) {
            te.isResearching = false;
            te.researchProgress = 0;
            te.maxResearchProgress = 0;
            return;
        }

        final int powerPerTick = te.core.getPowerUsage(250);

        // start research (don't subtract per-tick energy here)
        if (!te.isResearching) {
            if (te.currentEnergy >= powerPerTick) {
                MaterialInfo info = ResearchRegistry.getInfo(input.getItem());
                if (info != null) {
                    ResearchValue value = ResearchItemUtil.getResearchPoints(info);
                    if (value != null) {
                        te.maxResearchProgress = value.getResearchTime();
                        te.researchProgress = 0;
                        te.isResearching = true;
                        // do not subtract te.currentEnergy here; subtract each tick below
                    } else {
                        System.out.println("ResearchValue is null for input: " + input);
                    }
                } else {
                    System.out.println("MaterialInfo is null for input: " + input);
                }
            }
        }

        // per-tick progression: subtract energy and increment progress
        if (te.isResearching) {
            if (te.currentEnergy >= powerPerTick) {
                te.currentEnergy -= powerPerTick;
                te.researchProgress++;

                if (te.researchProgress >= te.maxResearchProgress) {
                    // finish research
                    te.researchProgress = 0;
                    te.maxResearchProgress = 0;
                    input = te.inventory[mainSlot];
                    if (input != null) {
                        MaterialInfo info = ResearchRegistry.getInfo(input.getItem());
                        if (info != null) {
                            ResearchValue researchValue = ResearchItemUtil.getResearchPoints(info);
                            if (researchValue != null) {
                                // consume input
                                input.stackSize--;
                                te.inventory[mainSlot] = (input.stackSize <= 0) ? null : input;

                                // produce output
                                ItemStack itemStack = new ItemStack(hbmenhanced.researchPoint, 1);
                                getItemValues.setValues(researchValue, itemStack, te.team, input);
                                te.inventory[outputSlot] = itemStack;
                            } else {
                                System.out.println("researchValue null at completion");
                            }
                        }
                    }
                    te.isResearching = false;
                }
            } else {
                // not enough energy this tick -> pause research (keep isResearching so it can resume)
                // do nothing else
            }
        }
    }


}
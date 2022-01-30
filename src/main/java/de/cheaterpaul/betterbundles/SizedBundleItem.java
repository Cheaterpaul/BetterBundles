package de.cheaterpaul.betterbundles;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * entirely copied from {@link net.minecraft.world.item.BundleItem} and inserted size attribute
 */
public class SizedBundleItem extends BundleItem {

    protected final int size;

    public SizedBundleItem(Properties properties, int size) {
        super(properties);
        this.size = size;
    }

    @Override
    public boolean overrideStackedOnOther(@Nonnull ItemStack stack, @Nonnull Slot slot, @Nonnull ClickAction action, @Nonnull Player player) {
        if (action != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack var5 = slot.getItem();
            if (var5.isEmpty()) {
                removeOne(stack).ifPresent((p_150740_) -> {
                    add(stack, slot.safeInsert(p_150740_), size, player);
                });
            } else if (var5.getItem().canFitInsideContainerItems()) {
                int var6 = (size - getContentWeight(stack, size)) / getWeight(var5, size);
                add(stack, slot.safeTake(var5.getCount(), var6, player), size, player);
            }

            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2, @Nonnull Slot slot, @Nonnull ClickAction action, @Nonnull Player player, @Nonnull SlotAccess slotAccess) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (stack2.isEmpty()) {
                Optional<ItemStack> var10000 = removeOne(stack1);
                Objects.requireNonNull(slotAccess);
                var10000.ifPresent(slotAccess::set);
            } else {
                stack2.shrink(add(stack1, stack2, size, player));
            }

            return true;
        } else {
            return false;
        }
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, Player player, @Nonnull InteractionHand hand) {
        ItemStack var4 = player.getItemInHand(hand);
        if (dropContents(var4, player)) {
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(var4, level.isClientSide());
        } else {
            return InteractionResultHolder.fail(var4);
        }
    }

    @Override
    public boolean isBarVisible(@Nonnull ItemStack stack) {
        return getContentWeight(stack, size) > 0;
    }

    @Override
    public int getBarWidth(@Nonnull ItemStack stack) {
        return Math.min(1 + 12 * getContentWeight(stack, size) / size, 13);
    }

    @Override
    public int getBarColor(@Nonnull ItemStack stack) {
        return super.getBarColor(stack);
    }

    @Nonnull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@Nonnull ItemStack stack) {
        NonNullList<ItemStack> var2 = NonNullList.create();
        Stream<ItemStack> var10000 = getContents(stack);
        Objects.requireNonNull(var2);
        var10000.forEach(var2::add);
        return Optional.of(new BundleTooltip(var2, getContentWeight(stack, size)));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Level level, List<Component> components, @Nonnull TooltipFlag flag) {
        components.add((new TranslatableComponent("item.minecraft.bundle.fullness", getContentWeight(stack, size), size)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onDestroyed(@Nonnull ItemEntity entity) {
        ItemUtils.onContainerDestroyed(entity, getContents(entity.getItem()));
    }



    private static int add(ItemStack bundleStack, ItemStack addStack, int size, @Nullable Player player) {
        if (!addStack.isEmpty() && addStack.getItem().canFitInsideContainerItems()) {
            CompoundTag tag = bundleStack.getOrCreateTag();
            if (!tag.contains("Items")) {
                tag.put("Items", new ListTag());
            }

            int contentWeight = getContentWeight(bundleStack, size);
            int addStackWeight = getWeight(addStack, 64);
            if (addStackWeight == 0) {
                if (player != null) {
                    player.displayClientMessage(new TranslatableComponent("text.betterbundles.stack_size_to_large"), false);
                }
                return 0;
            }
            int remainingSlots = Math.min(addStack.getCount(), (size - contentWeight) / addStackWeight);
            if (remainingSlots == 0) {
                return 0;
            } else {
                int putSize = remainingSlots;
                ListTag list = tag.getList("Items", 10);
                List<CompoundTag> var7 = getMatchingItem(addStack, list);
                for (CompoundTag itemTag : var7) {
                    if (remainingSlots <= 0) break;
                    ItemStack var9 = ItemStack.of(itemTag);
                    int freeSlots = Math.min(var9.getMaxStackSize() - var9.getCount(), remainingSlots);
                    var9.grow(freeSlots);
                    var9.save(itemTag);
                    list.remove(itemTag);
                    list.add(0, itemTag);
                    remainingSlots -= freeSlots;
                }
                if (remainingSlots > 0) {
                    ItemStack var10 = addStack.copy();
                    var10.setCount(remainingSlots);
                    CompoundTag var11 = new CompoundTag();
                    var10.save(var11);
                    list.add(0, var11);
                }

                return putSize;
            }
        } else {
            return 0;
        }
    }

    private static List<CompoundTag> getMatchingItem(ItemStack p_150757_, ListTag p_150758_) {
        if (BetterBundlesMod.BUNDLE_TAG.contains(p_150757_.getItem())) {
            return Collections.emptyList();
        } else {
            Stream<?> var10000 = p_150758_.stream();
            var10000 = var10000.filter(CompoundTag.class::isInstance);
            return var10000.map(CompoundTag.class::cast).filter((p_150755_) -> {
                return ItemStack.isSameItemSameTags(ItemStack.of(p_150755_), p_150757_);
            }).filter(tag -> {
                ItemStack stack = ItemStack.of(tag);
                return stack.getCount() < stack.getMaxStackSize();
            }).toList();
        }
    }

    private static int getWeight(ItemStack stack, int size) {
        if (BetterBundlesMod.BUNDLE_TAG.contains(stack.getItem())) {
            return 4 + getContentWeight(stack, size);
        } else {
            if ((stack.is(Items.BEEHIVE) || stack.is(Items.BEE_NEST)) && stack.hasTag()) {
                CompoundTag var1 = stack.getTagElement("BlockEntityTag");
                if (var1 != null && !var1.getList("Bees", 10).isEmpty()) {
                    return size;
                }
            }

            return (int)(size / (float)stack.getMaxStackSize());
        }
    }

    private static int getContentWeight(ItemStack bundleStack, int size) {
        return getContents(bundleStack).mapToInt((stack) -> {
            return getWeight(stack,64) * stack.getCount();
        }).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("Items")) {
            return Optional.empty();
        } else {
            ListTag tagList = tag.getList("Items", 10);
            if (tagList.isEmpty()) {
                return Optional.empty();
            } else {
                CompoundTag tag1 = tagList.getCompound(0);
                ItemStack stack1 = ItemStack.of(tag1);
                tagList.remove(0);
                if (tagList.isEmpty()) {
                    stack.removeTagKey("Items");
                }

                return Optional.of(stack1);
            }
        }
    }

    private static boolean dropContents(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("Items")) {
            return false;
        } else {
            if (player instanceof ServerPlayer) {
                ListTag tagList = tag.getList("Items", 10);

                for(int i = 0; i < tagList.size(); ++i) {
                    CompoundTag tag2 = tagList.getCompound(i);
                    ItemStack stack2 = ItemStack.of(tag2);
                    player.drop(stack2, true);
                }
            }

            stack.removeTagKey("Items");
            return true;
        }
    }

    private static Stream<ItemStack> getContents(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return Stream.empty();
        } else {
            ListTag itemList = tag.getList("Items", 10);
            Stream<?> items = itemList.stream();
            return items.map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }
}

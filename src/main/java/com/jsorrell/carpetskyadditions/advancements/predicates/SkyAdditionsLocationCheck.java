package com.jsorrell.carpetskyadditions.advancements.predicates;

import java.util.Optional;

import com.jsorrell.carpetskyadditions.advancements.criterion.SkyAdditionsLocationPredicate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;

public record SkyAdditionsLocationCheck(Optional<SkyAdditionsLocationPredicate> predicate, BlockPos offset)
                implements LootItemCondition {

        private static final MapCodec<BlockPos> OFFSET_CODEC = RecordCodecBuilder.mapCodec(
                        instance -> instance.group(
                                        ExtraCodecs.strictOptionalField(Codec.INT, "offsetX", 0).forGetter(Vec3i::getX),
                                        ExtraCodecs.strictOptionalField(Codec.INT, "offsetY", 0).forGetter(Vec3i::getY),
                                        ExtraCodecs.strictOptionalField(Codec.INT, "offsetZ", 0).forGetter(Vec3i::getZ))
                                        .apply(instance, BlockPos::new));

        public static final Codec<SkyAdditionsLocationCheck> CODEC = RecordCodecBuilder.create(
                        instance -> instance.group(
                                        ExtraCodecs.strictOptionalField(SkyAdditionsLocationPredicate.CODEC,
                                                        "predicate")
                                                        .forGetter(SkyAdditionsLocationCheck::predicate),
                                        OFFSET_CODEC.forGetter(SkyAdditionsLocationCheck::offset))
                                        .apply(instance, SkyAdditionsLocationCheck::new));

        @Override
        public LootItemConditionType getType() {
                return SkyAdditionsLootItemConditions.LOCATION_CHECK;
        }

        public boolean test(LootContext lootContext) {
                Vec3 origin = lootContext.getParamOrNull(LootContextParams.ORIGIN);
                return origin != null
                                && (predicate.isPresent() && predicate.get().matches(
                                                lootContext.getLevel(),
                                                origin.x() + offset.getX(),
                                                origin.y() + offset.getY(),
                                                origin.z() + offset.getZ()));
        }

        public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder locationPredicateBuilder) {
                return () -> new SkyAdditionsLocationCheck(
                                Optional.of(new SkyAdditionsLocationPredicate(
                                                Optional.of(locationPredicateBuilder.build()), null, null, null, null)),
                                BlockPos.ZERO);
        }

        public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder locationPredicateBuilder,
                        BlockPos offset) {
                return () -> new SkyAdditionsLocationCheck(
                                Optional.of(new SkyAdditionsLocationPredicate(
                                                Optional.of(locationPredicateBuilder.build()), null, null, null, null)),
                                offset);
        }

}

package com.github.theredbrain.bamcore.registry;

import com.github.theredbrain.bamcore.BetterAdventureModCore;
import com.github.theredbrain.bamcore.components.LongComponent;
import com.github.theredbrain.bamcore.components.NonEmptyStringMapComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public final class ComponentsRegistry implements EntityComponentInitializer {
    public static final ComponentKey<LongComponent> LAST_LOGOUT_TIME =
            ComponentRegistry.getOrCreate(BetterAdventureModCore.identifier("last_logout_time"), LongComponent.class);
    public static final ComponentKey<NonEmptyStringMapComponent> PLAYER_SPECIFIC_DIMENSION_IDS =
            ComponentRegistry.getOrCreate(BetterAdventureModCore.identifier("player_specific_dimension_ids"), NonEmptyStringMapComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(LAST_LOGOUT_TIME, e -> new LongComponent(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(PLAYER_SPECIFIC_DIMENSION_IDS, e -> new NonEmptyStringMapComponent(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}

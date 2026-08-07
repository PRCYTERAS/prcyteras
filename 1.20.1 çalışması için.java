package com.prcyteras.prcyterasmod;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = "prcyterasmod")
public class BowHandler {

    private static final ModConfig CONFIG = ModConfig.load();

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        if (!CONFIG.aimbotEnabled) return;

        // 1.20.1'de player yerine getEntity() kullanılır
        Player player = event.getEntity();
        if (player == null) return;

        // 1.20.1'de level erişimi player.level() metodudur
        Level level = player.level();

        // 1.20.1'de client/server kontrolü metottur
        if (level.isClientSide()) return;

        Predicate<LivingEntity> targetFilter = entity -> {
            if (entity == player || !entity.isAlive()) return false;
            if (CONFIG.targetMonstersOnly) {
                return entity instanceof Monster;
            }
            return true;
        };

        LivingEntity target = level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(CONFIG.scanRange),
                targetFilter)
                .stream()
                .min(Comparator.comparingDouble(e -> e.distanceTo(player)))
                .orElse(null);

        if (target == null) return;

        // ShulkerBullet oluşturma ve konumlandırma
        ShulkerBullet bullet = new ShulkerBullet(EntityType.SHULKER_BULLET, level);
        bullet.setPos(player.getX(), player.getEyeY() - 0.15D, player.getZ());
        bullet.setTarget(target);

        Vec3 look = player.getLookAngle();
        bullet.setDeltaMovement(look.scale(0.5));

        level.addFreshEntity(bullet);

        // Orijinal ok fırlatma işlemini iptal et (çift projectile gitmesini önler)
        event.setCanceled(true);
    }
}
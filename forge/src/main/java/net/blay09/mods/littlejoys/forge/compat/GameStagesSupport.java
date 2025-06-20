package net.blay09.mods.littlejoys.forge.compat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.blay09.mods.littlejoys.api.LittleJoysAPI;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class GameStagesSupport {

    public GameStagesSupport() {
        LittleJoysAPI.registerEventCondition(new ResourceLocation("gamestages", "has_stage"),
                GameStageCondition.class,
                GameStageCondition.CODEC,
                GameStageCondition::fromNetwork);
    }

    public record GameStageCondition(String stage) implements EventCondition {

        public static final MapCodec<GameStageCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("stage").forGetter(GameStageCondition::stage)
        ).apply(instance, GameStageCondition::new));

        @Override
        public boolean test(EventContext context) {
            return GameStageHelper.hasStage(context.player(), stage);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf) {
            buf.writeUtf(stage);
        }

        public static GameStageCondition fromNetwork(FriendlyByteBuf buf) {
            final var stage = buf.readUtf();
            return new GameStageCondition(stage);
        }
    }

}
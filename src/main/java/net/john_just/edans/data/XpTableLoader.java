package net.john_just.edans.data;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.john_just.edans.skill.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;

public final class XpTableLoader extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final XpTableLoader INSTANCE = new XpTableLoader();

    private XpTableLoader() {
        super(new Gson(), "xp_table");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files,
                         ResourceManager rm, ProfilerFiller pf) {

        SkillTriggerRegistry.clearAll();

        files.values().forEach(raw -> {
            JsonObject root = raw.getAsJsonObject();

            /* --- ломка --- */
            root.getAsJsonArray("break").forEach(el -> {
                JsonObject o = el.getAsJsonObject();
                var skill = PlayerSkills.SkillType.valueOf(o.get("skill").getAsString());
                float xp = o.get("xp").getAsFloat();

                if (o.has("id")) {
                    ResourceLocation id = ResourceLocation.bySeparator(o.get("id").getAsString(), ':');
                    SkillTriggerRegistry.addBreakRule(
                            BuiltInRegistries.BLOCK.get(id), skill, xp);
                } else if (o.has("tag")) {
                    ResourceLocation tagId = ResourceLocation.bySeparator(o.get("tag").getAsString(), ':');
                    SkillTriggerRegistry.addBreakRuleTag(
                            BlockTags.create(tagId), skill, xp);
                } else {
                    LOGGER.warn("[XP] Пропущена запись break без 'id' и без 'tag': {}", o);
                }
            });

            /* --- крафт --- */
            root.getAsJsonArray("craft").forEach(el -> {
                JsonObject o = el.getAsJsonObject();
                var skill = PlayerSkills.SkillType.valueOf(o.get("skill").getAsString());
                float xp = o.get("xp").getAsFloat();

                if (o.has("result_id")) {
                    ResourceLocation resultId = ResourceLocation.bySeparator(o.get("result_id").getAsString(), ':');
                    SkillTriggerRegistry.addCraftRule(
                            e -> e.getCrafting().is(BuiltInRegistries.ITEM.get(resultId)),
                            skill, xp);
                } else if (o.has("result_tag")) {
                    ResourceLocation resultTag = ResourceLocation.bySeparator(o.get("result_tag").getAsString(), ':');
                    SkillTriggerRegistry.addCraftRule(
                            e -> e.getCrafting().is(ItemTags.create(resultTag)),
                            skill, xp);
                } else {
                    LOGGER.warn("[XP] Пропущена запись craft без 'result_id' и без 'result_tag': {}", o);
                }
            });

            // mint-секция на будущее …
        });

        LOGGER.info("[XP] Loaded break: {}, craft: {}",
                SkillTriggerRegistry.BREAK_TRIGGERS.size(),
                SkillTriggerRegistry.CRAFT_TRIGGERS.size());
    }
}

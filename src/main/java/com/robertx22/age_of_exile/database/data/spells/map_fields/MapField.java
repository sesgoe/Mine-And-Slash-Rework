package com.robertx22.age_of_exile.database.data.spells.map_fields;

import com.robertx22.age_of_exile.database.data.value_calc.ValueCalculation;
import com.robertx22.library_of_exile.registry.IGUID;

import java.util.HashMap;
import java.util.List;

public class MapField<T> implements IGUID {

    String id;

    public static HashMap<String, MapField> MAP = new HashMap<>();

    // double
    public static MapField<Double> RADIUS = make("radius");
    public static MapField<Double> CHAIN_COUNT = make("chain_count");
    public static MapField<Double> COUNT = make("count");
    //public static MapField<Double> MAX_SUMMONS = make("max_summons");
    public static MapField<Double> HEIGHT = make("height");
    public static MapField<Double> TICK_RATE = make("tick_rate");
    public static MapField<Double> PARTICLE_COUNT = make("particle_count");
    public static MapField<Double> Y_RANDOM = make("y_rand");
    public static MapField<Double> LIFESPAN_TICKS = make("life_ticks");
    public static MapField<Double> DISTANCE = make("distance");
    public static MapField<Double> WIDTH = make("width");
    public static MapField<Double> CHANCE = make("chance");
    public static MapField<Double> PROJECTILE_SPEED = make("proj_speed");
    public static MapField<Double> PROJECTILES_APART = make("proj_apart");
    public static MapField<Double> SELECTION_CHANCE = make("selection_chance");
    public static MapField<Double> PUSH_STRENGTH = make("push_str");
    public static MapField<Double> POTION_DURATION = make("potion_dur");
    public static MapField<Double> COOLDOWN_TICKS = make("cooldown_ticks");
    public static MapField<Double> POTION_STRENGTH = make("potion_str");
    public static MapField<Double> EFFECT_STACKS = make("effect_stacks");
    public static MapField<Double> BLOCK_FALL_SPEED = make("block_fall_speed");
    public static MapField<Double> PROJECTILE_COUNT = make("proj_count");

    public static MapField<String> MOTION = make("motion");
    public static MapField<String> SET_ADD = make("set_add");
    public static MapField<String> COOLDOWN_ID = make("cooldown_id");

    public static MapField<Double> VOLUME = make("volume");
    public static MapField<Double> PITCH = make("pitch");
    public static MapField<Double> SECONDS = make("seconds");
    public static MapField<Double> MOTION_MULTI = make("motion_multiplier");

    public static MapField<Double> RANDOM_Y_OFFSET = make("random_y_offset");
    public static MapField<Double> RANDOM_X_OFFSET = make("random_x_offset");
    public static MapField<Double> RANDOM_Z_OFFSET = make("random_z_offset");

    // string
    public static MapField<String> PROJECTILE_ENTITY = make("proj_en");
    public static MapField<String> SUMMONED_PET_ID = make("summon_id");
    public static MapField<String> ITEM = make("item");
    public static MapField<String> ELEMENT = make(new MapField<String>("element"));
    public static MapField<String> SELECTION_TYPE = make("selection_type");
    public static MapField<String> RESOURCE_TYPE = make("resource_type");
    public static MapField<String> ENTITY_PREDICATE = make("en_predicate");
    public static MapField<String> PARTICLE_TYPE = make("particle_type");
    public static MapField<String> SOUND = make("sound");
    public static MapField<String> PARTICLE_SHAPE = make("shape");
    public static MapField<String> EXILE_POTION_ID = make("exile_potion_id");
    public static MapField<String> POTION_ID = make("potion_id");
    public static MapField<String> POTION_ACTION = make("potion_action");
    public static MapField<String> BLOCK = make("block");
    public static MapField<String> ENTITY_NAME = make("entity_name");
    public static MapField<String> PUSH_WAY = make("push_way");
    public static MapField<String> AGGRO_TYPE = make("aggro");
    public static MapField<String> SUMMON_TYPE = make("summon_type");
    public static MapField<String> COMMAND = make("command");
    public static MapField<String> POS_SOURCE = make("pos_source");
    public static MapField<String> SHOOT_DIRECTION = make("shoot_way");
    public static MapField<String> SPELL_MODIFIER = make("spell_mod");
    public static MapField<String> DMG_EFFECT_TYPE = make("dmg_effect_type");
    public static MapField<String> SPECIFIC_ACTION = make("specific_action");
    public static MapField<String> EFFECT_TAG = make("effect_tag");

    // boolean
    public static MapField<Boolean> GRAVITY = make("gravity");
    public static MapField<Boolean> EXPIRE_ON_ENTITY_HIT = make("expire_on_en_hit");
    public static MapField<Boolean> EXPIRE_ON_BLOCK_HIT = make("expire_on_block_hit");
    public static MapField<Boolean> IS_BLOCK_FALLING = make("is_falling_block");
    public static MapField<Boolean> FIND_NEAREST_SURFACE = make("find_surface");
    //public static MapField<Boolean> FIND_NEAREST_CEILING = make("find_ceiling");
    public static MapField<Boolean> IS_SILENT = make("is_silent");
    public static MapField<Boolean> HITS_ALLIES = make("hits_allies");
    public static MapField<Boolean> IGNORE_Y = make("ignore_y");
    public static MapField<Boolean> TRACKS_ENEMIES = make("tracks_enemies");
    public static MapField<Boolean> DISABLE_KNOCKBACK = make("disable_knockback");
    public static MapField<Boolean> COUNTS_TOWARDS_MAX_SUMMONS = make("counts_towards_max_summons");

    public static MapField<List<String>> ENTITY_PREDICATES = make("entity_predicates");

    public static MapField<ValueCalculation> VALUE_CALCULATION = make("value_calculation");

    public MapField(String id) {
        this.id = id;
    }

    private static <T> MapField<T> make(String f) {
        MapField<T> field = new MapField<T>(f);
        MAP.put(field.GUID(), field);
        return field;
    }

    private static <T> MapField<T> make(MapField<T> field) {
        MAP.put(field.GUID(), field);
        return field;
    }

    @Override
    public String GUID() {
        return id;
    }

    public static void init() {

    }
}

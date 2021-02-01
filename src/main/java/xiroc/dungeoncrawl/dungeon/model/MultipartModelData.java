package xiroc.dungeoncrawl.dungeon.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.piece.DungeonMultipartModelPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.WeightedRandom;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MultipartModelData {

    @Nullable
    public List<Condition<?>> conditions;

    public WeightedRandom<Instance> models;

    @Nullable
    public WeightedRandom<Instance> alternatives;

    private MultipartModelData() {
        this.conditions = null;
        this.models = null;
        this.alternatives = null;
    }

    public static MultipartModelData fromJson(JsonObject object, ResourceLocation file) {
        MultipartModelData multipartModelData = new MultipartModelData();

        if (object.has("conditions")) {
            List<Condition<?>> conditions = new ArrayList<>();
            JsonObject jsonConditions = object.getAsJsonObject("conditions");
            jsonConditions.entrySet().forEach((entry) -> {
                Condition<?> condition = Condition.fromJson(entry.getValue(), entry.getKey());
                if (condition != null) {
                    conditions.add(condition);
                }
            });
            if (!conditions.isEmpty()) {
                multipartModelData.conditions = conditions;
            } else {
                DungeonCrawl.LOGGER.warn("Multipart metadata has an empty or incorrect condition set");
            }
        }

        if (object.has("models")) {
            WeightedRandom<Instance> models = getInstancesFromJson(object.getAsJsonArray("models"), file);
            if (models == null) {
                DungeonCrawl.LOGGER.warn("Multipart metadata has an empty model list");
                return null;
            }
            multipartModelData.models = models;
        } else {
            DungeonCrawl.LOGGER.warn("Multipart metadata does not have a model list");
            return null;
        }

        if (object.has("alternatives")) {
            multipartModelData.alternatives = getInstancesFromJson(object.getAsJsonArray("alternatives"), file);
            if (multipartModelData.conditions == null) {
                DungeonCrawl.LOGGER.warn("Multipart metadata has an alternative model set but no conditions");
            }
        }

        return multipartModelData;
    }

    @Nullable
    private static WeightedRandom<Instance> getInstancesFromJson(JsonArray array, ResourceLocation file) {
        WeightedRandom.Builder<MultipartModelData.Instance> builder = new WeightedRandom.Builder<>();

        array.forEach((element) -> {
            JsonObject object1 = element.getAsJsonObject();
            MultipartModelData.Instance data = MultipartModelData.Instance.fromJson(object1, file);
            builder.add(data, JSONUtils.getWeightOrDefault(object1));
            if (data != MultipartModelData.Instance.EMPTY) {
                DungeonModels.REFERENCES_TO_UPDATE.add(data); // Enqueue reference update
            }
        });
        if (builder.entries.isEmpty()) {
            return null;
        }
        return builder.build();
    }

    public boolean checkConditions(DungeonPiece piece) {
        if (conditions != null) {
            for (Condition<?> condition : conditions) {
                if (!condition.check(piece)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static class Instance {

        public static final Instance EMPTY = new Instance(null, null, DungeonModels.NO_OFFSET, Rotation.NONE);

        public final Vec3i offset;
        public final Rotation rotation;

        private final ResourceLocation file;

        /**
         * This will be null for EMPTY.
         */
        @Nullable
        public DungeonModel model;

        private final String key;

        public Instance(ResourceLocation file, String key, Vec3i offset, Rotation rotation) {
            this.file = file;
            this.key = key;
            this.offset = offset;
            this.rotation = rotation;
        }

        public DungeonMultipartModelPiece createMultipartPiece(DungeonPiece parentPiece, DungeonModel parent, Rotation rotation, int x, int y, int z) {
            if (model != null) {
                DungeonMultipartModelPiece piece = new DungeonMultipartModelPiece(null, DungeonPiece.DEFAULT_NBT);
                Vec3i pos = Orientation.rotatedMultipartOffset(parent, model, offset, rotation);
                piece.setWorldPosition(x + pos.getX(), y + pos.getY(), z + pos.getZ());
                piece.modelKey = model.key;
                piece.rotation = this.rotation.add(rotation);
                piece.stage = parentPiece.stage;
                piece.theme = parentPiece.theme;
                piece.subTheme = parentPiece.subTheme;
                piece.setupBoundingBox();
                return piece;
            } else {
                throw new RuntimeException("Can't create a multipart piece without a model. Metadata file: " + file.toString());
            }
        }

        public void updateReference() {
            if (this == EMPTY) {
                return;
            }
            this.model = DungeonModels.MODELS.get(key);
            if (model == null) {
                throw new RuntimeException("Missing model " + key + " in " + file.toString());
            }
        }

        public static Instance fromJson(JsonObject object, ResourceLocation file) {
            if (object.has("model")) {
                Vec3i offset = object.has("offset") ? JSONUtils.getOffset(object.getAsJsonObject("offset")) : DungeonModels.NO_OFFSET;

                Rotation rotation = object.has("rotation") ? Rotation.valueOf(object.get("rotation").getAsString().toUpperCase(Locale.ROOT)) : Rotation.NONE;

                return new Instance(file, object.get("model").getAsString(), offset, rotation);
            } else {
                return EMPTY;
            }
        }

    }

    public static class Condition<T> {

        private final Property<T> property;
        private final T value;

        public Condition(Property<T> property, T value) {
            this.property = property;
            this.value = value;
        }

        @Nullable
        public static Condition<?> fromJson(JsonElement element, String name) {
            switch (name) {
                case "north":
                    return new Condition<>(Property.NORTH, element.getAsBoolean());
                case "east":
                    return new Condition<>(Property.EAST, element.getAsBoolean());
                case "south":
                    return new Condition<>(Property.SOUTH, element.getAsBoolean());
                case "west":
                    return new Condition<>(Property.WEST, element.getAsBoolean());
                case "rotated_north":
                    return new Condition<>(Property.ROTATED_NORTH, element.getAsBoolean());
                case "rotated_east":
                    return new Condition<>(Property.ROTATED_EAST, element.getAsBoolean());
                case "rotated_south":
                    return new Condition<>(Property.ROTATED_SOUTH, element.getAsBoolean());
                case "rotated_west":
                    return new Condition<>(Property.ROTATED_WEST, element.getAsBoolean());
                case "straight":
                    return new Condition<>(Property.STRAIGHT, element.getAsBoolean());
                case "connections":
                    return new Condition<>(Property.CONNECTIONS, element.getAsInt());
                case "stage":
                    return new Condition<>(Property.STAGE, element.getAsInt());
                default:
                    return null;
            }
        }

        public boolean check(DungeonPiece piece) {
            T t = property.get(piece);
            return t != null && t.equals(value);
        }

    }

    @FunctionalInterface
    public interface Property<T> {

        Property<Boolean> NORTH = (piece) -> piece.sides[0];
        Property<Boolean> EAST = (piece) -> piece.sides[1];
        Property<Boolean> SOUTH = (piece) -> piece.sides[2];
        Property<Boolean> WEST = (piece) -> piece.sides[3];

        Property<Boolean> ROTATED_NORTH = (piece) -> piece.sides[Orientation.rotationAsInt(piece.rotation)];
        Property<Boolean> ROTATED_EAST = (piece) -> piece.sides[(1 + Orientation.rotationAsInt(piece.rotation)) % 4];
        Property<Boolean> ROTATED_SOUTH = (piece) -> piece.sides[(2 + Orientation.rotationAsInt(piece.rotation)) % 4];
        Property<Boolean> ROTATED_WEST = (piece) -> piece.sides[(3 + Orientation.rotationAsInt(piece.rotation)) % 4];

        Property<Boolean> STRAIGHT = (piece) -> (piece.sides[0] && piece.sides[2]) || (piece.sides[1] && piece.sides[3]);

        Property<Integer> CONNECTIONS = (piece) -> piece.connectedSides;
        Property<Integer> STAGE = (piece) -> piece.stage;

        @Nullable
        T get(DungeonPiece piece);

    }
}
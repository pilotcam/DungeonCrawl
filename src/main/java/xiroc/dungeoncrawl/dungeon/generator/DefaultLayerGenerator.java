package xiroc.dungeoncrawl.dungeon.generator;

import com.google.common.collect.Lists;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DefaultLayerGenerator extends LayerGenerator {

    private int nodesLeft, roomsLeft;

    /**
     * Whether a secret room should be generated or not.
     */
    private boolean secretRoom;

    public DefaultLayerGenerator(DungeonGeneratorSettings settings) {
        super(settings);
    }

    @Override
    public void initializeLayer(DungeonBuilder dungeonBuilder, Random rand, int layer) {
        this.nodesLeft = settings.maxNodes.apply(rand, layer);
        this.roomsLeft = settings.maxRooms.apply(rand, layer);
        this.secretRoom = false;
    }

    @Override
    public void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start) {
        DungeonStairs s = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).bottom();
        s.setGridPosition(start.x, start.z);
        dungeonLayer.grid[s.gridX][s.gridZ] = new PlaceHolder(s).addFlag(PlaceHolder.Flag.FIXED_ROTATION);

        dungeonLayer.stairsPlaced = false;
        dungeonLayer.start = start;

        Direction[] directions = Orientation.FLAT_FACINGS;

        int maxDirections = 3 + rand.nextInt(2);
        int startDirection = rand.nextInt(4);

        for (int i = 0; i < 4; i++) {
            Direction direction = directions[(i + startDirection) % 4];
            findPositionAndContinue(dungeonLayer, start, direction, rand, 2, Math.max(2, maxDistance), layer, 1);
        }

        if (layer == 0) {
            createStarterRoom(dungeonLayer, rand, layer);
        }

        if (secretRoom) {
            List<Tuple<DungeonCorridor, Position2D>> corridors = Lists.newArrayList();
            for (int x = 0; x < dungeonLayer.width; x++) {
                for (int z = 0; z < dungeonLayer.length; z++) {
                    if (dungeonLayer.grid[x][z] != null && dungeonLayer.grid[x][z].reference.getType() == 0
                            && dungeonLayer.grid[x][z].reference.connectedSides == 2) {
                        corridors.add(new Tuple<>((DungeonCorridor) dungeonLayer.grid[x][z].reference, new Position2D(x, z)));
                    }
                }
            }
            if (!corridors.isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    Tuple<DungeonCorridor, Position2D> corridor = corridors.get(rand.nextInt(corridors.size()));
                    if (dungeonLayer.placeSecretRoom(corridor.getA(), corridor.getB(), rand)) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Recursive layer generation
     */
    private void layerGenerationStep(DungeonLayer dungeonLayer, Position2D currentPosition, Position2D lastPosition,
                                     Random rand, int layer, int depth) {
        if (depth > maxDepth || (nodesLeft == 0 && roomsLeft == 0)) {
            return;
        }

        if (depth >= minStairsDepth && !dungeonLayer.stairsPlaced && layer != 4) {
            Direction toLast = currentPosition.directionTo(lastPosition);

            dungeonLayer.end = currentPosition;

            DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
            stairs.openSide(toLast);
            stairs.setGridPosition(dungeonLayer.end.x, dungeonLayer.end.z);
            dungeonLayer.grid[stairs.gridX][stairs.gridZ] = new PlaceHolder(stairs).addFlag(PlaceHolder.Flag.FIXED_ROTATION);

            dungeonLayer.stairsPlaced = true;

            connectStraight(dungeonLayer, lastPosition, currentPosition, rand, layer);

            Direction[] directions = Orientation.getHorizontalFacingsWithout(toLast);

            int maxDirections = depth < 3 ? 1 + rand.nextInt(3) : rand.nextInt(3);
            int counter = 0;
            int start = rand.nextInt(3);

            for (int i = 0; i < 3; i++) {
                if (counter < maxDirections) {
                    Direction direction = directions[(i + start) % 3];
                    if (findPositionAndContinue(dungeonLayer, currentPosition, direction, rand, minDistance, maxDistance, layer, ++depth)) {
                        counter++;
                    }
                }
            }
            return;
        }

        if (rand.nextFloat() < 0.5 && depth <= maxNodeDepth && depth >= minNodeDepth && nodesLeft > 0) {
            Position2D center = currentPosition.shift(lastPosition.directionTo(currentPosition), 1);

            if (DungeonBuilder.canPlacePiece(dungeonLayer, center.x - 1, center.z - 1, 3, 3, false)) {
                createNodeRoom(center, dungeonLayer);
                this.nodesLeft--;

                if (depth > 1) {
                    dungeonLayer.distantNodes.add(center);
                }

                connectStraight(dungeonLayer, lastPosition, currentPosition, rand, layer);

                Direction[] directions = Orientation.getHorizontalFacingsWithout(currentPosition.directionTo(lastPosition));

                int maxDirections = depth < 5 ? 2 + rand.nextInt(2) : rand.nextInt(3);
                int start = rand.nextInt(3);

                for (int i = 0; i < maxDirections; i++) {
                    Direction direction = directions[(i + start) % 3];
                    findPositionAndContinue(dungeonLayer, center.shift(direction, 1),
                            direction, rand, minDistance, maxDistance, layer, ++depth);
                }
                return;
            }
        }

        if (depth <= maxRoomDepth && depth >= minRoomDepth && roomsLeft > 0) {
            DungeonRoom room = new DungeonRoom(null, DungeonPiece.DEFAULT_NBT);
            room.setGridPosition(currentPosition);
            dungeonLayer.grid[currentPosition.x][currentPosition.z] = new PlaceHolder(room);
            this.roomsLeft--;

            connectStraight(dungeonLayer, lastPosition, currentPosition, rand, layer);

            Direction[] directions = Orientation.getHorizontalFacingsWithout(currentPosition.directionTo(lastPosition));

            int maxDirections = depth < 5 ? 2 + rand.nextInt(2) : rand.nextInt(3);
            int start = rand.nextInt(3);

            for (int i = 0; i < maxDirections; i++) {
                Direction direction = directions[(i + start) % 3];
                findPositionAndContinue(dungeonLayer, currentPosition,
                        direction, rand, minDistance, maxDistance, layer, ++depth);
            }
        }
    }

    private boolean findPositionAndContinue(DungeonLayer dungeonLayer, Position2D origin, Direction direction, Random rand,
                                            int min, int max, int layer, int depth) {
        switch (direction) {
            case NORTH:
                if (origin.z > min) {
                    Position2D pos = origin.shift(direction, (randomDistances ? min + rand.nextInt(Math.min(max, origin.z) - min + 1) : Math.min(1 + max, origin.z)));
                    if (dungeonLayer.grid[pos.x][pos.z] == null && dungeonLayer.map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(dungeonLayer, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            case EAST:
                int east = Dungeon.SIZE - origin.x - 1;
                if (east > min) {
                    Position2D pos = origin.shift(direction, (randomDistances ? min + rand.nextInt(Math.min(max, east) - min + 1) : Math.min(1 + max, east)));
                    if (dungeonLayer.grid[pos.x][pos.z] == null && dungeonLayer.map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(dungeonLayer, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            case SOUTH:
                int south = Dungeon.SIZE - origin.z - 1;
                if (south > min) {
                    Position2D pos = origin.shift(direction, (randomDistances ? min + rand.nextInt(Math.min(max, south) - min + 1) : Math.min(1 + max, south)));
                    if (dungeonLayer.grid[pos.x][pos.z] == null && dungeonLayer.map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(dungeonLayer, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            case WEST:
                if (origin.x > min) {
                    Position2D pos = origin.shift(direction, (randomDistances ? min + rand.nextInt(Math.min(max, origin.x) - min + 1) : Math.min(1 + max, origin.x)));
                    if (dungeonLayer.grid[pos.x][pos.z] == null && dungeonLayer.map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(dungeonLayer, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    private void createStarterRoom(DungeonLayer dungeonLayer, Random rand, int layer) {
        Tuple<Position2D, Rotation> sideRoomData = dungeonLayer.findStarterRoomData(dungeonLayer.start, rand);
        if (sideRoomData != null) {
            DungeonSideRoom room = new DungeonSideRoom();

            Direction dir = sideRoomData.getB().rotate(Direction.WEST);
            room.openSide(dir);
            room.setGridPosition(sideRoomData.getA().x, sideRoomData.getA().z);
            room.setRotation(sideRoomData.getB());
            room.model = DungeonModels.KEY_TO_MODEL.get("room/starter_room");
            room.stage = layer;

            dungeonLayer.map.markPositionAsOccupied(sideRoomData.getA());
            dungeonLayer.grid[sideRoomData.getA().x][sideRoomData.getA().z] = new PlaceHolder(room).addFlag(PlaceHolder.Flag.FIXED_MODEL);

            Position2D connectedSegment = sideRoomData.getA().shift(dir, 1);
            if (dungeonLayer.grid[connectedSegment.x][connectedSegment.z] != null) {
                dungeonLayer.grid[connectedSegment.x][connectedSegment.z].reference.openSide(dir.getOpposite());
                dungeonLayer.rotatePiece(dungeonLayer.grid[connectedSegment.x][connectedSegment.z], rand);
            }
        }
    }

    /**
     * Builds a straight connection from the start position to the end position.
     * No pieces will be placed at the start and end positions themselves, only in the space between them.
     * The start position and the end position need to have either the same x-coordinate or the same
     * z-coordinate. If this is not the case, an IllegalArgumentException will be thrown.
     *
     * @param start the start position
     * @param end   the end position
     */
    private void connectStraight(DungeonLayer dungeonLayer, Position2D start, Position2D end, Random rand, int layer) {
        if (start.x != end.x || start.z != end.z) {
            if (start.x == end.x) {
                if (start.z > end.z) {
                    // The corridor goes north from the start position (negative z)
                    dungeonLayer.openSideIfPresent(start, Direction.NORTH);
                    dungeonLayer.openSideIfPresent(end, Direction.SOUTH);
                    for (int z = start.z - 1; z > end.z; z--) {
                        if (dungeonLayer.grid[start.x][z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[start.x][z].reference;
                            if (piece.canConnect(Direction.NORTH, start.x, z) && piece.canConnect(Direction.SOUTH, start.x, z)) {
                                piece.openSide(Direction.NORTH);
                                piece.openSide(Direction.SOUTH);
                            }
                        } else {
                            DungeonCorridor corridor = new DungeonCorridor();
                            corridor.setGridPosition(start.x, z);
                            corridor.openSide(Direction.NORTH);
                            corridor.openSide(Direction.SOUTH);
                            corridor.setRotation(Orientation.getRotationFromFacing(Direction.NORTH));
                            dungeonLayer.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);

                            rollForAdditionalNode(dungeonLayer, corridor.gridX, corridor.gridZ, Direction.EAST, rand, layer);
                            rollForAdditionalNode(dungeonLayer, corridor.gridX, corridor.gridZ, Direction.WEST, rand, layer);
                        }
                    }
                } else {
                    // The corridor goes south from the start position (positive z)
                    dungeonLayer.openSideIfPresent(start, Direction.SOUTH);
                    dungeonLayer.openSideIfPresent(end, Direction.NORTH);
                    for (int z = start.z + 1; z < end.z; z++) {
                        if (dungeonLayer.grid[start.x][z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[start.x][z].reference;
                            if (piece.canConnect(Direction.SOUTH, start.x, z) && piece.canConnect(Direction.NORTH, start.x, z)) {
                                piece.openSide(Direction.SOUTH);
                                piece.openSide(Direction.NORTH);
                            }
                        } else {
                            DungeonCorridor corridor = new DungeonCorridor();
                            corridor.setGridPosition(start.x, z);
                            corridor.openSide(Direction.SOUTH);
                            corridor.openSide(Direction.NORTH);
                            corridor.setRotation(Orientation.getRotationFromFacing(Direction.SOUTH));
                            dungeonLayer.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);

                            rollForAdditionalNode(dungeonLayer, corridor.gridX, corridor.gridZ, Direction.WEST, rand, layer);
                            rollForAdditionalNode(dungeonLayer, corridor.gridX, corridor.gridZ, Direction.EAST, rand, layer);
                        }
                    }
                }
            } else if (start.z == end.z) {
                if (start.x > end.x) {
                    // The corridor goes west from the start position (negative x)
                    dungeonLayer.openSideIfPresent(start, Direction.WEST);
                    dungeonLayer.openSideIfPresent(end, Direction.EAST);
                    for (int x = start.x - 1; x > end.x; x--) {
                        if (dungeonLayer.grid[x][start.z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[x][start.z].reference;
                            if (piece.canConnect(Direction.WEST, x, start.z) && piece.canConnect(Direction.EAST, x, start.z)) {
                                piece.openSide(Direction.WEST);
                                piece.openSide(Direction.EAST);
                            }
                        } else {
                            DungeonCorridor corridor = new DungeonCorridor();
                            corridor.setGridPosition(x, start.z);
                            corridor.openSide(Direction.WEST);
                            corridor.openSide(Direction.EAST);
                            corridor.setRotation(Orientation.getRotationFromFacing(Direction.WEST));
                            dungeonLayer.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);

                            rollForAdditionalNode(dungeonLayer, corridor.gridX, corridor.gridZ, Direction.NORTH, rand, layer);
                            rollForAdditionalNode(dungeonLayer, corridor.gridX, corridor.gridZ, Direction.SOUTH, rand, layer);
                        }
                    }
                } else {
                    // The corridor goes east from the start position (positive x)
                    dungeonLayer.openSideIfPresent(start, Direction.EAST);
                    dungeonLayer.openSideIfPresent(end, Direction.WEST);
                    for (int x = start.x + 1; x < end.x; x++) {
                        if (dungeonLayer.grid[x][start.z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[x][start.z].reference;
                            if (piece.canConnect(Direction.EAST, x, start.z) && piece.canConnect(Direction.WEST, x, start.z)) {
                                piece.openSide(Direction.EAST);
                                piece.openSide(Direction.WEST);
                            }
                        } else {
                            DungeonCorridor corridor = new DungeonCorridor();
                            corridor.setGridPosition(x, start.z);
                            corridor.openSide(Direction.EAST);
                            corridor.openSide(Direction.WEST);
                            corridor.setRotation(Orientation.getRotationFromFacing(Direction.EAST));
                            dungeonLayer.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);

                            rollForAdditionalNode(dungeonLayer, corridor.gridX, corridor.gridZ, Direction.SOUTH, rand, layer);
                            rollForAdditionalNode(dungeonLayer, corridor.gridX, corridor.gridZ, Direction.NORTH, rand, layer);
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("The start and end positions of a straight connection must have either the same x-coordinate or the same z-coordinate");
            }
        } else {
            throw new IllegalArgumentException("The start and end positions of a straight connection must not be the same.");
        }
    }

    private void rollForAdditionalNode(DungeonLayer dungeonLayer, int corridorX, int corridorZ, Direction direction, Random rand, int layer) {
        // TODO: Generator settings
        if (nodesLeft > 0 && rand.nextFloat() < 0.2) {
            Position2D corridor = new Position2D(corridorX, corridorZ);
            Position2D center = corridor.shift(direction, 3);
            if (center.isValid(dungeonLayer.width, dungeonLayer.length) && dungeonLayer.canPlaceNode(center)) {
                createNodeRoom(center, dungeonLayer);
                nodesLeft--;

                connectStraight(dungeonLayer, center.shift(direction.getOpposite(), 1), corridor, rand, layer);

                Direction[] directions = Orientation.getHorizontalFacingsWithout(center.directionTo(corridor));

                int count = rand.nextInt(3);
                int start = rand.nextInt(3);

                for (int i = 0; i < 3 && count > 0; i++) {
                    Direction dir = directions[(i + start) % 3];
                    if (findPositionAndContinue(dungeonLayer, center.shift(dir, 1),
                            dir, rand, minDistance, maxDistance, layer, 1)) {
                        count--;
                    }
                }
            }
        }
    }

    public void setSecretRoom(boolean secretRoom) {
        this.secretRoom = secretRoom;
    }

}
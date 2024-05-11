package byow.lab12;

import org.junit.Test;

import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    /**
     * A class representing a position in a 2D array.
     */
    private static class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int x, int y) {
            return new Position(this.x + x, this.y + y);
        }
    }

    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;
    private static final long SEED = 2548545;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Adds a hexagon of the given length to the given position in the given tiles.
     *
     * @param length   the length of the hexagon
     * @param position the position to add the hexagon to
     * @param tiles    the tiles to add the hexagon to
     */
    public static void addHexagon(int length, Position position, TETile tile, TETile[][] tiles) {
        if (length < 2) return;
        drawHexagon(length - 1, length, tile, position, tiles);
    }

    /**
     * Adds a column of hexagons of the given length to the given tiles.
     *
     * @param length   the length of each hexagon in the column
     * @param position the position to start the column at
     * @param tiles    the tiles to add the column to
     */
    public static void addHExColumn(int length, int num, Position position, TETile[][] tiles) {
        if (num < 1) return;
        addHexagon(length, position, randomTile(), tiles);
        if (num > 1) {
            Position nextPosition = getBottomNeighbours(position, length);
            addHExColumn(length, num - 1, nextPosition, tiles);
        }
    }

    private static Position getTopRightNeighbours(Position position, int n) {
        return position.shift(2 * n - 1, n);
    }

    private static Position getBottomRightNeighbours(Position position, int n) {
        return position.shift(2 * n - 1, -n);
    }

    private static Position getBottomNeighbours(Position position, int n) {
        return position.shift(0, -2 * n);
    }

    /**
     * Picks a RANDOM tile with a 33% change of being
     * a wall, 33% chance of being a flower, and 33%
     * chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0:
                return Tileset.GRASS;
            case 1:
                return Tileset.FLOWER;
            case 2:
                return Tileset.MOUNTAIN;
            case 3:
                return Tileset.WATER;
            case 4:
                return Tileset.SAND;
            default:
                return Tileset.NOTHING;
        }
    }

    /**
     * Draws a row of hexagons of the given length at the given position in the given tiles.
     *
     * @param length   the length of each hexagon in the row
     * @param position the position to start the row at
     * @param tile     the tile to draw each hexagon with
     * @param tiles    the tiles to draw the row in
     */
    private static void drawRow(int length, Position position, TETile tile, TETile[][] tiles) {
        for (int dx = 0; dx < length; dx++) {
            tiles[position.x + dx][position.y] = tile;
        }
    }

    /**
     * As a helper method for addHexagon, draws a hexagon of the given length at the given position in the given tiles.
     *
     * @param space    the offset of the hexagon for each row
     * @param pattern  the pattern number of the hexagon for each row
     * @param tile     the tile to draw the hexagon with
     * @param position the position to draw the hexagon at
     * @param tiles    the tiles to draw the hexagon in
     */
    private static void drawHexagon(int space, int pattern, TETile tile, Position position, TETile[][] tiles) {
        // draw this row of the hexagon
        Position startOfRow = position.shift(space, 0);
        drawRow(pattern, startOfRow, tile, tiles);

        // draw the rest of the hexagon
        if (space > 0) {
            Position nextRow = position.shift(0, -1);
            drawHexagon(space - 1, pattern + 2, tile, nextRow, tiles);
        }

        // draw the last row of the hexagon
        Position correspondingRow = startOfRow.shift(0, -(2 * space + 1));
        drawRow(pattern, correspondingRow, tile, tiles);

    }

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     *
     * @param tiles the 2D array of tiles to fill with random tiles
     */
    public static void fillWithRandomTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void generate(TETile[][] tiles, Position position, int hexSize, int testSize) {
        addHExColumn(hexSize, testSize, position, tiles);
        for (int i = 1; i < testSize; i++) {
            position = getTopRightNeighbours(position, hexSize);
            addHExColumn(hexSize, testSize + i, position, tiles);
        }
        for (int i = testSize - 2; i >= 0; i--) {
            position = getBottomRightNeighbours(position, hexSize);
            addHExColumn(hexSize, testSize + i, position, tiles);
        }
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithRandomTiles(world);
        Position position = new Position(12, 43);
        generate(world,position, 3, 4);

        ter.renderFrame(world);
    }
}

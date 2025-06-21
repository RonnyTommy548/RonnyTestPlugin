package com.zulrahloot;

import net.runelite.api.CollisionDataFlag;
import net.runelite.api.coords.WorldPoint;

public class LootPlacement {

    public enum LootDirection {
        DIAGONAL,
        CARDINAL
    }

    public enum AbsoluteDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NORTHEAST,
        NORTHWEST,
        SOUTHEAST,
        SOUTHWEST
    }

    public static LootDirection getLootDirection(int dx, int dy) {
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        if (absDx == 2 * absDy || absDy == 2 * absDx) {
            // tie breaker case
            if (dx < 0 && absDx < absDy) {
                return LootDirection.CARDINAL; // west case
            } else if (dx > 0 && absDx > absDy) {
                return LootDirection.CARDINAL; // east case
            } else {
                return LootDirection.DIAGONAL;
            }
        } else if (absDx > 2 * absDy || absDy > 2 * absDx) {
            // one axis delta is clearly dominant by more than 2x
            return LootDirection.CARDINAL;
        } else {
            return LootDirection.DIAGONAL;
        }
    }

    public static AbsoluteDirection resolveLootDirection(int dx, int dy, LootDirection direction) {
        if (direction == LootDirection.CARDINAL) {
            if (Math.abs(dx) > Math.abs(dy)) {
                return dx > 0 ? AbsoluteDirection.EAST : AbsoluteDirection.WEST;
            } else {
                return dy > 0 ? AbsoluteDirection.NORTH : AbsoluteDirection.SOUTH;
            }
        } else { // DIAGONAL
            if (dx > 0 && dy > 0) return AbsoluteDirection.NORTHEAST;
            if (dx < 0 && dy > 0) return AbsoluteDirection.NORTHWEST;
            if (dx > 0 && dy < 0) return AbsoluteDirection.SOUTHEAST;
            return AbsoluteDirection.SOUTHWEST;
        }
    }

    public static WorldPoint getLootDestinationTile(WorldPoint zulrah, WorldPoint player, int[][] collisionMap, int baseX, int baseY)
{
	// Use Zulrah's center tile (2 tiles NE of SW)
	int zulrahX = zulrah.getX() + 2;
	int zulrahY = zulrah.getY() + 2;

	int dx = zulrahX - player.getX();
	int dy = zulrahY - player.getY();

	LootDirection lootDirection = LootPlacement.getLootDirection(dx, dy);
	AbsoluteDirection absoluteDirection = LootPlacement.resolveLootDirection(dx, dy, lootDirection);

	int currX = player.getX() - baseX;
	int currY = player.getY() - baseY;
	int lastValidDiagonalX = currX;
	int lastValidDiagonalY = currY;

	int maxSteps = 20;

	if ((collisionMap[currX][currY] & CollisionDataFlag.BLOCK_MOVEMENT_FULL) != 0) {
		return new WorldPoint(currX + baseX, currY + baseY, player.getPlane());
	}

	int stepX = 0;
	int stepY = 0;

	switch (absoluteDirection) {
		case NORTH:      stepY = 1;  break;
		case SOUTH:      stepY = -1; break;
		case EAST:       stepX = 1;  break;
		case WEST:       stepX = -1; break;
		case NORTHEAST:  stepX = 1;  stepY = 1;  break;
		case NORTHWEST:  stepX = -1; stepY = 1;  break;
		case SOUTHEAST:  stepX = 1;  stepY = -1; break;
		case SOUTHWEST:  stepX = -1; stepY = -1; break;
	}

	if (lootDirection == LootDirection.CARDINAL)
	{
		// Cardinal stepping logic stays unchanged
		for (int i = 0; i < maxSteps; i++) {
			currX += stepX;
			currY += stepY;

			if (currX < 0 || currY < 0 || currX >= collisionMap.length || currY >= collisionMap[0].length) {
				break;
			}

			int flag = collisionMap[currX][currY];
			if ((flag & CollisionDataFlag.BLOCK_MOVEMENT_FULL) != 0) {
				currX -= stepX;
				currY -= stepY;
				break;
			}
		}
		return new WorldPoint(currX + baseX, currY + baseY, player.getPlane());
	}
	else
	{
		// Diagonal zig-zag stepping: start with N/S step, then alternate with E/W
		for (int i = 0; i < maxSteps; i++) {
			// Step in Y (N/S)
			currY += stepY;
			if (currX < 0 || currY < 0 || currX >= collisionMap.length || currY >= collisionMap[0].length
				|| (collisionMap[currX][currY] & CollisionDataFlag.BLOCK_MOVEMENT_FULL) != 0) {
				break;
			}

			// Step in X (E/W)
			currX += stepX;
			if (currX < 0 || currY < 0 || currX >= collisionMap.length || currY >= collisionMap[0].length
				|| (collisionMap[currX][currY] & CollisionDataFlag.BLOCK_MOVEMENT_FULL) != 0) {
				break;
			}

			// Valid full diagonal step (Y then X)
			lastValidDiagonalX = currX;
			lastValidDiagonalY = currY;
		}

		return new WorldPoint(lastValidDiagonalX + baseX, lastValidDiagonalY + baseY, player.getPlane());
	}
}


}
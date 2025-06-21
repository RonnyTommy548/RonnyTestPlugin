package com.zulrahloot;

import org.junit.jupiter.api.Test;

import com.zulrahloot.LootPlacement;
import com.zulrahloot.LootPlacement.AbsoluteDirection;

import static org.junit.jupiter.api.Assertions.*;

public class LootPlacementTest {

    @Test
    public void testTieBreakerCases() {
        // Expected: CARDINAL
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(-4, 8), "-4, 8 should be CARDINAL");
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(-4, -8), "-4, -8 should be CARDINAL");
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(8, 4), "8, 4 should be CARDINAL");
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(8, -4), "8, -4 should be CARDINAL");

        // Expected: DIAGONAL
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(-8, 4), "-8, 4 should be DIAGONAL");
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(-8, -4), "-8, -4 should be DIAGONAL");
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(4, 8), "4, 8 should be DIAGONAL");
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(4, -8), "4, -8 should be DIAGONAL");
    }

    @Test
    public void testClearlyDominantAxisCases() {
        // dx is clearly dominant over dy
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(9, 4), "9, 4 should be CARDINAL");
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(9, -4), "9, -4 should be CARDINAL");
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(-9, 4), "-9, 4 should be CARDINAL");
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(-9, -4), "-9, -4 should be CARDINAL");

        // dy is clearly dominant over dx
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(4, 9), "4, 9 should be CARDINAL");
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(4, -9), "4, -9 should be CARDINAL");
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(-4, 9), "-4, 9 should be CARDINAL");
        assertEquals(LootPlacement.LootDirection.CARDINAL, LootPlacement.getLootDirection(-4, -9), "-4, -9 should be CARDINAL");
    }

    @Test
    public void testNoClearlyDominantAxisCases() {
        // dx is not more than 2x dy
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(7, 4), "7, 4 should be DIAGONAL");
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(7, -4), "7, -4 should be DIAGONAL");
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(-7, 4), "-7, 4 should be DIAGONAL");
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(-7, -4), "-7, -4 should be DIAGONAL");

        // dy is not more than 2x dx
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(4, 7), "4, 7 should be DIAGONAL");
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(4, -7), "4, -7 should be DIAGONAL");
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(-4, 7), "-4, 7 should be DIAGONAL");
        assertEquals(LootPlacement.LootDirection.DIAGONAL, LootPlacement.getLootDirection(-4, -7), "-4, -7 should be DIAGONAL");
    }

    @Test
    public void testResolveLootDirection_Cardinal() {
        assertEquals(AbsoluteDirection.EAST, LootPlacement.resolveLootDirection(5, 0, LootPlacement.LootDirection.CARDINAL));
        assertEquals(AbsoluteDirection.WEST, LootPlacement.resolveLootDirection(-5, 0, LootPlacement.LootDirection.CARDINAL));
        assertEquals(AbsoluteDirection.NORTH, LootPlacement.resolveLootDirection(0, 5, LootPlacement.LootDirection.CARDINAL));
        assertEquals(AbsoluteDirection.SOUTH, LootPlacement.resolveLootDirection(0, -5, LootPlacement.LootDirection.CARDINAL));
    }

    @Test
    public void testResolveLootDirection_Diagonal() {
        assertEquals(AbsoluteDirection.SOUTHWEST, LootPlacement.resolveLootDirection(-4, -4, LootPlacement.LootDirection.DIAGONAL));
        assertEquals(AbsoluteDirection.SOUTHEAST, LootPlacement.resolveLootDirection(4, -4, LootPlacement.LootDirection.DIAGONAL));
        assertEquals(AbsoluteDirection.NORTHWEST, LootPlacement.resolveLootDirection(-4, 4, LootPlacement.LootDirection.DIAGONAL));
        assertEquals(AbsoluteDirection.NORTHEAST, LootPlacement.resolveLootDirection(4, 4, LootPlacement.LootDirection.DIAGONAL));
    }
}
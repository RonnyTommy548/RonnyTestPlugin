package com.example;

import org.junit.jupiter.api.Test;

import com.example.LootPlacementAlgo.AbsoluteDirection;

import static org.junit.jupiter.api.Assertions.*;

public class LootPlacementAlgoTest {

    @Test
    public void testTieBreakerCases() {
        // Expected: CARDINAL
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(-4, 8), "-4, 8 should be CARDINAL");
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(-4, -8), "-4, -8 should be CARDINAL");
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(8, 4), "8, 4 should be CARDINAL");
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(8, -4), "8, -4 should be CARDINAL");

        // Expected: DIAGONAL
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(-8, 4), "-8, 4 should be DIAGONAL");
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(-8, -4), "-8, -4 should be DIAGONAL");
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(4, 8), "4, 8 should be DIAGONAL");
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(4, -8), "4, -8 should be DIAGONAL");
    }

    @Test
    public void testClearlyDominantAxisCases() {
        // dx is clearly dominant over dy
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(9, 4), "9, 4 should be CARDINAL");
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(9, -4), "9, -4 should be CARDINAL");
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(-9, 4), "-9, 4 should be CARDINAL");
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(-9, -4), "-9, -4 should be CARDINAL");

        // dy is clearly dominant over dx
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(4, 9), "4, 9 should be CARDINAL");
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(4, -9), "4, -9 should be CARDINAL");
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(-4, 9), "-4, 9 should be CARDINAL");
        assertEquals(LootPlacementAlgo.LootDirection.CARDINAL, LootPlacementAlgo.getLootDirection(-4, -9), "-4, -9 should be CARDINAL");
    }

    @Test
    public void testNoClearlyDominantAxisCases() {
        // dx is not more than 2x dy
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(7, 4), "7, 4 should be DIAGONAL");
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(7, -4), "7, -4 should be DIAGONAL");
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(-7, 4), "-7, 4 should be DIAGONAL");
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(-7, -4), "-7, -4 should be DIAGONAL");

        // dy is not more than 2x dx
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(4, 7), "4, 7 should be DIAGONAL");
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(4, -7), "4, -7 should be DIAGONAL");
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(-4, 7), "-4, 7 should be DIAGONAL");
        assertEquals(LootPlacementAlgo.LootDirection.DIAGONAL, LootPlacementAlgo.getLootDirection(-4, -7), "-4, -7 should be DIAGONAL");
    }

    @Test
    public void testResolveLootDirection_Cardinal() {
        assertEquals(AbsoluteDirection.EAST, LootPlacementAlgo.resolveLootDirection(5, 0, LootPlacementAlgo.LootDirection.CARDINAL));
        assertEquals(AbsoluteDirection.WEST, LootPlacementAlgo.resolveLootDirection(-5, 0, LootPlacementAlgo.LootDirection.CARDINAL));
        assertEquals(AbsoluteDirection.NORTH, LootPlacementAlgo.resolveLootDirection(0, 5, LootPlacementAlgo.LootDirection.CARDINAL));
        assertEquals(AbsoluteDirection.SOUTH, LootPlacementAlgo.resolveLootDirection(0, -5, LootPlacementAlgo.LootDirection.CARDINAL));
    }

    @Test
    public void testResolveLootDirection_Diagonal() {
        assertEquals(AbsoluteDirection.SOUTHWEST, LootPlacementAlgo.resolveLootDirection(-4, -4, LootPlacementAlgo.LootDirection.DIAGONAL));
        assertEquals(AbsoluteDirection.SOUTHEAST, LootPlacementAlgo.resolveLootDirection(4, -4, LootPlacementAlgo.LootDirection.DIAGONAL));
        assertEquals(AbsoluteDirection.NORTHWEST, LootPlacementAlgo.resolveLootDirection(-4, 4, LootPlacementAlgo.LootDirection.DIAGONAL));
        assertEquals(AbsoluteDirection.NORTHEAST, LootPlacementAlgo.resolveLootDirection(4, 4, LootPlacementAlgo.LootDirection.DIAGONAL));
    }
}
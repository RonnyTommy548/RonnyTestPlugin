package com.zulrahloot;

import static org.junit.jupiter.api.Assertions.*;

import com.zulrahloot.LootPlacement.AbsoluteDirection;
import com.zulrahloot.LootPlacement.LootDirection;
import org.junit.jupiter.api.Test;

public class LootPlacementTest {

  @Test
  public void testTieBreakerCases() {
    assertEquals(
        LootDirection.CARDINAL, LootPlacement.getLootDirection(-4, 8), "-4, 8 should be CARDINAL");
    assertEquals(
        LootDirection.CARDINAL,
        LootPlacement.getLootDirection(-4, -8),
        "-4, -8 should be CARDINAL");
    assertEquals(
        LootDirection.CARDINAL, LootPlacement.getLootDirection(8, 4), "8, 4 should be CARDINAL");
    assertEquals(
        LootDirection.CARDINAL, LootPlacement.getLootDirection(8, -4), "8, -4 should be CARDINAL");

    assertEquals(
        LootDirection.DIAGONAL, LootPlacement.getLootDirection(-8, 4), "-8, 4 should be DIAGONAL");
    assertEquals(
        LootDirection.DIAGONAL,
        LootPlacement.getLootDirection(-8, -4),
        "-8, -4 should be DIAGONAL");
    assertEquals(
        LootDirection.DIAGONAL, LootPlacement.getLootDirection(4, 8), "4, 8 should be DIAGONAL");
    assertEquals(
        LootDirection.DIAGONAL, LootPlacement.getLootDirection(4, -8), "4, -8 should be DIAGONAL");
  }

  @Test
  public void testClearlyDominantAxisCases() {
    assertEquals(
        LootDirection.CARDINAL, LootPlacement.getLootDirection(9, 4), "9, 4 should be CARDINAL");
    assertEquals(
        LootDirection.CARDINAL, LootPlacement.getLootDirection(9, -4), "9, -4 should be CARDINAL");
    assertEquals(
        LootDirection.CARDINAL, LootPlacement.getLootDirection(-9, 4), "-9, 4 should be CARDINAL");
    assertEquals(
        LootDirection.CARDINAL,
        LootPlacement.getLootDirection(-9, -4),
        "-9, -4 should be CARDINAL");

    assertEquals(
        LootDirection.CARDINAL, LootPlacement.getLootDirection(4, 9), "4, 9 should be CARDINAL");
    assertEquals(
        LootDirection.CARDINAL, LootPlacement.getLootDirection(4, -9), "4, -9 should be CARDINAL");
    assertEquals(
        LootDirection.CARDINAL, LootPlacement.getLootDirection(-4, 9), "-4, 9 should be CARDINAL");
    assertEquals(
        LootDirection.CARDINAL,
        LootPlacement.getLootDirection(-4, -9),
        "-4, -9 should be CARDINAL");
  }

  @Test
  public void testNoClearlyDominantAxisCases() {
    assertEquals(
        LootDirection.DIAGONAL, LootPlacement.getLootDirection(7, 4), "7, 4 should be DIAGONAL");
    assertEquals(
        LootDirection.DIAGONAL, LootPlacement.getLootDirection(7, -4), "7, -4 should be DIAGONAL");
    assertEquals(
        LootDirection.DIAGONAL, LootPlacement.getLootDirection(-7, 4), "-7, 4 should be DIAGONAL");
    assertEquals(
        LootDirection.DIAGONAL,
        LootPlacement.getLootDirection(-7, -4),
        "-7, -4 should be DIAGONAL");

    assertEquals(
        LootDirection.DIAGONAL, LootPlacement.getLootDirection(4, 7), "4, 7 should be DIAGONAL");
    assertEquals(
        LootDirection.DIAGONAL, LootPlacement.getLootDirection(4, -7), "4, -7 should be DIAGONAL");
    assertEquals(
        LootDirection.DIAGONAL, LootPlacement.getLootDirection(-4, 7), "-4, 7 should be DIAGONAL");
    assertEquals(
        LootDirection.DIAGONAL,
        LootPlacement.getLootDirection(-4, -7),
        "-4, -7 should be DIAGONAL");
  }

  @Test
  public void testResolveLootDirection_Cardinal() {
    assertEquals(
        AbsoluteDirection.EAST, LootPlacement.resolveLootDirection(5, 0, LootDirection.CARDINAL));
    assertEquals(
        AbsoluteDirection.WEST, LootPlacement.resolveLootDirection(-5, 0, LootDirection.CARDINAL));
    assertEquals(
        AbsoluteDirection.NORTH, LootPlacement.resolveLootDirection(0, 5, LootDirection.CARDINAL));
    assertEquals(
        AbsoluteDirection.SOUTH, LootPlacement.resolveLootDirection(0, -5, LootDirection.CARDINAL));
  }

  @Test
  public void testResolveLootDirection_Diagonal() {
    assertEquals(
        AbsoluteDirection.SOUTHWEST,
        LootPlacement.resolveLootDirection(-4, -4, LootPlacement.LootDirection.DIAGONAL));
    assertEquals(
        AbsoluteDirection.SOUTHEAST,
        LootPlacement.resolveLootDirection(4, -4, LootPlacement.LootDirection.DIAGONAL));
    assertEquals(
        AbsoluteDirection.NORTHWEST,
        LootPlacement.resolveLootDirection(-4, 4, LootPlacement.LootDirection.DIAGONAL));
    assertEquals(
        AbsoluteDirection.NORTHEAST,
        LootPlacement.resolveLootDirection(4, 4, LootPlacement.LootDirection.DIAGONAL));
  }
}

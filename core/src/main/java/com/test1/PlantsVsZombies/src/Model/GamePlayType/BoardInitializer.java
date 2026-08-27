package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;

public class BoardInitializer {

    public static void initializeBoard(ChapterType chapterType, ArrayList<Mower> mowers,
                                       ArrayList<Tile> tiles, GamePlay gamePlay) {
        if (chapterType == ChapterType.BIG_WAVE_BEACH) {
            setupBeachBoard(mowers, tiles, gamePlay);
        } else {
            setupStandardBoard(chapterType, mowers, tiles, gamePlay);
        }
    }

    private static void setupBeachBoard(ArrayList<Mower> mowers, ArrayList<Tile> tiles, GamePlay gamePlay) {
        for (int y = 1; y < 6; y++) {
            float startX = 430f;
            float startY = gamePlay.getRealY(y) + 15;
            mowers.add(new Mower(y, startX, startY));

            for (int x = 1; x < 10; x++) {
                Position newPosition = new Position(x, y);
                boolean isArable = (x <= 7);
                Tile newTile = new Tile(newPosition, isArable, 0);

                if (x == 9 && Math.random() <= 0.60) {
                    newTile.setLowTide(true);
                }
                tiles.add(newTile);
            }
        }
    }

    private static void setupStandardBoard(ChapterType chapterType, ArrayList<Mower> mowers,
                                           ArrayList<Tile> tiles, GamePlay gamePlay) {
        for (int y = 1; y < 6; y++) {
            float startX = 430f;
            float startY = gamePlay.getRealY(y) + 15;
            mowers.add(new Mower(y, startX, startY));

            for (int x = 1; x < 10; x++) {
                Position newPosition = new Position(x, y);
                boolean isArable = (Math.random() >= 0.06 || (x == 5 && (y == 2 || y == 4))) ||
                    (x == 1 || x == 2 || x == 3);
                int tileHP = 0;
                if (!isArable && (chapterType == ChapterType.ANCIENT_EGYPT || chapterType == ChapterType.DARK_AGE)) {
                    tileHP = 700;
                } else if (chapterType == ChapterType.FROSTBITE_CAVES) {
                    tileHP = (Math.random() <= 0.5) ? 700 : 0;
                }
                Tile newTile = new Tile(newPosition, isArable, tileHP);

                if (!isArable && chapterType == ChapterType.DARK_AGE) {
                    double rand = Math.random();
                    if (rand < 0.20) {
                        newTile.setGraveType(Tile.GraveType.PLANT_FOOD);
                    } else if (rand < 0.40) {
                        newTile.setGraveType(Tile.GraveType.SUN);
                    } else {
                        newTile.setGraveType(Tile.GraveType.NORMAL);
                    }

                    if (Math.random() <= 0.30) {
                        newTile.setNecromancy(true);
                    }
                }
                tiles.add(newTile);
            }
        }
    }
}

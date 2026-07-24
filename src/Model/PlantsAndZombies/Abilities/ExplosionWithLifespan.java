package src.Model.PlantsAndZombies.Abilities;

import src.Enums.ChapterType;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.Tile;

import java.util.ArrayList;

public class ExplosionWithLifespan implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        ArrayList<String> tags = plant.getPlantStats().getTags();

        if (tags.contains("insta-kill")) {
            handleMakingArablePlants(plant, tags);
        }
        if (tags.contains("shroom")) {
            handleShroomPlants(plant, tags);
            return;
        }

        if (tags.contains("fire")) {
            fireLine(plant);
            return;
        }

        if (tags.contains("AoE")) {
            AoEDamage(plant);
            return;
        }
    }

    private void handleShroomPlants(BattlePlant plant, ArrayList<String> tags) {
        int damage = (int) plant.getPlantStats().getAttributes().get("damage");
        if (tags.contains("Ice")) {
            //todo;
            for (Zombie zombie : game.getZombies()) {
                int frozenTime = (int) plant.getPlantStats().getAttributes().get("freezeTime");
                zombie.freeze(frozenTime);
                zombie.takeDamage(damage);
            }
        } else {
            //todo:
            for (Tile tile : game.getTiles()) {
                for (Zombie zombie : tile.getZombies()) {
                    zombie.takeDamage(damage);
                }
            }
            //todo:
            Tile tile = game.getTile();
            tile.setArable(false);
        }
    }

    private void fireLine(BattlePlant plant) {
        int damage = (int) plant.getPlantStats().getAttributes().get("damage");

        int row = plant.getRow();
        for (int i = 0; i < 9; i++) {
            //todo
            Tile tile = game.getTile();
            for (Zombie zombie : tile.getZombies()) {
                zombie.unfreeze();
                zombie.takeDamage(damage);
            }
            for (BattlePlant battlePlant : tile.getPlants()) {
                battlePlant.setFrozen(false);
            }
        }
    }

    private void AoEDamage(BattlePlant plant) {
        int range = (int) plant.getPlantStats().getAttributes().get("range");
        int damage = (int) plant.getPlantStats().getAttributes().get("damage");
        int plantRow = plant.getRow();
        int plantColumn = plant.getColumn();

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                //todo
                Tile tile = game.getTile();
                for (Zombie zombie : tile.getZombies()) {
                    zombie.takeDamage(damage);
                }
            }
        }
    }

    private void handleMakingArablePlants(BattlePlant plant, ArrayList<String> tags) {
        if (tags.contains("fire")) {
            int row = plant.getRow();
            int column = plant.getColumn();
            if (plant.getPlantStats().getAttributes().containsKey("range")) {
                rangeMelt();
            } else {
                //todo:
                Tile tile = game.getTile();
                for (BattlePlant plant1 : tile.getPlants()) {
                    plant1.setFrozen(false);
                }
            }

        } else {
            //todo
            for (Tile tile : game.getTile()) {
                if ((game.getChapter.equals(ChapterType.ANCIENT_EGYPT)) ||
                        ((game.getChapter.equals(ChapterType.DARK_AGE))) {
                    if (!tile.isArable()) {
                        tile.setArable(true);
                    }
                }
            }
        }

        if (plant.getPlantStats().getAttributes().containsKey("damage")) {
            int damage = (int) plant.getPlantStats().getAttributes().get("damage");

            Tile tile = game.getTile();//todo
            for (Zombie zombie : tile.getZombies()) {
                zombie.takeDamage(damage);
            }
        }
    }

    private void rangeMelt(BattlePlant plant) {
        int range = (int) plant.getPlantStats().getAttributes().get("range");
        int plantRow = plant.getRow();
        int plantColumn = plant.getColumn();

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                //todo
                Tile tile = game.getTile();
                for (BattlePlant plant1 : tile.getPlants()) {
                    plant1.setFrozen(false);
                }
            }
        }
    }
}

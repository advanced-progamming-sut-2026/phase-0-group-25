package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.Main;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.Model.DroppedPlantFood;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.*;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.SandstormEffect;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.BorderedTable;
import com.test1.PlantsVsZombies.src.Model.IcyWindEffect;
import java.util.ArrayList;
import java.util.Iterator;

public class GamePlayScreen extends ScreenAdapter implements GamePlayMenuView {
    private GamePlay gamePlay;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private ScreenViewport viewport;
    private TextureBank textureBank;
    private TextureRegion region;
    private PamPlayer player;
    private float stateTime = 0;
    private BitmapFont hudFont;
    private TextureRegion sunIcon;
    private TextureRegion plantFoodIcon;
    private TextureRegion getPlantFoodIconInGame;
    private TextureRegion bgHud;
    private float timeAccumulator = 0f;
    private final float TICK_RATE = 0.1f;
    private TextureRegion flagIcon;
    private TextureRegion zombieHeadIcon;
    private TextureRegion progressBarFrame;
    private TextureRegion cardBgRegion;
    private BattlePlant selectedPlant = null;
    private Vector3 mouseWorldPos = new Vector3();
    private TextureRegion plusIcon;
    private static final float SUN_PLUS_X = 190f;
    private static final float SUN_PLUS_Y = 1120f;
    private static final float PF_PLUS_X = 269f;
    private static final float PF_PLUS_Y = 1120f;
    private static final float PLUS_BTN_SIZE = 40f;
    private static final float CARD_X = 45f;
    private static final float CARD_START_Y = 980f;
    private static final float CARD_WIDTH = 160f;
    private static final float CARD_HEIGHT = 105f;
    private static final float CARD_SPACING = 11f;
    private static final String PF_BANK_SLOT_ASSET_ID = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BANK_FILLED_SLOT";
    private static final String SOS_TILE_ASSET_ID = "IMAGE_BACKGROUNDS_PROTECT_TILE_PROTECT_TILE_112X125";
    private TextureRegion sosTileRegion;
    private static final float DEADLINE_X = 943f;
    private TextureRegion pfBankSlotRegion;
    private static final String PLUS_BUTTON_ASSET_ID = "IMAGE_UI_HUD_INGAME_COIN_BUY";
    private static final String CARD_BG_ASSET_ID = "IMAGE_UI_PACKETS_SELECTED";
    private static final String ERROR_BG_ASSET_ID = "IMAGE_UI_GENERIC_TIMER_RIBBON_RED";
    private boolean isShovelSelected = false;
    private TextureRegion shovelIcon;
    private TextureRegion shovelIconInGame;
    private TextureRegion iceSliderRegion;
    private TextureRegion iceBlockTexture;
    private TextureRegion cardBoostedBgRegion;
    private static final String BEACH_WATER_ANIM_PATH = "768/FULL/BACKGROUNDS/WAVE_UPPERLAYER/WAVE_UPPERLAYER.PAM";
    private static final String BEACH_TIDELINE_ANIM_PATH = "768/FULL/BACKGROUNDS/WATER_TIDE_LINE/WATER_TIDE_LINE.PAM";
    private TextureRegion lowTideRuneRegion;
    private static final String CARD_BOOSTED_BG_ASSET_ID = "IMAGE_UI_PACKETS_BOOST";
    private static final String ICY_WIND_ANIM_PATH = "768/FULL/EFFECTS/FROSTBITE_CHILL_WIND/FROSTBITE_CHILL_WIND.PAM";
    private static final String ICE_SLIDER_ASSET_ID = "IMAGE_EFFECTS_TILESLIDER_ICEAGE_UP_TILESLIDER_ICEAGE_UP_116X140";
    private static final String SHOVEL_ASSET = "IMAGE_UI_HUD_INGAME_SHOVEL_ICON";
    private static final String SHOVEL_ASSET_ID = "IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON";
    private static final String SANDSTORM_ANIM_PATH = "768/INITIAL/EFFECTS/SANDSTORM_TOP/SANDSTORM_TOP.PAM";
    private static final String FOOD_BANK_ASSET_ID = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BANK";
    private static final String PF_LOCKED_SLOT_ASSET_ID = "IMAGE_ZEN_GARDEN_LOCKED_POT_ICON";
    private TextureRegion foodBankRegion;
    private TextureRegion pfLockedSlotRegion;
    private boolean isPlantFoodSelected = false;
    private static final float PF_BTN_X = 1675f;
    private static final float PF_BTN_Y = 30f;
    private static final float PF_BTN_SIZE = 100f;
    private static final float SHOVEL_BTN_X = 1770f;
    private static final float SHOVEL_BTN_Y = 30f;
    private static final float SHOVEL_BTN_SIZE = 100f;
    private static final String[] EGYPT_GRAVE_ASSET_IDS = {
        "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_118X148",
        "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_118X148_2",
        "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_113X145",
        "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_110X145",
        "IMAGE_GRAVESTONES_EGYPT_HIEROGLYPH_EGYPT_HIEROGLYPH_109X119"
    };
    private final TextureRegion[] iceStageRegions = new TextureRegion[3];
    private static final String[] ICE_STAGE_ASSET_IDS = {
        "IMAGE_EFFECTS_FROSTBITE_CHILL_PLANT_FROSTBITE_CHILL_PLANT_153X62",
        "IMAGE_EFFECTS_FROSTBITE_CHILL_PLANT_FROSTBITE_CHILL_PLANT_153X79",
        "IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_164X169"
    };
    private final TextureRegion[] darkNormalGraveRegions = new TextureRegion[5];
    private final TextureRegion[] darkPlantFoodGraveRegions = new TextureRegion[5];
    private final TextureRegion[] darkSunGraveRegions = new TextureRegion[5];
    private TextureRegion necromancyRuneRegion;
    private IntroDialogueCutscene introCutscene;
    private static final String[] DARK_NORMAL_GRAVES = {
        "IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_132X160", "IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_132X160_2",
        "IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_132X156", "IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_125X149", "IMAGE_GRAVESTONES_DARK_NOOP_DARK_NOOP_93X89"
    };
    private static final String[] DARK_PF_GRAVES = {
        "IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_132X160", "IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_132X160_2",
        "IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_132X157", "IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_129X144", "IMAGE_GRAVESTONES_DARK_PLANTFOOD_DARK_PLANTFOOD_93X95"
    };
    private static final String[] DARK_SUN_GRAVES = {
        "IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_132X160", "IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_132X160_2",
        "IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_132X157", "IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_132X144", "IMAGE_GRAVESTONES_DARK_SUN_DARK_SUN_93X91"
    };
    private final TextureRegion[] egyptGraveRegions = new TextureRegion[5];
    private static final float GRAVE_MAX_HP = 700f;

    private static final float TIDELINE_X = 1595f;
    private static final float TIDELINE_Y = 505f;
    private static final float WATER_BASE_X = 2220f;
    private static final float WATER_BASE_Y = 505f;
    private static final float WATER_MOVE_RANGE = 76f;

    private static final float START_WAVE_BTN_X = 1450f;
    private static final float START_WAVE_BTN_Y = 1100f;
    private static final float START_WAVE_BTN_W = 220f;
    private static final float START_WAVE_BTN_H = 75f;

    // ---- Objectives / end-of-game modal system ----
    private Stage uiStage;
    private Skin skin;
    private Stack modalStack;
    private boolean endModalShown = false;

    public GamePlayScreen(GamePlay gamePlay) {
        this.gamePlay = gamePlay;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1200);
        shapeRenderer = new ShapeRenderer();


        batch = new SpriteBatch();
        viewport = new ScreenViewport();
        textureBank = new TextureBank("768", Gdx.files.local("Assets"));
        player = new PamPlayer(textureBank, Gdx.files.local("Assets"));

        String bgKey = getBgPath(gamePlay.getChapterType());
        region = textureBank.region(bgKey);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.local("pvz.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 48;
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 3;
        hudFont = generator.generateFont(parameter);
        generator.dispose();
        shovelIcon = textureBank.region(SHOVEL_ASSET_ID);
        pfBankSlotRegion = textureBank.region(PF_BANK_SLOT_ASSET_ID);
        shovelIconInGame = textureBank.region(SHOVEL_ASSET);
        cardBgRegion = textureBank.region(CARD_BG_ASSET_ID);
        plusIcon = textureBank.region(PLUS_BUTTON_ASSET_ID);
        iceSliderRegion = textureBank.region(ICE_SLIDER_ASSET_ID);
        cardBoostedBgRegion = textureBank.region(CARD_BOOSTED_BG_ASSET_ID);
        sosTileRegion = textureBank.region(SOS_TILE_ASSET_ID);
        sunIcon = textureBank.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN");
        plantFoodIcon = textureBank.region("IMAGE_UI_HUD_INGAME_PLANTFOOD_BUTTON");
        getPlantFoodIconInGame = textureBank.region("IMAGE_EFFECTS_PLANTFOOD_PICKUP_PLANTFOOD_PICKUP_79X79");
        bgHud = textureBank.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        flagIcon = textureBank.region("IMAGE_ZOMBIE_ZOMBIE_FEASTIVUS_FLAG_ZOMBIE_FEASTIVUS_FLAG_123X95");
        zombieHeadIcon = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD");
        progressBarFrame = textureBank.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER");
        iceBlockTexture = textureBank.region("IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_ZOMBIE_FROSTBITE_ICE_BLOCK_ZOMBIE_153X243");
        lowTideRuneRegion = textureBank.region("IMAGE_PLANT_WATERRABBIT_WATERRABBIT_82X82");
        for (int i = 0; i < EGYPT_GRAVE_ASSET_IDS.length; i++) {
            egyptGraveRegions[i] = textureBank.region(EGYPT_GRAVE_ASSET_IDS[i]);
        }
        for (int i = 0; i < ICE_STAGE_ASSET_IDS.length; i++) {
            iceStageRegions[i] = textureBank.region(ICE_STAGE_ASSET_IDS[i]);
        }
        foodBankRegion = textureBank.region(FOOD_BANK_ASSET_ID);
        if (foodBankRegion == null) {
            foodBankRegion = textureBank.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        }
        pfLockedSlotRegion = textureBank.region(PF_LOCKED_SLOT_ASSET_ID);
        for (int i = 0; i < 5; i++) {
            darkNormalGraveRegions[i] = textureBank.region(DARK_NORMAL_GRAVES[i]);
            darkPlantFoodGraveRegions[i] = textureBank.region(DARK_PF_GRAVES[i]);
            darkSunGraveRegions[i] = textureBank.region(DARK_SUN_GRAVES[i]);
        }
        if (gamePlay.getChapterType() == ChapterType.ANCIENT_EGYPT && gamePlay.getLevel() == 1) {
            introCutscene = new IntroDialogueCutscene(textureBank);
        }
        necromancyRuneRegion = textureBank.region("IMAGE_EFFECTS_GRIMROSE_UNDERZOMBIE_EFFECT_GRIMROSE_UNDERZOMBIE_EFFECT_167X52");

        skin = Main.getInstance().getSkin();
        uiStage = new Stage(new ScreenViewport());
        modalStack = new Stack();
        modalStack.setFillParent(true);
        uiStage.addActor(modalStack);

        InputAdapter gameInputAdapter = new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
                gamePlay.tryCollectSunByClick(mouseWorldPos.x, mouseWorldPos.y);
                gamePlay.tryCollectPlantFoodByHover(mouseWorldPos.x, mouseWorldPos.y);
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                camera.unproject(mouseWorldPos.set(screenX, screenY, 0));
                gamePlay.tryCollectSunByClick(mouseWorldPos.x, mouseWorldPos.y);
                gamePlay.tryCollectPlantFoodByHover(mouseWorldPos.x, mouseWorldPos.y);
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                camera.unproject(mouseWorldPos.set(screenX, screenY, 0));

                if (introCutscene != null && !introCutscene.isFinished()) {
                    introCutscene.advance();
                    if (introCutscene.isFinished()) {
                        gamePlay.isPaused = false;
                    }
                    return true;
                }
                if (button == com.badlogic.gdx.Input.Buttons.RIGHT) {
                    selectedPlant = null;
                    isShovelSelected = false;
                    isPlantFoodSelected = false;
                    return true;
                }

                User user = gamePlay.getThisUser();
                boolean isDebug = user != null && user.isDebugMode();


                if (isDebug && button == com.badlogic.gdx.Input.Buttons.LEFT) {
                    if (mouseWorldPos.x >= SUN_PLUS_X && mouseWorldPos.x <= SUN_PLUS_X + PLUS_BTN_SIZE &&
                        mouseWorldPos.y >= SUN_PLUS_Y && mouseWorldPos.y <= SUN_PLUS_Y + PLUS_BTN_SIZE) {
                        gamePlay.cheatAddSun(100);
                        return true;
                    }
                    if (mouseWorldPos.x >= PF_PLUS_X && mouseWorldPos.x <= PF_PLUS_X + PLUS_BTN_SIZE &&
                        mouseWorldPos.y >= PF_PLUS_Y && mouseWorldPos.y <= PF_PLUS_Y + PLUS_BTN_SIZE) {
                        gamePlay.addPlantFood();
                        return true;
                    }
                }

                if (gamePlay.tryCollectSunByClick(mouseWorldPos.x, mouseWorldPos.y)) {
                    return true;
                }


                if (mouseWorldPos.x >= SHOVEL_BTN_X && mouseWorldPos.x <= SHOVEL_BTN_X + SHOVEL_BTN_SIZE &&
                    mouseWorldPos.y >= SHOVEL_BTN_Y && mouseWorldPos.y <= SHOVEL_BTN_Y + SHOVEL_BTN_SIZE) {
                    isShovelSelected = !isShovelSelected;
                    if (isShovelSelected) {
                        selectedPlant = null;
                        isPlantFoodSelected = false;
                    }
                    return true;
                }


                if (mouseWorldPos.x >= PF_BTN_X && mouseWorldPos.x <= PF_BTN_X + PF_BTN_SIZE &&
                    mouseWorldPos.y >= PF_BTN_Y && mouseWorldPos.y <= PF_BTN_Y + PF_BTN_SIZE) {
                    if (gamePlay.getNumOfPlantFood() > 0) {
                        isPlantFoodSelected = !isPlantFoodSelected;
                        if (isPlantFoodSelected) {
                            selectedPlant = null;
                            isShovelSelected = false;
                        }
                    } else {
                        showError("No Plant Food available!");
                    }
                    return true;
                }


                if (gamePlay instanceof PlantWhatYouGet) {
                    PlantWhatYouGet pwyb = (PlantWhatYouGet) gamePlay;
                    if (!pwyb.isWaveStarted()) {
                        if (mouseWorldPos.x >= START_WAVE_BTN_X && mouseWorldPos.x <= START_WAVE_BTN_X + START_WAVE_BTN_W &&
                            mouseWorldPos.y >= START_WAVE_BTN_Y && mouseWorldPos.y <= START_WAVE_BTN_Y + START_WAVE_BTN_H) {
                            pwyb.startWave();
                            return true;
                        }
                    }
                }


                ArrayList<BattlePlant> deck = gamePlay.getPlants();
                for (int i = 0; i < deck.size(); i++) {
                    float cardX = CARD_X;
                    float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));

                    if (mouseWorldPos.x >= cardX && mouseWorldPos.x <= cardX + CARD_WIDTH &&
                        mouseWorldPos.y >= cardY && mouseWorldPos.y <= cardY + CARD_HEIGHT) {

                        BattlePlant clickedPlant = deck.get(i);
                        boolean canAfford = gamePlay.getMySuns() >= clickedPlant.getPlantStats().getCost();

                        boolean isSetupPhase = (gamePlay instanceof PlantWhatYouGet && !((PlantWhatYouGet) gamePlay).isWaveStarted());
                        boolean isReady = isSetupPhase || (clickedPlant.getCurrentCoolDown() <= 0 || !clickedPlant.getActiveCooldown());

                        if (canAfford && isReady) {
                            isShovelSelected = false;
                            isPlantFoodSelected = false;
                            selectedPlant = (selectedPlant == clickedPlant) ? null : clickedPlant;
                        } else if (!canAfford) {
                            showError("Not enough sun!");
                        } else if (!isReady) {
                            showError("Plant is recharging!");
                        }
                        return true;
                    }
                }


                int col = (int) Math.floor((mouseWorldPos.x - 490) / 152.2) + 1;
                int row = (int) Math.floor((mouseWorldPos.y - 130) / 150) + 1;

                if (col >= 1 && col <= 9 && row >= 1 && row <= 5) {

                    if (isShovelSelected) {
                        gamePlay.plucking(new Position(col, row));
                        isShovelSelected = false;
                        return true;
                    }

                    if (isPlantFoodSelected) {
                        Tile tile = gamePlay.getTileByPosition(col, row);
                        if (tile != null && !tile.getPlants().isEmpty()) {
                            if (gamePlay.usePlantFood(col, row)) {
                                isPlantFoodSelected = false;
                            }
                        } else {
                            showError("No plant on this tile!");
                        }
                        return true;
                    }

                    if (selectedPlant != null) {
                        gamePlay.planting(selectedPlant, new Position(col, row));
                        selectedPlant = null;
                        return true;
                    }
                }

                return false;
            }
        };

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(gameInputAdapter);
        Gdx.input.setInputProcessor(multiplexer);

        showObjectivesModal();
    }

    // ==========================================================
    // OBJECTIVES MODAL (beginning of level, before any dialog)
    // ==========================================================
    private void showObjectivesModal() {
        gamePlay.isPaused = true;

        BorderedTable box = new BorderedTable();
        box.pad(30);

        Label title = createModalLabel("Level Objective", Color.BLACK);
        title.setFontScale(1.15f);
        box.add(title).padBottom(16).row();

        String objectives = gamePlay.getLevelObjectives();
        Label objectiveLabel = createModalLabel(objectives != null ? objectives : "", Color.BLACK);
        objectiveLabel.setWrap(true);
        objectiveLabel.setAlignment(Align.center);
        box.add(objectiveLabel).width(440).padBottom(20).row();

        Label hint = createModalLabel("(tap anywhere to continue)", Color.DARK_GRAY);
        hint.setFontScale(0.8f);
        box.add(hint);

        // Clicking anywhere (scrim or the box itself) dismisses this modal
        // and resumes the game -- unless there's an intro dialogue waiting,
        // in which case dismissing just reveals it and it stays paused
        // until the dialogue itself finishes (existing click-to-advance
        // logic in the gameplay input adapter handles that).
        showModal(box, () -> {
            if (introCutscene == null || introCutscene.isFinished()) {
                gamePlay.isPaused = false;
            }
        });
    }

    // ==========================================================
    // END OF GAME MODAL (win or loss)
    // ==========================================================
    private void showEndGameModal() {
        endModalShown = true;
        gamePlay.isPaused = true;

        boolean won = gamePlay.hasWon();

        BorderedTable box = new BorderedTable();
        box.pad(30);

        Label title = createModalLabel(won ? "Congratulations!" : "You Lost!", Color.BLACK);
        title.setFontScale(1.25f);
        box.add(title).colspan(2).padBottom(18).row();

        Label message = createModalLabel(
            won ? "You beat the level! Great job!" : "The zombies got through. Better luck next time!",
            Color.BLACK
        );
        message.setWrap(true);
        message.setAlignment(Align.center);
        box.add(message).width(420).colspan(2).padBottom(24).row();

        TextButton exitButton = createModalButton("Exit", () ->
            MenuManager.getInstance().changeMenu(MenuType.Game)
        );

        if (won) {
            box.add(exitButton).colspan(2);
        } else {
            TextButton tryAgainButton = createModalButton("Try Again", () -> {
                int level = gamePlay.getLevel();
                MenuManager.getInstance().getGameMenu().startGame(level);
            });

            Table buttonRow = new Table();
            buttonRow.add(tryAgainButton).padRight(14);
            buttonRow.add(exitButton);
            box.add(buttonRow).colspan(2);
        }

        // No click-anywhere dismissal here -- only the buttons above
        // should close this modal.
        showModal(box, null);
    }

    // ==========================================================
    // Shared modal plumbing (self-contained since GamePlayScreen does
    // not extend AbstractScreen and therefore has no Stage of its own
    // otherwise).
    // ==========================================================
    private Texture modalScrimTexture;

    private void showModal(Table content, Runnable onDismissAnywhere) {
        modalStack.clearChildren();

        if (modalScrimTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(0f, 0f, 0f, 0.6f);
            pixmap.fill();
            modalScrimTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        Image scrim = new Image(new TextureRegionDrawable(new TextureRegion(modalScrimTexture)));
        scrim.setFillParent(true);
        modalStack.addActor(scrim);

        Table centerWrapper = new Table();
        centerWrapper.setFillParent(true);
        centerWrapper.add(content);
        modalStack.addActor(centerWrapper);

        if (onDismissAnywhere != null) {
            modalStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    closeModal();
                    onDismissAnywhere.run();
                }
            });
        }
    }

    private void closeModal() {
        modalStack.clearChildren();
    }

    private Label createModalLabel(String text, Color color) {
        BitmapFont font = skin.get("FBUSV8C5EI_2", BitmapFont.class);
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        style.fontColor = color;
        return new Label(text, style);
    }

    private TextButton createModalButton(String text, Runnable onClick) {
        TextButton button = new TextButton(text, skin, "green");
        button.pad(10, 22, 10, 22);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
        });
        return button;
    }

    @Override
    public void render(float delta) {
        int gameSpeed = 1;
        User user = gamePlay.getThisUser();
        if (user != null && user.getUserProgress() != null) {
            gameSpeed = Math.max(1, Math.min(3, user.getUserProgress().getGameSpeed()));
        }
        float effectiveDelta = delta * gameSpeed;

        stateTime += effectiveDelta;
        gamePlay.setTotalTimePassed(stateTime);
        textureBank.update();

        ScreenUtils.clear(0.1f, 0.4f, 0.1f, 1);

        if (!gamePlay.isPaused()) {
            timeAccumulator += effectiveDelta;
            while (timeAccumulator >= TICK_RATE) {
                gamePlay.update();
                timeAccumulator -= TICK_RATE;
            }

            if (!endModalShown && gamePlay.isGameOver()) {
                showEndGameModal();
            }

            for (Mower mower : gamePlay.getMowers()) {
                mower.update(effectiveDelta, gamePlay);
            }

            for (Sun sun : gamePlay.getActiveSuns()) {
                sun.update(effectiveDelta);
            }
        }

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(region, 0, 0, 1920, 1200);


        if (gamePlay instanceof SaveOurSeeds){
            int[][] protectedCoords = {{5, 2}, {5, 4}};
            float pulse = 0.75f + 0.25f * (float) Math.sin(stateTime * 5f);

            for (int[] coord : protectedCoords) {
                float realX = gamePlay.getRealX(coord[0]);
                float realY = gamePlay.getRealY(coord[1]);
                float tileW = 145f;
                float tileH = 140f;

                if (sosTileRegion != null) {
                    batch.setColor(1f, 0.9f, 0.2f, pulse);
                    batch.draw(sosTileRegion, realX - (tileW / 2f) - 7, realY - 60f, tileW, tileH);
                    batch.setColor(Color.WHITE);
                }
            }
        }

        if (gamePlay.getChapterType() == ChapterType.ANCIENT_EGYPT) {
            for (Tile tile : gamePlay.getTiles()) {
                if (!tile.isArable() && tile.getHP() > 0) {
                    int gridX = (int) tile.getPosition().getX();
                    int gridY = (int) tile.getPosition().getY();

                    float realX = gamePlay.getRealX(gridX);
                    float realY = gamePlay.getRealY(gridY);

                    float hpRatio = Math.min(1.0f, tile.getHP() / GRAVE_MAX_HP);
                    int stageIndex = 4 - (int) Math.min(4, Math.floor(hpRatio * 5));

                    TextureRegion graveTexture = egyptGraveRegions[stageIndex];
                    if (graveTexture != null) {
                        float graveW = 115f;
                        float graveH = 145f;
                        batch.draw(graveTexture, realX - (graveW / 2f) - 7, realY - 30f, graveW, graveH);
                    }
                }
            }
        }
        else if (gamePlay.getChapterType() == ChapterType.FROSTBITE_CAVES) {
            for (Tile tile : gamePlay.getTiles()) {
                if (!tile.isArable()) {
                    int gridX = (int) tile.getPosition().getX();
                    int gridY = (int) tile.getPosition().getY();

                    float realX = gamePlay.getRealX(gridX);
                    float realY = gamePlay.getRealY(gridY);

                    float tileW = 145f;
                    float tileH = 140f;

                    if (tile.getHP() == 0 && iceSliderRegion != null) {
                        batch.draw(iceSliderRegion, realX - (tileW / 2f) , realY - 47f, tileW, tileH);
                    }
                    else if (tile.getHP() > 0 && iceBlockTexture != null) {
                        batch.draw(iceBlockTexture, realX - (tileW / 2f) , realY - 25f, 130f, 155f);
                    }
                }
            }
        }
        else if (gamePlay.getChapterType() == ChapterType.DARK_AGE) {
            for (Tile tile : gamePlay.getTiles()) {
                if (!tile.isArable() && tile.getHP() > 0) {
                    int gridX = (int) tile.getPosition().getX();
                    int gridY = (int) tile.getPosition().getY();
                    float realX = gamePlay.getRealX(gridX);
                    float realY = gamePlay.getRealY(gridY);

                    if (tile.isNecromancy() && !tile.isNecromancyTriggered()) {
                        if (necromancyRuneRegion != null) {
                            batch.draw(necromancyRuneRegion, realX - 60f, realY - 50f, 120f, 60f);
                        }
                    }

                    float hpRatio = Math.max(0f, Math.min(1.0f, (float) tile.getHP() / GRAVE_MAX_HP));
                    int stageIndex = 4 - (int) Math.min(4, Math.floor(hpRatio * 4.99f));

                    TextureRegion graveTex = switch (tile.getGraveType()) {
                        case PLANT_FOOD -> darkPlantFoodGraveRegions[stageIndex];
                        case SUN -> darkSunGraveRegions[stageIndex];
                        default -> darkNormalGraveRegions[stageIndex];
                    };

                    if (graveTex != null) {
                        batch.draw(graveTex, realX - 60f - 7f, realY - 30f, 115f, 145f);
                    }
                }
            }
        }
        if (gamePlay.getChapterType() == ChapterType.BIG_WAVE_BEACH) {
            for (Tile tile : gamePlay.getTiles()) {
                if (!tile.isArable() && tile.isLowTide() && !tile.isLowTideTriggered()) {
                    int gridX = (int) tile.getPosition().getX();
                    int gridY = (int) tile.getPosition().getY();
                    float realX = gamePlay.getRealX(gridX);
                    float realY = gamePlay.getRealY(gridY);

                    if (lowTideRuneRegion != null) {
                        float pulse = 0.6f + 0.4f * (float) Math.sin(stateTime * 4f);
                        batch.setColor(0.1f, 0.7f, 1f, pulse);
                        batch.draw(lowTideRuneRegion, realX - 60f, realY - 45f, 120f, 60f);
                        batch.setColor(Color.WHITE);
                    }
                }
            }

            float waterOffset = (float) Math.sin(stateTime * 0.8f) * WATER_MOVE_RANGE;
            float currentWaterX = WATER_BASE_X + waterOffset;

            player.draw(batch, BEACH_WATER_ANIM_PATH, "water", stateTime, currentWaterX, WATER_BASE_Y, true);

            player.draw(batch, BEACH_TIDELINE_ANIM_PATH, "idle", stateTime, TIDELINE_X, TIDELINE_Y, true);
        }

        for (BattlePlant p : gamePlay.getGamePlants()) {
            if (p.isAlive() && p.getPosition() != null && p.getPlantStats().getAnimation() != null) {
                float drawX = (float) p.getPosition().getX();
                float drawY = (float) p.getPosition().getY();

                int iceStage = 0;
                if (p.isFrozen() || p.getIceTime() >= 3) {
                    iceStage = 3;
                } else if (p.getIceTime() == 2) {
                    iceStage = 2;
                } else if (p.getIceTime() == 1) {
                    iceStage = 1;
                }

                if (iceStage > 0) {
                    batch.setColor(0.65f, 0.85f, 1.0f, 1.0f);
                } else {
                    batch.setColor(Color.WHITE);
                }

                player.draw(batch, p.getPlantStats().getAnimation(), p.getCurrentAnimationName(),
                    stateTime, drawX, drawY, true, p.getVisibilities());

                batch.setColor(Color.WHITE);

                if (iceStage > 0) {
                    TextureRegion iceTex = iceStageRegions[iceStage - 1];
                    if (iceTex != null) {
                        float iceW = (iceStage == 3) ? 140f : 120f;
                        float iceH = (iceStage == 3) ? 160f : 135f;
                        float offsetX = iceW / 2f;
                        float offsetY = 50f;

                        batch.draw(iceTex, drawX - offsetX - 2f, drawY - offsetY, iceW, iceH);
                    }
                }
            }
        }

        for (DroppedPlantFood pf : gamePlay.getActivePlantFoods()) {
            float x = (float) pf.getPosition().getX();
            float y = (float) pf.getPosition().getY();

            float floatOffset = (float) Math.sin(stateTime * 5f) * 7f;

            batch.draw(getPlantFoodIconInGame, x, y + floatOffset, 65, 65);
        }

        for (Zombie z : gamePlay.getGameZombies()) {
            if (z.isAlive() && z.getZombieStats().getAnimation() != null) {
                float drawX = (float) z.getPosition().getX();
                float drawY = (float) z.getPosition().getY();

                if (z.isHalated()) {
                    float pulse = 0.75f + 0.25f * (float) Math.sin(stateTime * 7f);
                    batch.setColor(0.35f * pulse, 1.0f, 0.45f * pulse, 1.0f);
                } else {
                    batch.setColor(Color.WHITE);
                }

                player.draw(batch, z.getZombieStats().getAnimation(), z.getCurrentAnimationName(),
                    stateTime, drawX, drawY, true, z.getVisibility());

                batch.setColor(Color.WHITE);
            }
        }

        for (Sun sun : gamePlay.getActiveSuns()) {
            if (!sun.isCollected()) {
                float x = (float) sun.getPosition().getX() + 40;
                float y = (float) sun.getPosition().getY() + 40;

                if (sun.getNumberOfSun() >= 100) {
                    float scale = 1.35f;
                    batch.setTransformMatrix(batch.getTransformMatrix().idt()
                        .translate(x, y, 0)
                        .scale(scale, scale, 1)
                        .translate(-x, -y, 0));
                    player.draw(batch, sun.getAnimationPath(), "animation", stateTime, x, y, true);
                    batch.setTransformMatrix(batch.getTransformMatrix().idt());
                } else {
                    player.draw(batch, sun.getAnimationPath(), "animation", stateTime, x, y, true);
                }
            }
        }

        for (Zombie zombie : gamePlay.getGameZombies()) {
            if (zombie.isAlive()) {
                float px = (float) zombie.getPosition().getX();
                float py = (float) zombie.getPosition().getY();
                batch.setColor(zombie.getColor());
                player.draw(batch, zombie.getZombieStats().getAnimation(), zombie.getCurrentAnimationName(),
                    stateTime, px, py, true, zombie.getVisibility());
                batch.setColor(Color.WHITE);
            }
        }

        for (Mower mower : gamePlay.getMowers()) {
            if (!mower.isDone()) {
                player.draw(batch, mower.getAnimationPath(), mower.getCurrentAnimState(),
                    stateTime, mower.getX(), mower.getY(), true);
            }
        }

        Iterator<SandstormEffect> it = gamePlay.getActiveSandstorms().iterator();
        while (it.hasNext()) {
            SandstormEffect storm = it.next();
            storm.update(delta);

            if (storm.isFinished()) {
                it.remove();
            } else {
                player.draw(
                    batch,
                    SANDSTORM_ANIM_PATH,
                    "loop",
                    storm.getAnimTime(),
                    storm.getX(),
                    storm.getY() + 40,
                    true
                );
            }
        }

        if (gamePlay.getChapterType() == ChapterType.FROSTBITE_CAVES) {
            Iterator<IcyWindEffect> windIt = gamePlay.getActiveIcyWinds().iterator();
            while (windIt.hasNext()) {
                IcyWindEffect wind = windIt.next();
                wind.update(delta);

                if (wind.isFinished()) {
                    windIt.remove();
                } else {
                    float rowY = gamePlay.getRealY(wind.getRow());
                    player.draw(
                        batch,
                        ICY_WIND_ANIM_PATH,
                        "animation",
                        wind.getAnimTime(),
                        960f,
                        rowY + 25,
                        true
                    );
                }
            }
        }

        batch.draw(bgHud, 20, 1100, 215, 80);
        batch.draw(sunIcon, 30, 1110, 60, 60);
        hudFont.draw(batch, String.valueOf(gamePlay.getMySuns()), 95, 1160);


        batch.draw(foodBankRegion, 240, 1100, 230, 80);


        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser != null && currentUser.isDebugMode() && plusIcon != null) {
            batch.draw(plusIcon, SUN_PLUS_X, SUN_PLUS_Y, PLUS_BTN_SIZE, PLUS_BTN_SIZE);
            batch.draw(plusIcon, PF_PLUS_X, PF_PLUS_Y, PLUS_BTN_SIZE, PLUS_BTN_SIZE);
        }


        float slotStartX = 325f;
        float slotSpacing = 27f;
        float slotY = 1127f;
        float slotSize = 25f;

        for (int i = 0; i < 5; i++) {
            float sx = slotStartX + (i * slotSpacing);
            if (i < 3) {
                if (i < gamePlay.getNumOfPlantFood()) {

                    if (pfBankSlotRegion != null) {
                        batch.draw(pfBankSlotRegion, sx, slotY, slotSize, slotSize);
                    }
                } else {

                    if (pfBankSlotRegion != null) {
                        batch.setColor(0.3f, 0.3f, 0.3f, 0.45f);
                        batch.draw(pfBankSlotRegion, sx, slotY, slotSize, slotSize);
                        batch.setColor(Color.WHITE);
                    }
                }
            } else {

                if (pfLockedSlotRegion != null) {
                    batch.setColor(0.7f, 0.7f, 0.7f, 0.75f);
                    batch.draw(pfLockedSlotRegion, sx + 3f, slotY + 3f, slotSize - 6f, slotSize - 6f);
                    batch.setColor(Color.WHITE);
                }
            }
        }

        if (plantFoodIcon != null) {
            if (isPlantFoodSelected) {
                batch.setColor(0.6f, 1f, 0.6f, 1f);
            } else if (gamePlay.getNumOfPlantFood() == 0) {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
            } else {
                batch.setColor(Color.WHITE);
            }
            batch.draw(plantFoodIcon, PF_BTN_X, PF_BTN_Y, PF_BTN_SIZE, PF_BTN_SIZE);
            batch.setColor(Color.WHITE);
        }

        ArrayList<BattlePlant> deck = gamePlay.getPlants();
        for (int i = 0; i < deck.size(); i++) {
            BattlePlant p = deck.get(i);
            PlantType pType = PlantType.fromName(p.getName());
            if (pType == null) continue;

            TextureRegion plantIcon = textureBank.region(pType.getIconAssetId());
            float cardX = CARD_X;
            float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));

            boolean canAfford = gamePlay.getMySuns() >= p.getPlantStats().getCost();
            boolean isReady = p.getCurrentCoolDown() <= 0 || !p.getActiveCooldown();

            if (!canAfford || !isReady) {
                batch.setColor(0.4f, 0.4f, 0.4f, 0.85f);
            } else if (selectedPlant == p) {
                batch.setColor(0.6f, 1f, 0.6f, 1f);
            } else {
                batch.setColor(Color.WHITE);
            }

            boolean isBoosted = gamePlay.isPlantBoosted(p.getName());
            TextureRegion currentCardBg = (isBoosted && cardBoostedBgRegion != null)
                ? cardBoostedBgRegion
                : cardBgRegion;

            if (currentCardBg != null) {
                batch.draw(currentCardBg, cardX, cardY, CARD_WIDTH, CARD_HEIGHT);
            }

            if (plantIcon != null) {
                float availW = CARD_WIDTH - 20f;
                float availH = CARD_HEIGHT - 35f;

                float origW = plantIcon.getRegionWidth();
                float origH = plantIcon.getRegionHeight();
                float scale = Math.min(availW / origW, availH / origH);

                float finalW = origW * scale;
                float finalH = origH * scale;
                float plantDrawX = cardX + (CARD_WIDTH - finalW) / 2f;
                float plantDrawY = cardY + 22f + (availH - finalH) / 2f;

                batch.draw(plantIcon, plantDrawX, plantDrawY, finalW, finalH);
            }

            batch.setColor(Color.WHITE);


            hudFont.getData().setScale(0.40f);
            hudFont.draw(batch, String.valueOf(p.getPlantStats().getCost()), cardX + CARD_WIDTH - 42, cardY + 22);
            hudFont.getData().setScale(1f);
        }

        if (shovelIcon != null) {
            if (isShovelSelected) {
                batch.setColor(0.6f, 1f, 0.6f, 1f);
            } else {
                batch.setColor(Color.WHITE);
            }
            batch.draw(shovelIcon, SHOVEL_BTN_X, SHOVEL_BTN_Y, SHOVEL_BTN_SIZE, SHOVEL_BTN_SIZE);
            batch.setColor(Color.WHITE);
        }

        batch.end();

        float barWidth = 450f;
        float barHeight = 45f;
        float barLeftX = (1920f - barWidth) / 2f;
        float barRightX = barLeftX + barWidth;
        float barY = 1130f;

        float progress = gamePlay.getProgressPercentage();
        float headX = barRightX - (barWidth * progress);
        float greenWidth = barRightX - headX;

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (selectedPlant != null || isShovelSelected) {
            int hoverCol = (int) Math.floor((mouseWorldPos.x - 490) / 152.2) + 1;
            int hoverRow = (int) Math.floor((mouseWorldPos.y - 130) / 150) + 1;

            if (hoverCol >= 1 && hoverCol <= 9 && hoverRow >= 1 && hoverRow <= 5) {
                float tileX = 490f + (hoverCol - 1) * 152.2f;
                float tileY = 130f + (hoverRow - 1) * 150f;
                float tileW = 145f;
                float tileH = 140f;

                shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.35f));
                shapeRenderer.rect(tileX - 5, tileY + 5, tileW, tileH);
            }
        }

        if (selectedPlant != null || isShovelSelected || isPlantFoodSelected) {
            int hoverCol = (int) Math.floor((mouseWorldPos.x - 490) / 152.2) + 1;
            int hoverRow = (int) Math.floor((mouseWorldPos.y - 130) / 150) + 1;

            if (hoverCol >= 1 && hoverCol <= 9 && hoverRow >= 1 && hoverRow <= 5) {
                float tileX = 490f + (hoverCol - 1) * 152.2f;
                float tileY = 130f + (hoverRow - 1) * 150f;
                shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.35f));
                shapeRenderer.rect(tileX - 5, tileY + 5, 145f, 140f);
            }
        }

        boolean isSetupPhase = (gamePlay instanceof PlantWhatYouGet && !((PlantWhatYouGet) gamePlay).isWaveStarted());

        if (!isSetupPhase) {
            for (int i = 0; i < deck.size(); i++) {
                BattlePlant p = deck.get(i);
                double cd = p.getCurrentCoolDown();
                double totalCd = p.getPlantStats().getRechargeTime();

                if (cd > 0 && totalCd > 0) {
                    float cdRatio = (float) Math.min(1.0, cd / totalCd);
                    float cardX = CARD_X;
                    float cardY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));
                    float overlayHeight = CARD_HEIGHT * cdRatio;

                    shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.65f));
                    shapeRenderer.rect(cardX, cardY + (CARD_HEIGHT - overlayHeight), CARD_WIDTH, overlayHeight);
                }
            }
        }

        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
        shapeRenderer.rect(barLeftX + 15, barY + 10, barWidth - 30, barHeight - 20);

        if (greenWidth > 0) {
            shapeRenderer.setColor(new Color(0.2f, 0.9f, 0.2f, 1f));
            shapeRenderer.rect(Math.max(headX, barLeftX + 15), barY + 10, greenWidth - 15, barHeight - 20);
        }

        shapeRenderer.end();

        boolean showGrid = (user != null && user.getUserProgress() != null && user.getUserProgress().isShowTileGrid());




        boolean isDeadLineMode = (gamePlay instanceof DeadLine);

        if (showGrid || isDeadLineMode) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);


            if (showGrid) {
                shapeRenderer.setColor(new Color(1f, 0f, 0f, 0.85f));
                Gdx.gl.glLineWidth(2);

                for (int r = 1; r <= 5; r++) {
                    for (int c = 1; c <= 9; c++) {
                        float tileX = 490f + (c - 1) * 152.2f;
                        float tileY = 130f + (r - 1) * 150f;
                        shapeRenderer.rect(tileX - 5, tileY + 5, 145f, 140f);
                    }
                }
            }


            if (isDeadLineMode) {
                float pulse = 0.7f + 0.3f * (float) Math.sin(stateTime * 6f);
                shapeRenderer.setColor(new Color(1f, 0.1f, 0.1f, pulse));
                Gdx.gl.glLineWidth(6);


                shapeRenderer.line(DEADLINE_X, 130f, DEADLINE_X, 130f + (5 * 150f));
            }

            shapeRenderer.end();
        }

        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        batch.begin();
        batch.draw(progressBarFrame, barLeftX, barY, barWidth, barHeight);

        int totalWaves = gamePlay.calculateWaves(gamePlay.getChapterType(), gamePlay.getLevel());
        for (int i = 0; i < totalWaves; i++) {
            float flagProgressPercent = (float) i / totalWaves;
            float flagX = barRightX - (barWidth * flagProgressPercent);

            if (i == totalWaves - 1) {
                batch.draw(flagIcon, flagX - 15, barY + 5, 45, 55);
            } else {
                batch.draw(flagIcon, flagX - 10, barY + 10, 30, 40);
            }
        }

        batch.draw(zombieHeadIcon, headX - 25, barY - 5, 50, 50);

        if (selectedPlant != null && selectedPlant.getPlantStats().getAnimation() != null) {
            String strOfidle = PlantType.fromName(selectedPlant.getName()).getStateName();

            player.draw(batch, selectedPlant.getPlantStats().getAnimation(), strOfidle,
                stateTime, mouseWorldPos.x, mouseWorldPos.y, true);
        }


        if (gamePlay instanceof TimedWar) {
            TimedWar tw = (TimedWar) gamePlay;
            int kills = tw.getNumOfDeadZombies();
            int targetKills = 7;
            float timeLeft = Math.max(0f, (600 - tw.getTotalTicksPassed()) * 0.1f);
            boolean targetReached = kills >= targetKills;

            float boxX = 1450f;
            float boxY = 1100f;

            batch.draw(bgHud, boxX, boxY, 280, 80);
            hudFont.getData().setScale(0.48f);

            if (targetReached) {
                hudFont.setColor(Color.GREEN);
                hudFont.draw(batch, "GOAL ACHIEVED! (" + kills + "/" + targetKills + ")", boxX + 20, boxY + 50);
            } else {
                if (timeLeft <= 10.0f) {
                    hudFont.setColor(Color.RED);
                } else {
                    hudFont.setColor(Color.YELLOW);
                }
                hudFont.draw(batch, "Kills: " + kills + " / " + targetKills, boxX + 25, boxY + 60);
                hudFont.draw(batch, String.format("Time: %.1fs", timeLeft), boxX + 25, boxY + 30);
            }

            hudFont.getData().setScale(1f);
            hudFont.setColor(Color.WHITE);
        }

        if (gamePlay instanceof LoveYourPlants) {
            LoveYourPlants lyp = (LoveYourPlants) gamePlay;
            int lost = lyp.getNumOfLost();
            int maxAllowed = 5;

            float boxX = 1450f;
            float boxY = 1100f;

            batch.draw(bgHud, boxX, boxY, 260, 80);
            hudFont.getData().setScale(0.45f);

            if (lost >= 4) {
                hudFont.setColor(Color.RED);
            } else {
                hudFont.setColor(Color.WHITE);
            }

            hudFont.draw(batch, "Plants Lost: " + lost + " / " + maxAllowed, boxX + 20, boxY + 48);
            hudFont.getData().setScale(1f);
            hudFont.setColor(Color.WHITE);
        }

        if (gamePlay instanceof PlantWhatYouGet) {
            PlantWhatYouGet pwyb = (PlantWhatYouGet) gamePlay;
            if (!pwyb.isWaveStarted()) {
                float pulse = 0.85f + 0.15f * (float) Math.sin(stateTime * 6f);
                batch.setColor(0.3f, 0.9f, 0.3f, pulse);
                batch.draw(bgHud, START_WAVE_BTN_X, START_WAVE_BTN_Y, START_WAVE_BTN_W, START_WAVE_BTN_H);
                batch.setColor(Color.WHITE);

                hudFont.getData().setScale(0.55f);
                hudFont.setColor(Color.YELLOW);
                hudFont.draw(batch, "LET'S ROCK!", START_WAVE_BTN_X + 28, START_WAVE_BTN_Y + 50);
                hudFont.getData().setScale(1f);
                hudFont.setColor(Color.WHITE);
            }
        }

        if (isShovelSelected && shovelIcon != null) {
            batch.draw(shovelIconInGame, mouseWorldPos.x - 40, mouseWorldPos.y - 10, 80, 80);
        }

        if (isPlantFoodSelected && getPlantFoodIconInGame != null) {
            batch.draw(getPlantFoodIconInGame, mouseWorldPos.x - 30, mouseWorldPos.y - 30, 60, 60);
        }

        batch.end();

        if (introCutscene != null && !introCutscene.isFinished()) {
            introCutscene.render(batch, shapeRenderer, player, hudFont, stateTime);
        }

        uiStage.act(delta);
        uiStage.draw();

        UIManager.renderToasts(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(1920 / 2f, 1200 / 2f, 0);
        camera.update();
        if (uiStage != null) {
            uiStage.getViewport().update(width, height, true);
        }
        UIManager.resizeToasts(width, height);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        hudFont.dispose();
        if (uiStage != null) {
            uiStage.dispose();
        }
        if (modalScrimTexture != null) {
            modalScrimTexture.dispose();
        }
    }

    private String getBgPath(ChapterType chapterType) {
        return switch (chapterType) {
            case MINI_GAME -> "IMAGE_BACKGROUNDS_BACKGROUND_LOD_BIGBRAINZ_TEXTURE";
            case ANCIENT_EGYPT -> "IMAGE_BACKGROUNDS_EGYPT_TEXTURE";
            case DARK_AGE -> "IMAGE_BACKGROUNDS_DARK_TEXTURE";
            case FROSTBITE_CAVES -> "IMAGE_BACKGROUNDS_ICEAGE_TEXTURE";
            case BIG_WAVE_BEACH -> "IMAGE_BACKGROUNDS_BEACH_TEXTURE";
        };
    }

    @Override
    public void showCurrentMenu() {

    }

    @Override
    public void showError(String errorMessage) {
        UIManager.showToast(errorMessage, ERROR_BG_ASSET_ID);
    }
}

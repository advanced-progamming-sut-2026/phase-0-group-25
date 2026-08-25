// file: core/src/main/java/com/test1/PlantsVsZombies/src/View/LibGDXViews/GamePlayModals.java
package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.test1.PlantsVsZombies.Main;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import pvz.skin.BorderedTable;

/**
 * Reusable objectives / pause / end-of-game modal system for any screen
 * built around a GamePlay session. Used by GamePlayScreen, VasebreakerScreen,
 * and WallnutBowlingScreen so the three modals only exist in one place.
 *
 * A screen embeds this by:
 *  1. constructing one instance in show(), giving it an onExit and
 *     onRestart action appropriate to that screen (e.g. regular levels
 *     exit to MenuType.Game and restart via GameMenu.startGame(level);
 *     mini-games exit to MenuType.TravelLog and restart via
 *     TravelLogMenu.startMiniGame(name)),
 *  2. putting getStage() first in an InputMultiplexer ahead of the
 *     screen's own gameplay InputAdapter, so a visible modal's scrim
 *     naturally blocks board/game clicks underneath it,
 *  3. calling showObjectivesModal(...) once at the end of show(),
 *  4. calling checkAndMaybeShowEndGameModal() once per frame while the
 *     game is running,
 *  5. wiring a pause button (screen-specific look/position) to call
 *     showPauseModal(),
 *  6. calling getStage().act(delta) / getStage().draw() each frame, and
 *     resize(...)/dispose() from the screen's own resize/dispose.
 */
public class GamePlayModals {

    private final GamePlay gamePlay;
    private final Runnable onExit;
    private final Runnable onRestart;

    private final Stage stage;
    private final Skin skin;
    private final Stack modalStack;
    private Texture scrimTexture;

    private boolean endModalShown = false;

    public GamePlayModals(GamePlay gamePlay, Runnable onExit, Runnable onRestart) {
        this.gamePlay = gamePlay;
        this.onExit = onExit;
        this.onRestart = onRestart;

        this.skin = Main.getInstance().getSkin();
        this.stage = new Stage(new ScreenViewport());
        this.modalStack = new Stack();
        this.modalStack.setFillParent(true);
        this.stage.addActor(modalStack);
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isEndModalShown() {
        return endModalShown;
    }

    // ==========================================================
    // OBJECTIVES MODAL (beginning of level, before any dialog)
    // ==========================================================

    /**
     * @param afterDismiss what to do once the player dismisses the modal
     *                     (click anywhere). Pass null to just resume the
     *                     game immediately; pass a custom Runnable if the
     *                     screen needs extra logic first (e.g.
     *                     GamePlayScreen only resumes if there's no intro
     *                     dialogue still waiting to play).
     */
    public void showObjectivesModal(Runnable afterDismiss) {
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

        showModal(box, afterDismiss != null ? afterDismiss : () -> gamePlay.isPaused = false);
    }

    // ==========================================================
    // PAUSE MODAL
    // ==========================================================
    public void showPauseModal() {
        gamePlay.isPaused = true;

        BorderedTable box = new BorderedTable();
        box.pad(35, 45, 35, 45);

        Label title = createModalLabel("Game Paused", Color.BLACK);
        title.setFontScale(1.35f);
        box.add(title).colspan(3).padBottom(28).row();

        TextButton resumeButton = createModalButton("Resume", () -> {
            closeModal();
            gamePlay.isPaused = false;
        });

        TextButton restartButton = createModalButton("Restart", () -> {
            closeModal();
            if (onRestart != null) onRestart.run();
        });

        TextButton exitButton = createModalButton("Exit", () -> {
            closeModal();
            if (onExit != null) onExit.run();
        });

        Table buttonRow = new Table();
        buttonRow.add(resumeButton).padRight(16);
        buttonRow.add(restartButton).padRight(16);
        buttonRow.add(exitButton);
        box.add(buttonRow).colspan(3);

        showModal(box, null);
    }

    // ==========================================================
    // END OF GAME MODAL (win or loss)
    // ==========================================================

    /**
     * Call once per frame while the game is running (i.e. while ticking
     * happens). Shows the end-of-game modal exactly once, the first frame
     * gamePlay.isGameOver() becomes true.
     */
    public void checkAndMaybeShowEndGameModal() {
        if (!endModalShown && gamePlay.isGameOver()) {
            showEndGameModal();
        }
    }

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

        TextButton exitButton = createModalButton("Exit", () -> {
            if (onExit != null) onExit.run();
        });

        if (won) {
            box.add(exitButton).colspan(2);
        } else {
            TextButton tryAgainButton = createModalButton("Try Again", () -> {
                if (onRestart != null) onRestart.run();
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
    // Shared modal plumbing
    // ==========================================================

    private void showModal(Table content, Runnable onDismissAnywhere) {
        modalStack.clearChildren();
        modalStack.clearListeners();

        if (scrimTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(0f, 0f, 0f, 0.6f);
            pixmap.fill();
            scrimTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        Image scrim = new Image(new TextureRegionDrawable(new TextureRegion(scrimTexture)));
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
        modalStack.clearListeners();
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

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        if (scrimTexture != null) {
            scrimTexture.dispose();
        }
    }
}

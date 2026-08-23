package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;

import java.util.ArrayList;
import java.util.List;

public class IntroDialogueCutscene {
    public static class DialogueLine {
        public final String speakerName;
        public final String text;
        public final boolean isLeftSpeaker;

        public DialogueLine(String speakerName, String text, boolean isLeftSpeaker) {
            this.speakerName = speakerName;
            this.text = text;
            this.isLeftSpeaker = isLeftSpeaker;
        }
    }

    private final List<DialogueLine> lines = new ArrayList<>();
    private int currentIndex = 0;
    private boolean finished = false;


    private static final String DAVE_ANIM_PATH = "768/INITIAL/CRAZYDAVE/CRAZYDAVE/CRAZYDAVE.PAM";
    private static final String WINNIE_ANIM_PATH = "768/INITIAL/WINNIE/WINNIE/WINNIE.PAM";

    private TextureRegion dialogBoxRegion;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    public IntroDialogueCutscene(TextureBank textureBank) {
        if (textureBank != null) {
            dialogBoxRegion = textureBank.region("IMAGE_UI_MAINMENU_MM_SETTINGS_TAB");
        }


        lines.add(new DialogueLine("CRAZY DAVE", "Greetings, neighbor! Welcome to Ancient Egypt!\nWatch out for those hungry mummies!", true));
        lines.add(new DialogueLine("WINNIE", "User, sensors detect incoming zombie activity.\nPlant Sunflowers and form your defense line!", false));
        lines.add(new DialogueLine("CRAZY DAVE", "Because I'm CRAAAAZY! Let's rock and roll!\nRAWWRGHL!", true));
    }

    public void advance() {
        if (finished) return;
        currentIndex++;
        if (currentIndex >= lines.size()) {
            finished = true;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, PamPlayer player, BitmapFont font, float stateTime) {
        if (finished || lines.isEmpty()) return;

        DialogueLine currentLine = lines.get(currentIndex);


        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.55f));
        shapeRenderer.rect(0, 0, 1920, 1200);


        float boxX = 460f;
        float boxY = 80f;
        float boxW = 1000f;
        float boxH = 220f;

        shapeRenderer.setColor(new Color(0.12f, 0.08f, 0.05f, 0.9f));
        shapeRenderer.rect(boxX, boxY, boxW, boxH);
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);


        batch.begin();

        if (dialogBoxRegion != null) {
            batch.draw(dialogBoxRegion, boxX - 10, boxY , boxW + 20, boxH + 20);
        }

        if (currentLine.isLeftSpeaker) {
            batch.setColor(Color.WHITE);
        } else {
            batch.setColor(0.45f, 0.45f, 0.45f, 1f);
        }
        player.draw(batch, DAVE_ANIM_PATH, "anim_idle", stateTime, 280f, 320f, true);


        if (!currentLine.isLeftSpeaker) {
            batch.setColor(Color.WHITE);
        } else {
            batch.setColor(0.45f, 0.45f, 0.45f, 1f);
        }
        player.draw(batch, WINNIE_ANIM_PATH, "anim_idle", stateTime, 1640f, 320f, true);

        batch.setColor(Color.WHITE);

        font.getData().setScale(0.85f);
        font.setColor(currentLine.isLeftSpeaker ? new Color(1f, 0.85f, 0.2f, 1f) : new Color(0.3f, 0.9f, 1f, 1f));
        font.draw(batch, currentLine.speakerName, boxX + 40, boxY + boxH - 25);


        font.getData().setScale(0.60f);
        font.setColor(Color.WHITE);
        glyphLayout.setText(font, currentLine.text, Color.WHITE, boxW - 80, Align.left, true);
        font.draw(batch, glyphLayout, boxX + 40, boxY + boxH - 75);


        float pulseAlpha = 0.5f + 0.5f * (float) Math.sin(stateTime * 5f);
        font.getData().setScale(0.45f);
        font.setColor(1f, 1f, 1f, pulseAlpha);
        font.draw(batch, "[ Click anywhere to continue ]", boxX + boxW - 600, boxY + 37);

        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
        batch.setColor(Color.WHITE);
        batch.end();
    }
}

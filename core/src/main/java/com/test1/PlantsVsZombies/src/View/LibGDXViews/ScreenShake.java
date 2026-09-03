package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class ScreenShake {
    private static final float BASE_X = 1920f / 2f;
    private static final float BASE_Y = 1200f / 2f;
    private static float timeRemaining = 0f;
    private static float totalDuration = 0f;
    private static float intensity = 0f;

    public static void shake(float duration, float power) {
        timeRemaining = duration;
        totalDuration = duration;
        intensity = power;
    }


    public static void update(float delta, OrthographicCamera camera) {
        if (timeRemaining > 0) {
            timeRemaining -= delta;

            float currentPower = intensity * (timeRemaining / totalDuration);
            float offsetX = MathUtils.random(-currentPower, currentPower);
            float offsetY = MathUtils.random(-currentPower, currentPower);

            camera.position.set(BASE_X + offsetX, BASE_Y + offsetY, 0);

            if (timeRemaining <= 0) {
                camera.position.set(BASE_X, BASE_Y, 0);
            }
        } else {
            camera.position.set(BASE_X, BASE_Y, 0);
        }
    }
}

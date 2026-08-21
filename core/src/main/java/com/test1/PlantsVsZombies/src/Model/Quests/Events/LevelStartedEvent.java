package com.test1.PlantsVsZombies.src.Model.Quests.Events;

/**
 * Fired when a level (chapter level or mini-game) starts. Exists so quests
 * whose condition is scoped to a single level -- e.g. "use 3 explosive
 * plants IN ONE level" -- have a boundary to reset their progress on,
 * instead of accumulating across separate, unrelated level attempts.
 *
 * Not every quest needs this: quests phrased as lifetime/cumulative totals
 * (e.g. "kill 50 zombies from a chapter", "kill n zombies with a mower")
 * intentionally do NOT reset here.
 *
 * Where this actually gets fired from gameplay code is someone else's
 * piece of the event-wiring work -- this class just defines the event.
 */
public class LevelStartedEvent extends Event {
}

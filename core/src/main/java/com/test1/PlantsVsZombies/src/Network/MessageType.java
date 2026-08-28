package com.test1.PlantsVsZombies.src.Network;

/**
 * Every kind of message that can travel between a client and the
 * GameServer, in both directions (a client sends a request with one of
 * these types; the server echoes the same type back on its response so
 * the caller doesn't have to guess what it's looking at).
 */
public enum MessageType {

    // ================= Implemented this phase =================
    /** Create a new account. Payload: {"user": User} (already fully built,
     *  including the chosen security question/answer). */
    REGISTER,
    /** Log in with username+password. Payload: {"username", "password"}.
     *  Response payload on success: {"user": User, "sessionToken": String}. */
    LOGIN,
    /** Silently re-establish a session from a previously-issued token
     *  (the "stay logged in" flow). Payload: {"username", "sessionToken"}.
     *  Response payload on success: {"user": User}. */
    RESTORE_SESSION,
    /** Invalidate the current session token server-side.
     *  Payload: {"username", "sessionToken"}. */
    LOGOUT,
    /** Rename the currently logged-in user.
     *  Payload: {"username", "sessionToken", "newUsername"}. */
    CHANGE_USERNAME,
    /** Step 1 of password recovery: verify identity.
     *  Payload: {"username", "email", "answer"}. */
    FORGOT_PASSWORD,
    /** Step 2 of password recovery: actually set the new password.
     *  Payload: {"username", "newPassword"}. */
    RESET_PASSWORD,
    /** Fire-and-forget-from-the-caller's-perspective sync of the full,
     *  current progress for the logged-in user (coins, unlocks, quests,
     *  etc). Payload: {"username", "sessionToken", "user": User}. */
    SAVE_PROGRESS,
    /** Fetch every account's (sanitized -- no password/email/security
     *  answer) data for the leaderboard. Response payload: {"users": List<User>}. */
    GET_ALL_USERS,

    // ========= Reserved for later phases (not yet handled) =========
    // Two-player "I, Zombie" matchmaking:
    JOIN_MATCHMAKING_QUEUE,
    CANCEL_MATCHMAKING,
    CHALLENGE_USER,
    RESPOND_TO_CHALLENGE,
    MATCH_FOUND,
    OPPONENT_GAME_STATE,
    OPPONENT_DISCONNECTED,
    // In-match reactions (text / emoji / moving stickers):
    SEND_REACTION,
    REACTION_RECEIVED
}

package co.casterlabs.caffeinated.pluginsdk.koi;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import co.casterlabs.caffeinated.util.Reflective;
import co.casterlabs.koi.api.KoiChatterType;
import co.casterlabs.koi.api.types.events.KoiEvent;
import co.casterlabs.koi.api.types.events.RoomstateEvent;
import co.casterlabs.koi.api.types.events.StreamStatusEvent;
import co.casterlabs.koi.api.types.events.UserUpdateEvent;
import co.casterlabs.koi.api.types.user.User;
import co.casterlabs.koi.api.types.user.UserPlatform;
import co.casterlabs.rakurai.json.Rson;
import co.casterlabs.rakurai.json.element.JsonObject;
import lombok.Getter;
import lombok.NonNull;

public class Koi {
    private static @Reflective @Getter List<KoiEvent> eventHistory;
    private static @Reflective @Getter Map<UserPlatform, List<User>> viewers;
    private static @Reflective @Getter Map<UserPlatform, UserUpdateEvent> userStates;
    private static @Reflective @Getter Map<UserPlatform, StreamStatusEvent> streamStates;
    private static @Reflective @Getter Map<UserPlatform, RoomstateEvent> roomStates;

    private static @Reflective KoiHandle HANDLE;

    /**
     * @deprecated This is used internally.
     */
    @Deprecated
    public static JsonObject toJson() {
        return new JsonObject()
            .put("history", Rson.DEFAULT.toJson(eventHistory))
            .put("viewers", Rson.DEFAULT.toJson(viewers))
            .put("userStates", Rson.DEFAULT.toJson(userStates))
            .put("streamStates", Rson.DEFAULT.toJson(streamStates))
            .put("roomStates", Rson.DEFAULT.toJson(roomStates));
    }

    public static List<UserPlatform> getSignedInPlatforms() {
        return new LinkedList<>(userStates.keySet());
    }

    public static boolean isMultiUserMode() {
        return userStates.size() > 1;
    }

    public static boolean isSignedOut() {
        return userStates.size() == 0;
    }

    public static int getMaxMessageLength(@NonNull UserPlatform platform) {
        switch (platform) {
            case CAFFEINE:
                return 80;

            case TWITCH:
                return 500;

            case TROVO:
                return 300;

            case GLIMESH:
                return 255;

            case BRIME:
                return 300;

            default:
                return 100; // ?
        }
    }

    public static void sendChat(@NonNull UserPlatform platform, @NonNull String message, @NonNull KoiChatterType chatter) {
        HANDLE.sendChat(platform, message, chatter);
    }

    public static void upvoteChat(@NonNull UserPlatform platform, @NonNull String messageId) {
        HANDLE.upvoteChat(platform, messageId);
    }

    public static void deleteChat(@NonNull UserPlatform platform, @NonNull String messageId) {
        HANDLE.deleteChat(platform, messageId);
    }

    /**
     * It's important to note that this is really only useful outside of multi-user
     * mode.
     * 
     * @throws IndexOutOfBoundsException if no user is signed in.
     */
    public static UserPlatform getFirstSignedInPlatform() {
        return getSignedInPlatforms().get(0);
    }

    @Deprecated
    public static interface KoiHandle {

        public void sendChat(@NonNull UserPlatform platform, @NonNull String message, @NonNull KoiChatterType chatter);

        public void upvoteChat(@NonNull UserPlatform platform, @NonNull String messageId);

        public void deleteChat(@NonNull UserPlatform platform, @NonNull String messageId);

    }

}
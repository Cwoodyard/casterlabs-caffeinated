package co.casterlabs.koi.api.types.events;

import co.casterlabs.rakurai.json.annotating.JsonClass;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonClass(exposeAll = true)
public class UserUpdateEvent extends KoiEvent {
    private String timestamp;

    @Override
    public KoiEventType getType() {
        return KoiEventType.USER_UPDATE;
    }

}
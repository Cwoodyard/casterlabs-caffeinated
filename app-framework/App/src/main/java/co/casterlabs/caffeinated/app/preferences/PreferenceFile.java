package co.casterlabs.caffeinated.app.preferences;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import co.casterlabs.rakurai.json.Rson;
import co.casterlabs.rakurai.json.element.JsonElement;
import co.casterlabs.rakurai.json.serialization.JsonParseException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import xyz.e3ndr.fastloggingframework.logging.FastLogger;
import xyz.e3ndr.fastloggingframework.logging.LogLevel;

@Getter
public class PreferenceFile<T> {
    private static final String userDataDir;

    private FastLogger logger;
    private Class<T> clazz;
    private File file;

    private String name;
    private @Getter(AccessLevel.NONE) T data;

    static {
        AppDirs appDirs = AppDirsFactory.getInstance();
        userDataDir = appDirs.getUserDataDir("casterlabs-caffeinated", null, null, true);

        new File(userDataDir, "preferences").mkdirs();
    }

    /**
     * @param clazz required to deserialize from json.
     */
    @SneakyThrows
    public PreferenceFile(@NonNull String name, @NonNull Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;
        this.logger = new FastLogger(String.format("PreferenceFile (%s)", this.name));
        this.file = new File(userDataDir, String.format("preferences/%s.json", this.name));

        this.data = clazz.newInstance();

        this.load();
    }

    public T get() {
        return this.data;
    }

    public PreferenceFile<T> load() {
        try {
            if (this.file.exists()) {
                String json = new String(Files.readAllBytes(this.file.toPath()), StandardCharsets.UTF_8);
                this.data = Rson.DEFAULT.fromJson(json, this.clazz);
            } else {
                this.logger.info("Preferences file doesn't exist, creating it with defaults.");
                this.save();
            }
        } catch (JsonParseException e) {
            this.logger.warn("Unable to parse preferences file, overwriting with defaults.");
            this.save();
        } catch (IOException e) {
            this.logger.severe("Unable to read preferences file: %s", e);
        }

        return this;
    }

    public PreferenceFile<T> save() {
        try {
            this.file.createNewFile();

            JsonElement json = Rson.DEFAULT.toJson(this.data);

            Files.write(
                this.file.toPath(),
                json
                    .toString(true)
                    .getBytes(StandardCharsets.UTF_8)
            );

            this.logger.log(LogLevel.TRACE, String.format("Saved preferences: %s", json));
        } catch (IOException e) {
            this.logger.severe("Unable to write preferences file: %s", e);
        }

        return this;
    }

}

package org.black_ixx.playerpoints.storage.models;

import de.leonhard.storage.Yaml;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.services.ExecutorModule;
import org.black_ixx.playerpoints.storage.IStorage;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Object that handles points storage from a file config source.
 *
 * @author Mitsugaru
 */
@SuppressWarnings("unused")
public class YAMLStorage implements IStorage {

    /**
     * Plugin reference.
     */
    private final PlayerPoints plugin;

    /**
     * File reference.
     */
    private final File file;

    /**
     * Yaml config.
     */
    private final Yaml config;

    /**
     * Task that saves to disk.
     */
    private final SaveTask saveTask;

    /**
     * Points section string.
     */
    private static final String POINTS_SECTION = "Points.";

    /**
     * Constructor.
     *
     * @param pp - Player points plugin instance.
     */
    public YAMLStorage(final PlayerPoints pp) {
        plugin   = pp;
        file     = new File(plugin.getDataFolder().getAbsolutePath()
                                    + "/storage.yml");
        config   = new Yaml("storage.yml", plugin.getDataFolder().getAbsolutePath());
        saveTask = new SaveTask();
        save();
    }

    /**
     * Save the config data.
     *
     * The save action is done in a separate thread to save the server
     * performance.
     */
    private void save() {
        plugin.getModuleForClass(ExecutorModule.class).submit(saveTask);
    }

    /**
     * Reload the config file.
     */
    public void reload() {
        config.forceReload();
    }

    @Override
    public boolean setPoints(final String id, final int points) {
        config.set(POINTS_SECTION + id, points);
        save();
        return true;
    }

    @Override
    public int getPoints(final String id) {
        final int points = config.getOrSetDefault(POINTS_SECTION + id, 0);
        return points;
    }

    @Override
    public boolean playerEntryExists(final String id) {
        return config.contains(POINTS_SECTION + id);
    }

    @Override
    public boolean removePlayer(final String id) {
        config.set(POINTS_SECTION + id, null);
        save();
        return true;
    }

    @Override
    public Collection<String> getPlayers() {
        Collection<String> players = Collections.emptySet();

        if (config.contains("Points")) {
            players = Objects.requireNonNull(config.keySet("Points"));
        }
        return players;
    }

    @Override
    public boolean destroy() {
        final Collection<String> sections = config.keySet();
        sections.forEach(section -> config.set(section, null));
        save();
        return true;
    }

    @Override
    public boolean build() {
        boolean success = false;
        try {
            success = file.createNewFile();
        }
        catch (final IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create storage file!", e);
        }
        return success;
    }

    private final class SaveTask implements Runnable {
        @Override
        public void run() {
            // Set config
            // Save the file
            config.forceReload();
        }
    }
}

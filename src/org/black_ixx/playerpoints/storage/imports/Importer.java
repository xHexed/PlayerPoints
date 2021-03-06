package org.black_ixx.playerpoints.storage.imports;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.storage.StorageType;

/**
 * Imports data from a source to SQL.
 * 
 * @author Mitsugaru
 */
public class Importer {

    /**
     * Plugin instance.
     */
    private final PlayerPoints plugin;

    /**
     * Contructor.
     *
     * @param plugin - Plugin instance.
     */
    public Importer(final PlayerPoints plugin) {
        this.plugin = plugin;
    }

    /**
     * Check whether we need to import and where we are importing from.
     */
    public void checkImport() {
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (config.importSQL
                && config.getStorageType() == StorageType.MYSQL) {
            importSQL(config.importSource);
            plugin.getConfig().set("mysql.import.use", false);
            plugin.saveConfig();
        }
    }

    /**
     * Imports from SQLite / YAML to MYSQL.
     *
     * @param source - Type of storage to read from.
     */
    private void importSQL(final StorageType source) {
        switch (source) {
            case YAML:
                final YAMLImport yaml = new YAMLImport(plugin);
                yaml.doImport();
                break;
            case SQLITE:
                final SQLiteImport sqlite = new SQLiteImport(plugin);
                sqlite.doImport();
            default:
                break;
        }
    }
}

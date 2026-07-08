package org.cat.shoolpluginmy;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final ShoolPluginMy plugin;
    private Connection connection;
    private boolean enabled;

    public DatabaseManager(ShoolPluginMy plugin) {
        this.plugin = plugin;
    }

    public boolean connect() {
        disconnect();
        FileConfiguration config = plugin.getConfig();
        enabled = config.getBoolean("mysql.enabled", false);
        if (!enabled) {
            return true;
        }

        String host = config.getString("mysql.host", "localhost");
        int port = config.getInt("mysql.port", 3306);
        String database = config.getString("mysql.database", "createhead");
        String username = config.getString("mysql.username", "root");
        String password = config.getString("mysql.password", "");
        boolean useSsl = config.getBoolean("mysql.use-ssl", false);

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=" + useSsl + "&autoReconnect=true&characterEncoding=utf8";

        try {
            connection = DriverManager.getConnection(url, username, password);
            createTable();
            plugin.getLogger().info("MySQL подключение установлено.");
            return true;
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "Не удалось подключиться к MySQL", exception);
            enabled = false;
            return false;
        }
    }

    public void disconnect() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Ошибка при закрытии MySQL соединения", exception);
        } finally {
            connection = null;
        }
    }

    private void createTable() throws SQLException {
        String table = plugin.getConfig().getString("mysql.table", "createhead_data");
        String sql = "CREATE TABLE IF NOT EXISTS `" + table + "` ("
                + "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY,"
                + "`player_name` VARCHAR(16) NOT NULL,"
                + "`count` INT NOT NULL DEFAULT 0"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public boolean isEnabled() {
        return enabled && connection != null;
    }

    public int getCount(UUID uuid) {
        if (!isEnabled()) {
            return 0;
        }

        String table = plugin.getConfig().getString("mysql.table", "createhead_data");
        String sql = "SELECT `count` FROM `" + table + "` WHERE `uuid` = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "Ошибка чтения данных игрока из MySQL", exception);
        }
        return 0;
    }

    public void saveCount(UUID uuid, String playerName, int count) {
        if (!isEnabled()) {
            return;
        }

        String table = plugin.getConfig().getString("mysql.table", "createhead_data");
        String sql = "INSERT INTO `" + table + "` (`uuid`, `player_name`, `count`) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE `player_name` = VALUES(`player_name`), `count` = VALUES(`count`)";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                statement.setString(2, playerName);
                statement.setInt(3, count);
                statement.executeUpdate();
            } catch (SQLException exception) {
                plugin.getLogger().log(Level.SEVERE, "Ошибка сохранения данных игрока в MySQL", exception);
            }
        });
    }
}

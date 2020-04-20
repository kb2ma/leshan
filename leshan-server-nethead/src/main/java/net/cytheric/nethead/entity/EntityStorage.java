/* Copyright 2020 Ken Bannister <kb2ma@runbox.com>
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v3.0. See the file LICENSE-lgpl-v3.0.md in
 * the top level directory for more details.
 */
package net.cytheric.nethead.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides entity storage for the server. An entity is a container for related
 * attributes that models some useful aspect of a device, like a sensor or a
 * data channel.
 *
 * Entity storage is implemented with SQLite.
 */
public class EntityStorage {

    private static final Logger LOG = LoggerFactory.getLogger(EntityStorage.class);

    private String dbPath;

    public EntityStorage(String dbPath) {
        this.dbPath = dbPath;
    }

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }
}

/* Copyright 2020 Ken Bannister <kb2ma@runbox.com>
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v3.0. See the file LICENSE-lgpl-v3.0.md in
 * the top level directory for more details.
 */
 package net.cytheric.nethead.tool;

import java.sql.Connection;
import java.sql.SQLException;

import net.cytheric.nethead.entity.EntityStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides common functionality for a tool used to store an entity.
 */
public abstract class StorageTool {

    private static final Logger LOG = LoggerFactory.getLogger(StorageTool.class);

    protected EntityStorage es;
    protected Connection sqlConn;

    public StorageTool(EntityStorage es) {
        this.es = es;
    }

    // Private methods

    protected Connection getConnection() throws SQLException {
        if (sqlConn == null) {
            sqlConn = es.createConnection();
        }
        return sqlConn;
    }
}

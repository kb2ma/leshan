/* Copyright 2020 Ken Bannister <kb2ma@runbox.com>
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v3.0. See the file LICENSE-lgpl-v3.0.md in
 * the top level directory for more details.
 */
 package net.cytheric.nethead.tool;

import java.sql.ResultSet;
import java.sql.Statement;

import net.cytheric.nethead.entity.Device;
import net.cytheric.nethead.entity.EntityStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages use of entity storage for a device.
 */
public class DeviceTool extends StorageTool {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceTool.class);

    public DeviceTool(EntityStorage es) {
        super(es);
    }

    public Device addDevice(Device d) {
        try {
            Statement statement = getConnection().createStatement();
            int res = statement.executeUpdate("INSERT into device (serial_number) VALUES ('"
                                                + d.getSerialNumber() + "')");

            if (res == 1) {
                return d;
            }
        } catch (Exception e) {
            LOG.error("Error adding device S/N " + d.getSerialNumber() + "; " + e);
        }
        return null;
    }

    public Device findDevice(String serialNumber) {
        try {
            Statement statement = getConnection().createStatement();
            ResultSet rs = statement.executeQuery("select serial_number from device");

            if (rs.next()) {
                Device d = new Device();
                d.setSerialNumber(rs.getString(1));
                return d;
            }
        } catch (Exception e) {
            LOG.error("Error finding device S/N " + serialNumber + "; " + e);
        }
        return null;
    }
}
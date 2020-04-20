/* Copyright 2020 Ken Bannister <kb2ma@runbox.com>
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v3.0. See the file LICENSE-lgpl-v3.0.md in
 * the top level directory for more details.
 */
package net.cytheric.nethead.entity;

/**
 * Device entity
 */
public class Device {

    private String serialNumber;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String toString() {
        return "Device{ sn " + serialNumber + " }";
    }
}

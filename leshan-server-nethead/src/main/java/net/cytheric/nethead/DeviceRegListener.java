/* Copyright 2020 Ken Bannister <kb2ma@runbox.com>
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v3.0. See the file LICENSE-lgpl-v3.0.md in
 * the top level directory for more details.
 */
package net.cytheric.nethead;

import java.util.Collection;

import net.cytheric.nethead.entity.Device;
import net.cytheric.nethead.tool.DeviceTool;

import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers device with Nethead.
 */

public class DeviceRegListener implements RegistrationListener {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceRegListener.class);

    private DeviceTool tool;

    public DeviceRegListener(DeviceTool tool) {
        this.tool = tool;
    }

    @Override
    public void registered(Registration reg, Registration previousReg, 
                           Collection<Observation> previousObservations) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Endpoint [{}]", reg.getEndpoint());
        }
        String sn = reg.getEndpoint();

        Device d = tool.findDevice(sn);
        if (d == null) {
            d = new Device();
            d.setSerialNumber(sn);
            d = tool.addDevice(d);
            if (LOG.isInfoEnabled() && (d != null)) {
                LOG.info("Registered device!");
            }
        }
    }

    @Override
    public void updated(RegistrationUpdate update, Registration updatedRegistration,
            Registration previousRegistration) {
    }

    @Override
    public void unregistered(Registration reg, Collection<Observation> observations, boolean expired,
            Registration newReg) {
    }
}

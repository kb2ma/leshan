/* Copyright 2020 Ken Bannister <kb2ma@runbox.com>
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v3.0. See the file LICENSE-lgpl-v3.0.md in
 * the top level directory for more details.
 */
package net.cytheric.nethead;

import java.util.Collection;

import net.cytheric.nethead.entity.Device;
import net.cytheric.nethead.entity.RegistrationEntity;
import net.cytheric.nethead.tool.DeviceTool;
import net.cytheric.nethead.tool.RegistrationTool;

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

    private DeviceTool deviceTool;
    private RegistrationTool regTool;

    public DeviceRegListener(DeviceTool deviceTool, RegistrationTool regTool) {
        this.deviceTool = deviceTool;
        this.regTool = regTool;
    }

    @Override
    public void registered(Registration reg, Registration previousReg, 
                           Collection<Observation> previousObservations) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Endpoint [{}]", reg.getEndpoint());
        }
        String sn = reg.getEndpoint();

        RegistrationEntity r = regTool.findRegistration(reg.getId());
        if (r == null) {
            r = regTool.addRegistration(reg);
        }

        Device d = deviceTool.findDevice(sn);
        if (d == null) {
            d = new Device();
            d.setSerialNumber(sn);
            d = deviceTool.addDevice(d);
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

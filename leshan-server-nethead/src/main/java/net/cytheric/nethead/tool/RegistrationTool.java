/* Copyright 2020 Ken Bannister <kb2ma@runbox.com>
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v3.0. See the file LICENSE-lgpl-v3.0.md in
 * the top level directory for more details.
 */
 package net.cytheric.nethead.tool;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

import net.cytheric.nethead.entity.Device;
import net.cytheric.nethead.entity.EntityStorage;
import net.cytheric.nethead.entity.RegistrationEntity;

import org.eclipse.leshan.server.registration.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages use of entity storage for a registration.
 */
public class RegistrationTool extends StorageTool {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationTool.class);

    public RegistrationTool(EntityStorage es) {
        super(es);
    }

    public RegistrationEntity addRegistration(Registration reg) {
        try {
            Statement statement = getConnection().createStatement();
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT into registration");
            sb.append(" (reg_id, endpoint, identity, lifetime, links) VALUES ('");
            sb.append(reg.getId());
            sb.append("', '");
            sb.append(reg.getEndpoint());
            sb.append("', '");
            sb.append(reg.getIdentity().getPeerAddress().toString());
            sb.append("', ");
            sb.append(reg.getLifeTimeInSec());
            sb.append(", '");
            sb.append(Arrays.toString(reg.getObjectLinks()));
            sb.append("')");
            if (LOG.isDebugEnabled()) {
                LOG.debug("addRegistration SQL [{}]", sb.toString());
            }
            int res = statement.executeUpdate(sb.toString());

            if (res == 1) {
                RegistrationEntity r = new RegistrationEntity();
                r.setRegId(reg.getId());
                r.setEndpoint(reg.getEndpoint());
                r.setIdentity(reg.getIdentity().getPeerAddress().toString());
                r.setLifetime(reg.getLifeTimeInSec());
                r.setLinks(Arrays.toString(reg.getObjectLinks()));
                return r;
            }
        } catch (Exception e) {
            LOG.error("Error adding registration ID " + reg.getId() + "; " + e);
        }
        return null;
    }

    public RegistrationEntity findRegistration(String regId) {
        try {
            Statement statement = getConnection().createStatement();
            ResultSet rs = statement.executeQuery("select reg_id, endpoint, identity, lifetime, links from registration where reg_id = " + regId);

            if (rs.next()) {
                RegistrationEntity r = new RegistrationEntity();
                r.setRegId(rs.getString(1));
                r.setEndpoint(rs.getString(2));
                r.setIdentity(rs.getString(3));
                r.setLifetime(rs.getLong(4));
                r.setLinks(rs.getString(5));
                return r;
            }
        } catch (Exception e) {
            LOG.error("Error finding registration ID " + regId + "; " + e);
        }
        return null;
    }
}

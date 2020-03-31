/*******************************************************************************
 * Copyright (c) 2020 Ken Bannister. All rights reserved.
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v2.1. See the file LICENSE in the top level
 * directory for more details.
 *******************************************************************************/
package net.cytheric.nethead;

import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes observation data to Graphite as observations are received.
 */
public final class GraphiteWriter implements ObservationListener {

    private static final Logger LOG = LoggerFactory.getLogger(GraphiteWriter.class);

    private final DecimalFormat df = new DecimalFormat("###.##");

    @Override
    public void cancelled(Observation observation) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cancelled observation from [{}]", observation.getPath());
        }
    }

    @Override
    public void onResponse(Observation observation, Registration registration, ObserveResponse response) {
        // assumes response.isSuccess() == true ?
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received notification from [{}] containing value [{}]", observation.getPath(),
                    response.getContent().toString());
        }

        try (
            Socket socket = new Socket("localhost", 2003);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            double value = (Double)((LwM2mSingleResource)response.getContent()).getValue();
            out.println("time.3303.0 " + df.format(value) + " " + (System.currentTimeMillis() / 1000));
        } catch (Exception e) {
            LOG.error("Can't write observation", e);
        }
    }

    @Override
    public void onError(Observation observation, Registration registration, Exception error) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(String.format("Unable to handle notification of [%s:%s]", observation.getRegistrationId(),
                    observation.getPath()), error);
        }
    }

    @Override
    public void newObservation(Observation observation, Registration registration) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received new observation from [{}]", observation.getPath());
        }
    }
}

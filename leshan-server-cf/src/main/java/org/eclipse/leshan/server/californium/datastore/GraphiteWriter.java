/*******************************************************************************
 * Copyright (c) 2020 Ken Bannister. All rights reserved.
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v2.1. See the file LICENSE in the top level
 * directory for more details.
 *******************************************************************************/
package org.eclipse.leshan.server.californium.datastore;

import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.List;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.observation.ObservationService;
import org.eclipse.leshan.server.registration.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes observation data to Graphite.
 *
 * Usage:
 *
 * - Create instance with default constructor.
 *
 * - setDataSource() -- sets the source for observations
 */
public class GraphiteWriter {

    private static final String EVENT_DEREGISTRATION = "DEREGISTRATION";
    private static final String EVENT_UPDATED = "UPDATED";
    private static final String EVENT_REGISTRATION = "REGISTRATION";
    private static final String EVENT_AWAKE = "AWAKE";
    private static final String EVENT_SLEEPING = "SLEEPING";
    private static final String EVENT_NOTIFICATION = "NOTIFICATION";
    private static final String EVENT_COAP_LOG = "COAPLOG";

    private static final Logger LOG = LoggerFactory.getLogger(GraphiteWriter.class);

    private final ObservationListener observationListener = new ObservationListener() {
        // initialize RoundingMode too
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

/*
            if (registration != null) {
                String data = new StringBuilder("{\"ep\":\"").append(registration.getEndpoint()).append("\",\"res\":\"")
                        .append(observation.getPath().toString()).append("\",\"val\":")
                        .append(gson.toJson(response.getContent())).append("}").toString();

                sendEvent(EVENT_NOTIFICATION, data, registration.getEndpoint());
            }
*/
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
    };

    /**
     * Sets the source for observations.
     */
    public void setDataSource(ObservationService obsService) {
        obsService.addListener(observationListener);
    }
}

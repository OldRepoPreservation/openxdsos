/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Contributors:
 *   Apache Software Foundation  - inital API and implementation
 *   Misys Open Source Solutions - modified
 */
package org.openhealthtools.common.ws.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.axis2.transport.http.server.AxisHttpConnection;
import org.apache.axis2.transport.http.server.AxisHttpConnectionImpl;
import org.apache.axis2.transport.http.server.ConnectionListenerFailureHandler;
import org.apache.axis2.transport.http.server.DefaultConnectionListener;
import org.apache.axis2.transport.http.server.HttpConnectionManager;
import org.apache.axis2.transport.http.server.IOProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.params.HttpParams;

public class IheConnectionListener implements IOProcessor {

    private static Log LOG = LogFactory.getLog(IheConnectionListener.class);

    private volatile boolean destroyed = false;

    //private final int port;
    private final HttpConnectionManager connmanager;
    private final ConnectionListenerFailureHandler failureHandler;
    private final HttpParams params;

    private ServerSocket serversocket = null;

    /**
     * Use this constructor to provide a custom ConnectionListenerFailureHandler, e.g. by subclassing DefaultConnectionListenerFailureHandler
     */
    public IheConnectionListener(
            final ServerSocket serversocket,
            final HttpConnectionManager connmanager,
            final ConnectionListenerFailureHandler failureHandler,
            final HttpParams params) throws IOException {
        super();
        if (connmanager == null) {
            throw new IllegalArgumentException("Connection manager may not be null");
        }
        if (failureHandler == null) {
            throw new IllegalArgumentException("Failure handler may not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        this.serversocket = serversocket;
        this.connmanager = connmanager;
        this.failureHandler = failureHandler;
        this.params = params;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                try {
//                  if (serversocket == null || serversocket.isClosed()) {
//                  serversocket = new ServerSocket(port);
//                  serversocket.setReuseAddress(true);
                  if (LOG.isInfoEnabled()) {
                      LOG.info("Listening on port " + this.serversocket.getLocalPort());
                  }
//              }
                  LOG.debug("Waiting for incoming HTTP connection");
                    Socket socket = this.serversocket.accept();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Incoming HTTP connection from " +
                                socket.getRemoteSocketAddress());
                    }
                    AxisHttpConnection conn = new AxisHttpConnectionImpl(socket, this.params);
                    this.connmanager.process(conn);
                } catch(java.io.InterruptedIOException ie) {
                    break; 
                } catch (Throwable ex) {
                    if (Thread.interrupted()) {
                        break;
                    }
                    if (!failureHandler.failed(this, ex)) {
                        break;
                    }
                }
            }
        } finally {
            destroy();
        }
    }

    public void close() throws IOException {
        if (this.serversocket != null) {
            this.serversocket.close();
        }
    }

    public void destroy() {
        this.destroyed = true;
        try {
            close();
        } catch (IOException ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("I/O error closing listener", ex);
            }
        }
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

}

/*
 * Copyright (C) 2012 David Bordoley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package restlib.ext.servlet;

import javax.servlet.http.HttpServletResponse;

import restlib.data.Header;
import restlib.data.Protocol;
import restlib.data.Status;
import restlib.server.connector.ConnectorResponse;

final class ServletConnectorResponse extends ConnectorResponse {
    public static ServletConnectorResponse 
                        newInstance(final HttpServletResponse servletResponse) {
        return new ServletConnectorResponse(servletResponse);
    }
    
    private final HttpServletResponse servletResponse;
    
    private ServletConnectorResponse(final HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }
    
    @Override
    public ConnectorResponse addHeader(final Header header, final Object value) {
        servletResponse.addHeader(header.toString(), value.toString());
        return this;
    }

    @Override
    public ConnectorResponse setStatus(final Status status) {
        servletResponse.setStatus(status.code());
        return this;
    }

    @Override
    public ConnectorResponse setProtocolVersion(final Protocol version) {     
        return this;
    }
}

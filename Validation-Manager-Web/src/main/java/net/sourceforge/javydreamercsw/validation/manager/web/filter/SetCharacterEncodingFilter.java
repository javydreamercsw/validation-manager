/* 
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.filter;

import java.io.IOException;
import javax.servlet.*;

/**
 *
 * @author Leo-Fan.aq
 */
public class SetCharacterEncodingFilter implements Filter {
    // ----------------------------------------------------- Instance Variables

    /**
     * The default character encoding to set for requests that pass through this
     * filter.
     */
    protected String encoding = null;
    /**
     * The filter configuration object we are associated with. If this value is
     * null, this filter instance is not currently configured.
     */
    protected FilterConfig filterConfig = null;
    /**
     * Should a character encoding specified by the client be ignored?
     */
    protected boolean ignore = true;

    // --------------------------------------------------------- Public Methods
    /**
     * Take this filter out of service.
     */
    @Override
    public void destroy() {

        this.encoding = null;
        this.filterConfig = null;

    }

    /**
     * Select and set (if specified) the character encoding to be used to
     * interpret request parameters for this request.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        // Conditionally select and set the character encoding to be used
        if (ignore || (request.getCharacterEncoding() == null)) {
            String tempEncoding = selectEncoding(request);
            if (tempEncoding != null) {
                request.setCharacterEncoding(tempEncoding);
            }
        }

        // Pass control on to the next filter
        chain.doFilter(request, response);
    }

    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;
        this.encoding = filterConfig.getInitParameter("encoding");
        String value = filterConfig.getInitParameter("ignore");
        this.ignore = value == null ? false : value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("yes");
    }

    // ------------------------------------------------------ Protected Methods
    /**
     * Select an appropriate character encoding to be used, based on the
     * characteristics of the current request and/or filter initialization
     * parameters. If no character encoding should be set, return
     * <code>null</code>.
     * <p>
     * The default implementation unconditionally returns the value configured
     * by the <strong>encoding</strong> initialization parameter for this
     * filter.
     *
     * @param request The servlet request we are processing
     * @return encoding.
     */
    protected String selectEncoding(ServletRequest request) {
        return (this.encoding);
    }
}

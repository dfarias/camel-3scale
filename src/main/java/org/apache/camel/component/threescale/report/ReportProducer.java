/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.threescale.report;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ReportResponse;
import threescale.v3.api.ServerError;

public class ReportProducer extends DefaultProducer {

	private static final Logger LOG = LoggerFactory.getLogger(ReportProducer.class);

	private transient String reportProducerToString;

	public ReportProducer(Endpoint endpoint) {
		super(endpoint);
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		ParameterMap params;
		String serviceToken = null;
		String serviceId = null;

		// If no parameter map is defined, then use URI parameters
		if (null == getConfiguration().getParameterMap()) {
			params = new ParameterMap();
			params.add("user_key", getConfiguration().getUserKey());
			serviceToken = getConfiguration().getServiceToken();
			serviceId = getConfiguration().getServiceId();
		} else {
			params = getConfiguration().getParameterMap();
			serviceToken = getConfiguration().getParameterMap().getStringValue("service_token");
			serviceId = getConfiguration().getParameterMap().getStringValue("service_id");
		}

		ParameterMap usage = new ParameterMap();
		usage.add("hits", "1");
		params.add("usage", usage);

		ReportResponse response = null;

		try {

			response = getEndpoint().getServiceApi().report(serviceToken, serviceId, params);

			if (response.success() == true) {

				exchange.getIn().setHeader("THREESCALE_AUTH", "VALID");
				LOG.info("Plan: " + response.toString());

			} else {

				exchange.getIn().setHeader("THREESCALE_AUTH", "INVALID");

				LOG.error("Error Code: " + response.getErrorCode());
				LOG.error("Error Message: " + response.getErrorMessage());
			}

		} catch (ServerError serverError) {
			serverError.printStackTrace();
		}

	}

	protected ReportConfiguration getConfiguration() {
		return getEndpoint().getConfiguration();
	}

	@Override
	public String toString() {
		if (reportProducerToString == null) {
			reportProducerToString = "ReportProducer[" + URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
		}
		return reportProducerToString;
	}

	@Override
	public ReportEndpoint getEndpoint() {
		return (ReportEndpoint) super.getEndpoint();
	}
}
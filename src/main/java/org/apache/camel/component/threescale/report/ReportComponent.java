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

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.threescale.ThreeScaleConfiguration;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportComponent extends DefaultComponent {
	private static final Logger LOG = LoggerFactory.getLogger(ReportComponent.class);

	private ThreeScaleConfiguration configuration;

	public ReportComponent() {
		this(null);
	}

	public ReportComponent(CamelContext context) {
		super(context);
		
		this.configuration = new ThreeScaleConfiguration();
        registerExtension(new ReportComponentVerifierExtension());

	}

	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		
		ThreeScaleConfiguration configuration = getConfiguration().copy();
		setProperties(configuration, parameters);

		if (remaining == null || remaining.trim().length() == 0) {
			throw new IllegalArgumentException("From must be specified.");
		}

		return new ReportEndpoint(uri, this, configuration);
	}

	/**
	 * To use the shared 3scale configuration
	 */
	protected ThreeScaleConfiguration getConfiguration() {
		return configuration;
	}
}
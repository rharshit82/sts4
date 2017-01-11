/*******************************************************************************
 * Copyright (c) 2017 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.commons.cloudfoundry.client.v2;

import java.time.Duration;

public class ClientTimeouts {
	
	public static final ClientTimeouts DEFAULT_TIMEOUTS = new ClientTimeouts();
	
	private static final Duration APP_START_TIMEOUT = Duration.ofMillis(60*10);
	private static final Duration GET_SERVICES_TIMEOUT = Duration.ofSeconds(60);
	private static final Duration GET_SPACES_TIMEOUT = Duration.ofSeconds(20);
	private static final Duration GET_USERNAME_TIMEOUT = Duration.ofSeconds(5);
  
	
	public Duration getServicesTimeout() {
		return GET_SERVICES_TIMEOUT;
	}
	
	public Duration getSpacesTimeout() {
		return GET_SPACES_TIMEOUT;
	}
	
	public Duration getUsernameTimeout() {
		return GET_USERNAME_TIMEOUT;
	}
	
	public Duration getAppStartTimeout() {
		return APP_START_TIMEOUT;
	}
	
	public Duration getBuildpacksTimeout() {
		return Duration.ofSeconds(10);
	}

}

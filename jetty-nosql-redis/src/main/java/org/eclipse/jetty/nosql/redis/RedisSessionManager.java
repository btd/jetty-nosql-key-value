/*
 * Copyright 2014 den.
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

package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.nosql.kvs.KeyValueStoreSessionManager;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 *
 * @author den
 */
public class RedisSessionManager extends KeyValueStoreSessionManager {
    	private final static Logger log = Log.getLogger(RedisSessionManager.class);

	public RedisSessionManager() {
		super();
	}

	@Override
	public void doStart() throws Exception {
		log.info("starting...");
		super.doStart();
		log.info("started.");
	}

	@Override
	public void doStop() throws Exception {
		log.info("stopping...");
		super.doStop();
		log.info("stopped.");
	}
}

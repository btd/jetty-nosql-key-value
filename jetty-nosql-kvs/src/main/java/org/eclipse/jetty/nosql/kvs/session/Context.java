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

package org.eclipse.jetty.nosql.kvs.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author den
 */
public class Context implements Serializable {
   private Map<String, Object> _attributes = new HashMap<String, Object>(); 
   
   private long _version = 1l;

    public Context() {
    }

    public Context(long version) {
        _version = version;
    }

    public Map<String, Object> getAttributes() {
        return _attributes;
    }

    public void setAttributes(Map<String, Object> _attributes) {
        this._attributes = _attributes;
    }

    public long getVersion() {
        return _version;
    }

    public void setVersion(long _version) {
        this._version = _version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Context other = (Context) obj;
        if (!Objects.equals(this._attributes, other._attributes)) {
            return false;
        }
        return this._version == other._version;
    }

    public Object getAttribute(String attribute) {
        return _attributes.get(attribute);
    }
    
    public void setAttribute(String attribute, Object value) {
        _attributes.put(attribute, value);
    }
}

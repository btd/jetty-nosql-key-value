package org.eclipse.jetty.nosql.key_value.session.serializable;

import org.eclipse.jetty.nosql.key_value.session.AbstractSerializableSession;

import java.io.Serializable;


public class NativeSerializableSession extends AbstractSerializableSession implements Serializable {
    private static final long serialVersionUID = 8406865621253286071L;

    public NativeSerializableSession() {
        setCreationTime(System.currentTimeMillis());
        setAccessed(getCreationTime());
    }
}
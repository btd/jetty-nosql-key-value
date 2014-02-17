package org.eclipse.jetty.nosql.kvs.session;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSerializableSession implements ISerializableSession, Serializable {

    private static final long serialVersionUID = -8960779543485104697L;

    public String _id = "";
    public long _created = -1;
    public long _accessed = -1;
    public long _invalidated = -1;
    public int _maxIdle = -1;
    public Map<String, Context> _context = new HashMap<String, Context>();

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public void setId(String id) {
        this._id = id;
    }

    @Override
    public long getCreationTime() {
        return _created;
    }

    @Override
    public void setCreationTime(long created) {
        this._created = created;
    }

    @Override
    public long getAccessed() {
        return _accessed;
    }

    @Override
    public void setAccessed(long accessed) {
        this._accessed = accessed;
    }

    @Override
    public boolean isValid() {
        return _invalidated < 0;
    }

    @Override
    public void setValid(boolean valid) {
        if (valid) {
            this._invalidated = -1L;
        } else {
            this._invalidated = System.currentTimeMillis();
        }
    }

    @Override
    public long getInvalidated() {
        return _invalidated;
    }

    @Override
    public Map<String, Context> getContext() {
        return Collections.unmodifiableMap(_context);
    }

    @Override
    public void setContext(Map<String, Context> _context) {
        this._context = _context;
    }

    @Override
    public void setContextVersion(String context, long version) {
        if(_context.containsKey(context)) {
            _context.get(context).setVersion(version);
        } else {
            _context.put(context, new Context(version));
        }
    }

    @Override
    public long getContextVersion(String context) {
        if(!_context.containsKey(context)) {
            _context.put(context, new Context());
        }
        return _context.get(context).getVersion();
    }

    @Override
    public Object getContextAttribute(String context, String attribute) {
        Context c = _context.get(context);
        if(c != null) {
            return _context.get(context).getAttribute(attribute);
        } else {
            return null;
        }
    }

    @Override
    public void setContextAttribute(String context, String attribute, Object value) {
        if(!_context.containsKey(context)) {
            _context.put(context, new Context());
        }
        _context.get(context).setAttribute(attribute, value);
    }

    @Override
    public Map<String, Object> getContextAttributes(String context) {
        Context c = _context.get(context);
        if(c != null) {
            return Collections.unmodifiableMap(c.getAttributes());
        } else {
            return null;
        }
    }

    @Override
    public boolean removeContext(String context) {
        return _context.remove(context) != null;
    }
    
    @Override
    public int getMaxIdle() {
        return _maxIdle;
    }

    @Override
    public void setMaxIdle(int maxIdle) {
        _maxIdle = maxIdle;
    }

}

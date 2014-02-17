package org.eclipse.jetty.nosql.kvs.session;

import java.util.Enumeration;
import java.util.Map;

public interface ISerializableSession {

    /**
     *
     * @return string form of id
     */
    public String getId();

    /**
     *
     * @param id string form of id
     */
    public void setId(String id);

    /**
     *
     * @return creation time
     */
    public long getCreationTime();

    /**
     *
     * @param created creation time
     */
    public void setCreationTime(long created);

    /**
     *
     * @return last accessed time
     */
    public long getAccessed();

    /**
     *
     * @param accessed last accessed time
     */
    public void setAccessed(long accessed);

    /**
     *
     * @return true if session is valid
     */
    public boolean isValid();

    /**
     *
     * @param valid set true for valid sessions
     */
    public void setValid(boolean valid);

    /**
     *
     * @return invalidated time
     */
    public long getInvalidated();

    /**
     * @return actual context information
     */
    public Map<String, Context> getContext();

    /**
     *
     * @param context actual context
     */
    public void setContext(Map<String, Context> context);

    public void setContextVersion(String context, long version);

    public long getContextVersion(String context);

    public Object getContextAttribute(String context, String attribute);

    public void setContextAttribute(String context, String attribute, Object value);
    
    public Map<String, Object> getContextAttributes(String _contextId);

    /**
     *
     * @return maxIdle value
     */
    public int getMaxIdle();

    public void setMaxIdle(int maxIdle);

    public boolean removeContext(String context);

    
}

package org.eclipse.jetty.nosql.key_value.session.xstream;

import com.thoughtworks.xstream.XStream;
import org.eclipse.jetty.nosql.key_value.session.Serializer;
import org.eclipse.jetty.nosql.key_value.session.SerializationException;

public class XStreamSerializer implements Serializer {
    private XStream xstream = null;
    private ClassLoader classLoader = null;

    public XStreamSerializer() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public XStreamSerializer(ClassLoader cl) {
        xstream = new XStream();
        xstream.setClassLoader(cl);
        classLoader = cl;
    }

    public byte[] encode(Object obj) throws SerializationException {
        byte[] raw = null;
        try {
            raw = xstream.toXML(obj).getBytes("UTF-8");
        } catch (Exception error) {
            throw (new SerializationException(error));
        }
        return raw;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] raw, Class<T> klass) throws SerializationException {
        T obj = null;
        try {
            obj = (T) xstream.fromXML(new String(raw, "UTF-8"));
        } catch (Exception error) {
            throw (new SerializationException(error));
        }
        return obj;
    }
}

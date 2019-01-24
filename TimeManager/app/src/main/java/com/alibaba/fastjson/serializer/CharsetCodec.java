package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;


public class CharsetCodec implements ObjectSerializer, ObjectDeserializer {

    public final static CharsetCodec instance = new CharsetCodec();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object == null) {
            serializer.writeNull();
        } else {
            Charset charset = (Charset) object;
            serializer.write(charset.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        Object value = parser.parse();

        return value == null //
            ? null //
            : (T) Charset.forName((String) value);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}

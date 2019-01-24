package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Currency;

public class CurrencyCodec implements ObjectSerializer, ObjectDeserializer {

    public final static CurrencyCodec instance = new CurrencyCodec();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        final SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else {
            Currency currency = (Currency) object;
            out.writeString(currency.getCurrencyCode());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        String text = (String) parser.parse();

        return (text == null || text.length() == 0) //
            ? null //
            : (T) Currency.getInstance(text);
        
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

}
package com.sahilkumar.dfms.core.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.math.BigDecimal

/**
 * Reads a [BigDecimal] from either a JSON number or a JSON string (the backend
 * may serialize money either way), and writes it back as a numeric value.
 */
class BigDecimalAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): BigDecimal? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull()
        }
        return BigDecimal(reader.nextString())
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: BigDecimal?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(value)
        }
    }
}

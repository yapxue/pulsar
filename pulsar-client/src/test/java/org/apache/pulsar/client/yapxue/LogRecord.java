package org.apache.pulsar.client.yapxue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogRecord {
    private String level;
    private String time;
    private long ts;
    private String clazz;
    private String thread;
    private String message;

    public static LogRecord buildDefault() {
        LogRecord record = new LogRecord();
        record.setTs(System.currentTimeMillis());
        return record;
    }
}

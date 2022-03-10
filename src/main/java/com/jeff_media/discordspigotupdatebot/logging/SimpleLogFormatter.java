package com.jeff_media.discordspigotupdatebot.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleLogFormatter extends Formatter {

    private final Date date = new Date();
    private final static String format = "{0,date} {0,time}";
    private MessageFormat formatter;
    private final Object[] args = new Object[1];
    private final String lineSeparator = System.lineSeparator();

    public synchronized String format(LogRecord record) {

        final StringBuilder sb = new StringBuilder();

        date.setTime(record.getMillis());
        args[0] = date;

        final StringBuffer text = new StringBuffer();
        if (formatter == null) {
            formatter = new MessageFormat(format);
        }
        formatter.format(args, text, null);
        sb.append(text);
        sb.append(" ");


        // Class name
        if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName());
        } else {
            sb.append(record.getLoggerName());
        }

        // Method name
        if (record.getSourceMethodName() != null) {
            sb.append(" ");
            sb.append(record.getSourceMethodName());
        }
        sb.append(" - "); // lineSeparator



        String message = formatMessage(record);

        // Level
        sb.append(record.getLevel().getLocalizedName());
        sb.append(": ");

        int iOffset = (1000 - record.getLevel().intValue()) / 100;
        sb.append(" ".repeat(Math.max(0, iOffset)));


        sb.append(message);
        sb.append(lineSeparator);
        if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw);
        }
        return sb.toString();
    }
}

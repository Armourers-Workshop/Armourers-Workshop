package moe.plushie.armourers_workshop.gametest.init;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Logger {

    private static final List<Message> HISTORY = Collections.synchronizedList(new LinkedList<>());
    private static final List<Consumer<Message>> HANDLERS = Collections.synchronizedList(new LinkedList<>());

    public static List<Message> getHistory() {
        return HISTORY;
    }

    public static void init() {
        var context = (LoggerContext) LogManager.getContext(false);
        var config = context.getConfiguration();

        var appender = new Collector("LogCollector", null, null);
        appender.start();
        config.addAppender(appender);

        config.getRootLogger().addAppender(appender, null, null);
        context.updateLoggers();
    }

    public static void addChangeListener(Consumer<Message> handler) {
        HANDLERS.add(handler);
    }

    public static void removeChangeListener(Consumer<Message> handler) {
        HANDLERS.remove(handler);
    }

    public static class Message {

        private final String logger;
        private final String contents;

        private final Level level;

        public Message(LogEvent event) {
            this.logger = event.getLoggerFqcn();
            this.contents = event.getMessage().getFormattedMessage();
            this.level = event.getLevel();
        }

        public String logger() {
            return logger;
        }

        public String contents() {
            return contents;
        }

        public Level level() {
            return level;
        }
    }

    public static class Collector extends AbstractAppender {

        public Collector(String name, Filter filter, Layout<? extends Serializable> layout) {
            super(name, filter, layout, false);
        }

        @Override
        public void append(LogEvent event) {
            var event1 = new Message(event);
            HISTORY.add(event1);
            HANDLERS.forEach(handler -> handler.accept(event1));
        }
    }
}

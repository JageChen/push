package simo.conn.push.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class DataIterable<T> implements Iterable<T>, Closeable {
    private final MongoCursor<Document> iterator;
    private final MappingMongoConverter converter;
    private final Class<T> tClass;

    public DataIterable(FindIterable<Document> findIterable, MappingMongoConverter converter, Class<T> tClass) {
        this.iterator = findIterable.iterator();
        this.converter = converter;
        this.tClass = tClass;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                Document next = iterator.next();
                return converter.read(tClass, next);
            }
        };
    }

    @Override
    public void close() throws IOException {
        iterator.close();
    }
}

package dev.armenderoian.summad.util.io.database;

import java.io.IOException;

public interface IDataProvider {

    void open() throws IOException;

    void close() throws IOException;
}

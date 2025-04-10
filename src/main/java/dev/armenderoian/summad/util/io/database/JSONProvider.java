package dev.armenderoian.summad.util.io.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class JSONProvider<T> implements IDataProvider {

    protected Path file;
    protected Gson gson;

    protected T data;

    public JSONProvider(Path file) {
        this.file = file;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public JSONProvider(Path file, boolean prettyPrint) {
        this.file = file;
        if (prettyPrint) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        } else {
            gson = new Gson();
        }
    }

    @Override
    public void open() throws IOException {
        if (!Files.exists(file)) {
            data = gson.fromJson("{}", getDataClass());
        } else {
            try(var reader = Files.newBufferedReader(file)) {
                data = gson.fromJson(reader, getDataClass());
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (data != null) {
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
            }

            try(var writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                gson.toJson(data, getDataClass(), writer);
            }
        }
    }

    public abstract Class<T> getDataClass();
}

package ru.trkpo.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public interface Deserializer<T> {

    Optional<T> deserialize(BufferedReader reader) throws IOException;
}

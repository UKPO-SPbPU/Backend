package ru.trkpo.common.service;

public interface Serializer<T> {
    String serialize(T item);
}

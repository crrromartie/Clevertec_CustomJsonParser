package ru.clevertec.custom;

public interface CustomJsonParser {

    String parseToJson(Object[] objects) throws IllegalAccessException;

    String parseToJson(Object object) throws IllegalAccessException;
}

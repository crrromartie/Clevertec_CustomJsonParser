package ru.clevertec.custom;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.custom.impl.CustomJsonParserImpl;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomJsonParserImplTest {
    private CustomJsonParser customParser;
    private Gson gsonParser;

    @BeforeEach
    void setUp() {
        customParser = new CustomJsonParserImpl();
        gsonParser = new Gson();
    }

    @AfterEach
    void tearDown() {
        customParser = null;
        gsonParser = null;
    }

    @Test
    void parseToJsonTest() throws IllegalAccessException {
        Map<Long, Integer> parameters = new HashMap<>();
        parameters.put(1L, 6);
        parameters.put(2L, 5);
        String actual = customParser.parseToJson(parameters);
        String expected = gsonParser.toJson(parameters);
        assertEquals(expected, actual);
        String[] stringArray = new String[2];
        stringArray[0] = "Java";
        stringArray[1] = "Python";
        String actualArrayArg = customParser.parseToJson(stringArray);
        String expectedArrayArg = gsonParser.toJson(stringArray);
        assertEquals(expectedArrayArg, actualArrayArg);
    }
}

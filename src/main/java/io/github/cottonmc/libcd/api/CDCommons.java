package io.github.cottonmc.libcd.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDCommons {
    public static final String MODID = "libcd";

    public static final Logger logger = LoggerFactory.getLogger("LibCD");

    public static Gson newGson() {
        return new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
    }

}

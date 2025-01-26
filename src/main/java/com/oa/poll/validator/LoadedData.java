package com.oa.poll.validator;

import java.util.List;

public class LoadedData {
    public static List<Integer> VEGGIE_KEYS = null;

    public static void checkLoadedData() {
        if (VEGGIE_KEYS == null) {
            throw new IllegalStateException("List of veggie keys should not be empty. Bad DB configuration");
        }
    }
}

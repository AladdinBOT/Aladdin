package net.heyzeer0.aladdin.database.interfaces;

import net.heyzeer0.aladdin.database.AladdinData;

/**
 * Created by HeyZeer0 on 21/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public interface ManagedObject {
    void delete();

    void save();

    default void deleteAsync() {
        AladdinData.exec.submit(this::delete);
    }

    default void saveAsync() {
        AladdinData.exec.submit(this::save);
    }
}

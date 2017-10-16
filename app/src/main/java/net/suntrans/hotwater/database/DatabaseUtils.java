package net.suntrans.hotwater.database;

import android.arch.persistence.room.Room;

import net.suntrans.hotwater.utils.UiUtils;

/**
 * Created by Looney on 2017/10/16.
 * Des:
 */

public class DatabaseUtils {
    private static final AppDatabase ourInstance = Room.databaseBuilder(UiUtils.getContext(),
            AppDatabase.class, "hotwater-data").build();

    public  static AppDatabase getInstance() {
        return ourInstance;
    }

    private DatabaseUtils() {
    }
}

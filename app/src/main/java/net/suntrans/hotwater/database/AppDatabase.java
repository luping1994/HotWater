package net.suntrans.hotwater.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import net.suntrans.hotwater.bean.WarningHis;
import net.suntrans.hotwater.dao.WarningDao;

/**
 * Created by Looney on 2017/10/16.
 * Des:
 */
@Database(entities = {WarningHis.class}, version = 2,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WarningDao warningDao();
}

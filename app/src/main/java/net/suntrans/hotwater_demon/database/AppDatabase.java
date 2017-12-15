package net.suntrans.hotwater_demon.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import net.suntrans.hotwater_demon.bean.WarningHis;
import net.suntrans.hotwater_demon.dao.WarningDao;

/**
 * Created by Looney on 2017/10/16.
 * Des:
 */
@Database(entities = {WarningHis.class}, version = 2,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WarningDao warningDao();
}

package net.suntrans.hotwater_demon.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import net.suntrans.hotwater_demon.bean.WarningHis;

import java.util.List;

/**
 * Created by Looney on 2017/10/16.
 * Des:
 */
@Dao
public interface WarningDao {
    @Query("SELECT * FROM tb_warning_his")
    List<WarningHis> getAll();

    @Delete
    void delete(WarningHis his);


    @Insert
    void insertAll(WarningHis... hises);

}

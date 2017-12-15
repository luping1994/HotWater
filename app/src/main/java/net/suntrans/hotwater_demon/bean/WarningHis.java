package net.suntrans.hotwater_demon.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Looney on 2017/10/16.
 * Des:
 */
@Entity(tableName = "tb_warning_his")
public class WarningHis {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "ht_time")
    private String time;

    @ColumnInfo(name = "ht_message")
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

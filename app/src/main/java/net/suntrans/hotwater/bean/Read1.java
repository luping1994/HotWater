package net.suntrans.hotwater.bean;

import static net.suntrans.hotwater.R.id.Feire_press_ID;

/**
 * Created by Looney on 2017/8/28.
 */

public class Read1 {
    /**
     * action : read1
     * id : 2101
     * Jire_temp_down_ID : 27.30
     * Jire_temp_up_ID : 27.30
     * Jire_temp_tank_ID : 27.00
     * Hengwen_temp_tank_ID : 27.00
     * Bath_temp_ID : 27.00
     * Dining_temp_ID : 26.80
     * Huanjing_temp_ID : 27.10
     * Jire_level_ID : 0.00
     * Hengwen_level_ID : 0.00
     * created_at : 2017-08-25 16:14:58
     */

    public String action;
    public int id;
    public String Jire_temp_down_ID;
    public String Jire_temp_up_ID;
    public String Jire_temp_tank_ID;
    public String Hengwen_temp_tank_ID;
    public String Bath_temp_ID;
    public String Dining_temp_ID;
    public String Huanjing_temp_ID;
    public String Jire_level_ID;
    public String Hengwen_level_ID;
    public String Feire_press_ID;
    public String Supply_press_ID;

    public String created_at;

    @Override
    public String toString() {
        return "Read1{" +
                "action='" + action + '\'' +
                ", id=" + id +
                ", Jire_temp_down_ID='" + Jire_temp_down_ID + '\'' +
                ", Jire_temp_up_ID='" + Jire_temp_up_ID + '\'' +
                ", Jire_temp_tank_ID='" + Jire_temp_tank_ID + '\'' +
                ", Hengwen_temp_tank_ID='" + Hengwen_temp_tank_ID + '\'' +
                ", Bath_temp_ID='" + Bath_temp_ID + '\'' +
                ", Dining_temp_ID='" + Dining_temp_ID + '\'' +
                ", Huanjing_temp_ID='" + Huanjing_temp_ID + '\'' +
                ", Jire_level_ID='" + Jire_level_ID + '\'' +
                ", Hengwen_level_ID='" + Hengwen_level_ID + '\'' +
//                ", Feire_press_ID='" + Feire_press_ID + '\'' +
//                ", Supply_press_ID='" + Supply_press_ID + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}

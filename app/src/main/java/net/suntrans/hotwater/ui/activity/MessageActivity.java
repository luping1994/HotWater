package net.suntrans.hotwater.ui.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;


import net.suntrans.hotwater.bean.WarningHis;
import net.suntrans.hotwater.database.AppDatabase;
import net.suntrans.hotwater.database.DatabaseUtils;
import net.suntrans.hotwater.databinding.ActivityHisBinding;
import net.suntrans.hotwater.ui.DefaultDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Looney on 2017/10/16.
 * Des:
 */

public class MessageActivity extends AppCompatActivity {

    private ActivityHisBinding binding;
    private MyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_his);
        binding.toolbarWarper.toolbar.setTitle("异常提示");
        setSupportActionBar(binding.toolbarWarper.toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDefaultDisplayHomeAsUpEnabled(true);
        datas = new ArrayList<>();
        adapter = new MyAdapter(R.layout.activity_msg_cen, datas);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.addItemDecoration(new DefaultDecoration());

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        touchHelper.attachToRecyclerView(binding.recyclerview);
        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                delete(datas.get(pos));
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        });

    }

    private void delete(WarningHis warningHis) {
        new DeleteTask().execute(warningHis);
    }


    @Override
    protected void onResume() {
        super.onResume();
        new GetDataTask().execute();
    }

    class GetDataTask extends AsyncTask<Void, Void, List<WarningHis>> {


        private AppDatabase db;

        @Override
        protected List<WarningHis> doInBackground(Void... params) {
            //更新历史数据库数据
            db = DatabaseUtils.getInstance();
            List<WarningHis> all = db.warningDao().getAll();
            return all;
        }

        @Override
        protected void onPostExecute(List<WarningHis> warningHises) {
            datas.clear();
            datas.addAll(warningHises);

            if (datas.size()==0){
                binding.tips.setVisibility(View.VISIBLE);
            }else {
                binding.tips.setVisibility(View.INVISIBLE);

            }
            adapter.notifyDataSetChanged();
        }
    }

    class DeleteTask extends AsyncTask<WarningHis, Void, Boolean> {


        @Override
        protected Boolean doInBackground(WarningHis... params) {
            //更新历史数据库数据
            AppDatabase db = DatabaseUtils.getInstance();
            db.warningDao().delete(params[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean warningHises) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private List<WarningHis> datas;

    static class MyAdapter extends BaseItemDraggableAdapter<WarningHis, BaseViewHolder> {

        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<WarningHis> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, WarningHis item) {
            helper.setText(R.id.msg, item.getMessage())
                    .setText(R.id.time, item.getTime());
        }

    }



    private boolean isExsitMianActivity(Class<?> cls){
        Intent intent = new Intent(this, cls);
        ComponentName cmpName = intent.resolveActivity(getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break;  //跳出循环，优化效率
                }
            }
        }
        return flag;
    }


}

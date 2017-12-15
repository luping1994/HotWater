package net.suntrans.hotwater_demon.ui.activity;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.suntrans.hotwater_demon.R;
import net.suntrans.hotwater_demon.api.RetrofitHelper;
import net.suntrans.hotwater_demon.bean.WarningEntity;
import net.suntrans.hotwater_demon.databinding.ActivityWarningBinding;
import net.suntrans.hotwater_demon.ui.DefaultDecoration;
import net.suntrans.hotwater_demon.utils.UiUtils;
import net.suntrans.looney.widgets.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/10/17.
 * Des:
 */

public class WarningActivity extends RxAppCompatActivity {

    private ActivityWarningBinding binding;
    private MyAdapter adapter;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_warning);
        binding.toolbarWarper.toolbar.setTitle("异常提示");
        setSupportActionBar(binding.toolbarWarper.toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDefaultDisplayHomeAsUpEnabled(true);
        datas = new ArrayList<>();
        adapter = new MyAdapter(R.layout.activity_msg_cen, datas);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.addItemDecoration(new DefaultDecoration());

//        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
//        final ItemTouchHelper touchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
//        touchHelper.attachToRecyclerView(binding.recyclerview);
//        adapter.enableSwipeItem();

        binding.handlerError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(WarningActivity.this)
                        .setMessage("是否处理?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleError();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }


    private List<WarningEntity.InfoBean> datas;

    static class MyAdapter extends BaseItemDraggableAdapter<WarningEntity.InfoBean, BaseViewHolder> {

        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<WarningEntity.InfoBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, WarningEntity.InfoBean item) {
            helper.setText(R.id.msg, item.name)
                    .setText(R.id.time, item.created_at);
        }

    }


    private void getData() {
        RetrofitHelper.getApi().getWarningList()
                .compose(this.<WarningEntity>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<WarningEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(WarningEntity result) {
                        datas.clear();
                        datas.addAll(result.info);
                        adapter.notifyDataSetChanged();
                        if (datas.size() ==0){
                            binding.tips.setVisibility(View.VISIBLE);
                            binding.recyclerview.setVisibility(View.INVISIBLE);
                        }else {
                            binding.tips.setVisibility(View.INVISIBLE);
                            binding.recyclerview.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void handleError() {
        if (datas.size()==0){
            UiUtils.showToast("没有任何异常发生");
            return;
        }
        if (dialog == null) {
            dialog = new LoadingDialog(this);
            dialog.setCancelable(false);
        }



        dialog.setWaitText("请稍后..");
        dialog.show();
        RetrofitHelper.getApi().handlerError("1")
                .compose(this.<WarningEntity>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<WarningEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
//                        UiUtils.showToast("失败")
// ge;

                        getData();
                        if (dialog != null)
                            dialog.dismiss();
                    }

                    @Override
                    public void onNext(WarningEntity result) {
                        if (dialog != null)
                            dialog.dismiss();
                        if (result.code == 1) {

                            UiUtils.showToast("操作成功");
                            getData();
                        } else
                            UiUtils.showToast("失败");
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

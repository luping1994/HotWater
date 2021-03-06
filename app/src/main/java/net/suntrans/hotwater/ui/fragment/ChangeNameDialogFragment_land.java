package net.suntrans.hotwater.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.suntrans.hotwater.R;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.UiUtils;
import net.suntrans.hotwater.widgets.FullScreenDialog;


/**
 * Created by Administrator on 2017/8/16.
 */

public class ChangeNameDialogFragment_land extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    private String title;
    private TextView name;
    private TextView txTitle;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FullScreenDialog dialog = new FullScreenDialog(getContext());
        return dialog;
    }

    public static ChangeNameDialogFragment_land newInstance(String title){
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        ChangeNameDialogFragment_land dialogFragment = new ChangeNameDialogFragment_land();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_change_name_land,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        title = getArguments().getString("title");
        view.findViewById(R.id.queding).setOnClickListener(this);
         view.findViewById(R.id.qvxiao).setOnClickListener(this);
        name = (TextView) view.findViewById(R.id.name);
        txTitle = (TextView) view.findViewById(R.id.title);
        txTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.queding:
                update();
                break;
            case R.id.qvxiao:
                dismiss();
                break;
        }
    }

    private ChangeNameListener listener;

    public ChangeNameListener getListener() {
        return listener;
    }

    public void setListener(ChangeNameListener listener) {
        this.listener = listener;
    }

    public interface ChangeNameListener{
        void changeName(String name);
    }
    private void update() {
        String name1 =name.getText().toString();
        if (TextUtils.isEmpty(name1)){
            UiUtils.showToast("不能为空");
            return;
        }
        LogUtil.i(name1);

        if (listener!=null){
            listener.changeName(name1);
        }
        dismiss();
    }
}

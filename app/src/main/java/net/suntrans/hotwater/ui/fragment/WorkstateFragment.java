package net.suntrans.hotwater.ui.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import net.suntrans.hotwater.R;
import net.suntrans.hotwater.databinding.FragmentWorkstateBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkstateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkstateFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FragmentWorkstateBinding binding;


    public WorkstateFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkstateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkstateFragment newInstance(String param1, String param2) {
        WorkstateFragment fragment = new WorkstateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_workstate,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        updateState(false);
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId==R.id.zidong){
                    updateState(false);
                }else {
                    updateState(true);
                }
            }
        });
    }

    private void updateState(boolean canSwitch) {
        if (canSwitch){
            binding.tynB1.setEnabled(true);
            binding.tynB2.setEnabled(true);
            binding.xieshuifa.setEnabled(true);
            binding.feirexunhuanB.setEnabled(true);
            binding.reshuizhuanyiB1.setEnabled(true);
            binding.reshuizhuanyiB2.setEnabled(true);
            binding.reshuizhuanyifa.setEnabled(true);
            binding.ranqijizu.setEnabled(true);
            binding.reshuigongyingB.setEnabled(true);
            binding.jirebushuifa.setEnabled(true);
            binding.hengwenbushuifa.setEnabled(true);
            binding.xiyumoduanhuishuifa.setEnabled(true);
            binding.shitangmoduanfa.setEnabled(true);
        }else {
            binding.tynB1.setEnabled(false);
            binding.tynB2.setEnabled(false);
            binding.xieshuifa.setEnabled(false);
            binding.feirexunhuanB.setEnabled(false);
            binding.reshuizhuanyiB1.setEnabled(false);
            binding.reshuizhuanyiB2.setEnabled(false);
            binding.reshuizhuanyifa.setEnabled(false);
            binding.ranqijizu.setEnabled(false);
            binding.reshuigongyingB.setEnabled(false);
            binding.jirebushuifa.setEnabled(false);
            binding.hengwenbushuifa.setEnabled(false);
            binding.xiyumoduanhuishuifa.setEnabled(false);
            binding.shitangmoduanfa.setEnabled(false);
        }
    }
}

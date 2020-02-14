package com.example.holidaytest4.fragment;

import android.content.Intent;
import android.os.Bundle;
import com.example.holidaytest4.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.holidaytest4.activities.ViewPointActivity;
import com.example.holidaytest4.utils.UiUtils;
public class FoundFragment extends Fragment implements View.OnClickListener{

    private CardView chongqi;
    private CardView shanghai;
    private CardView anhui;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.found_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhui = view.findViewById(R.id.anhui_cd);
        shanghai = view.findViewById(R.id.shanghai_cd);
        chongqi = view.findViewById(R.id.chongqi_cd);
        anhui.setOnClickListener(this);
        shanghai.setOnClickListener(this);
        chongqi.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(getActivity()!=null) {
            UiUtils.changeStatusBarTextImgColor(getActivity(), true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.anhui_cd:
                Intent intent = new Intent(getActivity(), ViewPointActivity.class);
                intent.putExtra("目的地","安徽");
                startActivity(intent);
            break;
            case R.id.shanghai_cd:
                Intent intent1 = new Intent(getActivity(), ViewPointActivity.class);
                intent1.putExtra("目的地","上海");
                startActivity(intent1);
                break;
            case R.id.chongqi_cd:
                Intent intent2 = new Intent(getActivity(), ViewPointActivity.class);
                intent2.putExtra("目的地","重庆");
                startActivity(intent2);
                break;

        }
    }
    public static FoundFragment newInstance() {
        FoundFragment fragment = new FoundFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
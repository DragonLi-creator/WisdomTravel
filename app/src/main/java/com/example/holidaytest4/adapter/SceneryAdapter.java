package com.example.holidaytest4.adapter;

import android.content.Context;
import com.example.holidaytest4.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.holidaytest4.beans.SceneryInfo;
import com.example.holidaytest4.utils.ImageUtil;
import java.util.List;


public class SceneryAdapter extends RecyclerView.Adapter<SceneryAdapter.ViewHolder> {

    private List<SceneryInfo> list;
    private ItemClickListener itemClickLister;
    private Context myContext;

    //构造方法,并传入数据源
    public SceneryAdapter(List<SceneryInfo> list) {
        this.list = list;
    }

    public void setItemClickLister(ItemClickListener itemClickLister){
        this.itemClickLister = itemClickLister;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(myContext == null){
            myContext = viewGroup.getContext();
        }
        //反射每行的子布局,并把view传入viewHolder中,以便获取控件对象
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.scenery_item_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        SceneryInfo sceneryInfo = list.get(i);

        viewHolder.image_iv.setImageResource(ImageUtil.getImageId(sceneryInfo.getName()));
        viewHolder.name_tv.setText(sceneryInfo.getName());
        viewHolder.address_tv.setText(sceneryInfo.getAddress());
        viewHolder.number_tv.setText(sceneryInfo.getNumber()+"");
        viewHolder.distance_tv.setText(String.format("%skm", sceneryInfo.getDistance()));

        if(itemClickLister!=null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickLister.onItemClick(i);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickLister.onLongClick(i);
                    return true;  //消化事件
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 内部类,获取各控件的对象
     */
    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView image_iv;
        private TextView name_tv;
        private TextView address_tv;
        private TextView number_tv;
        private TextView distance_tv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_iv = itemView.findViewById(R.id.image_scenery);
            name_tv = itemView.findViewById(R.id.name_scenery);
            address_tv = itemView.findViewById(R.id.address_scenery);
            number_tv = itemView.findViewById(R.id.number_scenery);
            distance_tv = itemView.findViewById(R.id.distance_scenery);
        }
    }

}


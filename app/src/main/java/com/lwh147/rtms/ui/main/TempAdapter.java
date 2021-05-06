package com.lwh147.rtms.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lwh147.rtms.R;
import com.lwh147.rtms.data.model.TempInfo;
import com.lwh147.rtms.util.DateTimeUtil;

import java.util.List;

/**
 * @description: 体温信息适配器
 * @author: lwh
 * @create: 2021/5/6 13:16
 * @version: v1.0
 **/
public class TempAdapter extends RecyclerView.Adapter<TempAdapter.ViewHolder> {
    private final List<TempInfo> tempInfos;

    public TempAdapter(List<TempInfo> tempInfos) {
        super();
        this.tempInfos = tempInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temp_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewName.setText(tempInfos.get(position).getResidentName());
        holder.textViewTime.setText(DateTimeUtil.fromToday(tempInfos.get(position).getTime()));
        holder.textViewTemp.setText(String.valueOf(tempInfos.get(position).getTemp()));
    }

    @Override
    public int getItemCount() {
        return this.tempInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTime;
        public TextView textViewName;
        public TextView textViewTemp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.list_item_time);
            textViewTemp = itemView.findViewById(R.id.list_item_temp);
            textViewName = itemView.findViewById(R.id.list_item_name);
        }
    }
}

package com.lwh147.rtms.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lwh147.rtms.R;
import com.lwh147.rtms.data.model.Resident;

import java.util.List;

/**
 * @description: 居民适配器
 * @author: lwh
 * @create: 2021/5/6 20:38
 * @version: v1.0
 **/
public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ViewHolder> {
    private final List<Resident> residents;

    public ResidentAdapter(List<Resident> residents) {
        super();
        this.residents = residents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resident_item, parent, false);
        return new ResidentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            // 设置标题
            holder.textViewOrder.setText("#");
            holder.textViewName.setText("姓名");
            holder.textViewSex.setText("性别");
            holder.textViewBuilding.setText("楼号");
            holder.textViewEntrance.setText("单元号");
            holder.textViewRoom.setText("房间号");
            holder.textViewPhone.setText("联系电话");
            return;
        }
        Resident resident = residents.get(position - 1);
        holder.textViewOrder.setText(String.valueOf(resident.getOrder()));
        holder.textViewName.setText(resident.getName());
        holder.textViewSex.setText(resident.getSex() == 0 ? "男" : "女");
        holder.textViewBuilding.setText(String.valueOf(resident.getBuilding()));
        holder.textViewEntrance.setText(String.valueOf(resident.getEntrance()));
        holder.textViewRoom.setText(String.valueOf(resident.getRoom()));
        holder.textViewPhone.setText(resident.getPhone());
    }

    @Override
    public int getItemCount() {
        return residents.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewOrder;
        public TextView textViewName;
        public TextView textViewSex;
        public TextView textViewBuilding;
        public TextView textViewEntrance;
        public TextView textViewRoom;
        public TextView textViewPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrder = itemView.findViewById(R.id.resident_item_order);
            textViewName = itemView.findViewById(R.id.resident_item_name);
            textViewSex = itemView.findViewById(R.id.resident_item_sex);
            textViewBuilding = itemView.findViewById(R.id.resident_item_building);
            textViewEntrance = itemView.findViewById(R.id.resident_item_entrance);
            textViewRoom = itemView.findViewById(R.id.resident_item_room);
            textViewPhone = itemView.findViewById(R.id.resident_item_phone);
        }
    }
}

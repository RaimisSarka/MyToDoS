package com.ahsas.mytodos;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {

    List<ReminderDataModel> dataModelArrayList;

    public RecyclerAdapter(List<ReminderDataModel> dataModelArrayList){
        this.dataModelArrayList = dataModelArrayList;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        int mId;
        TextView showKindTextView;
        TextView showTitleTextView;
        TextView showStartDateTextView;
        TextView showFinishDateTextView;
        TextView showCommentTextView;
        TextView showStatusTextView; //0 - pending 1 - in progress 2 - canceled 3 - done

        Resources resources;

        public MyHolder(View itemView){
            super(itemView);

            showKindTextView = (TextView) itemView.findViewById(R.id.show_kind_textView);
            showTitleTextView = (TextView) itemView.findViewById(R.id.show_title_textView);
            showStartDateTextView = (TextView) itemView.findViewById(R.id.show_start_date_textView);
            showFinishDateTextView = (TextView) itemView.findViewById(R.id.show_finish_date_textView2);
            showCommentTextView = (TextView) itemView.findViewById(R.id.show_comment_textView);
            showStatusTextView = (TextView) itemView.findViewById(R.id.status_label_textView);

            resources = itemView.getResources();
        }

    }


    @Override
    public MyHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rem_item_view,null);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, Math.round(parent.getContext().getResources().getDimension(R.dimen.recycler_item_margin)), 0, 0);
        view.setLayoutParams(lp);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder( MyHolder holder, int position) {
        ReminderDataModel dataModel=dataModelArrayList.get(position);
        holder.mId = dataModel.getId();
        holder.showKindTextView.setText(dataModel.getKind());
        holder.showTitleTextView.setText(dataModel.getTitle());
        holder.showStartDateTextView.setText(dataModel.getStartDate());
        holder.showFinishDateTextView.setText(dataModel.getFinishDate());
        holder.showCommentTextView.setText(dataModel.getComment());

        switch (dataModel.getStatus()){
            case "0":
                holder.showStatusTextView.setText(R.string.pending_status);
                holder.showStatusTextView.setTextColor(holder.resources.getColor(R.color.colorPendingLabel));
                break;
            case "1":
                holder.showStatusTextView.setText(R.string.in_progress_status);
                holder.showStatusTextView.setTextColor(holder.resources.getColor(R.color.colorInProgressLabel));
                break;
            case "2":
                holder.showStatusTextView.setText(R.string.canceled_status);
                holder.showStatusTextView.setTextColor(holder.resources.getColor(R.color.colorCanceledLabel));
                break;
            case "3":
                holder.showStatusTextView.setText(R.string.done_status);
                holder.showStatusTextView.setTextColor(holder.resources.getColor(R.color.colorDoneLabel));
                break;
            default:
                holder.showStatusTextView.setText(dataModel.getStatus());
                break;

        }


        holder.itemView.setTag(holder.mId);
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public void removeItem (int position){
        dataModelArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataModelArrayList.size());
    }
}

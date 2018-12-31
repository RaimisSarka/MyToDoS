package com.ahsas.mytodos;

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
        TextView showKindTextView;
        TextView showTitleTextView;
        TextView showStartDateTextView;
        TextView showFinishDateTextView;
        TextView showCommentTextView;

        public MyHolder(View itemView){
            super(itemView);

            showKindTextView = (TextView) itemView.findViewById(R.id.show_kind_textView);
            showTitleTextView = (TextView) itemView.findViewById(R.id.show_title_textView);
            showStartDateTextView = (TextView) itemView.findViewById(R.id.show_start_date_textView);
            showFinishDateTextView = (TextView) itemView.findViewById(R.id.show_finish_date_textView2);
            showCommentTextView = (TextView) itemView.findViewById(R.id.show_comment_textView);
        }

    }


    @Override
    public MyHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rem_item_view,null);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //TODO add this to dimensions and make it 3dp not pixels
        lp.setMargins(0, 3, 0, 0);
        view.setLayoutParams(lp);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder( MyHolder holder, int position) {
        ReminderDataModel dataModel=dataModelArrayList.get(position);
        holder.showKindTextView.setText(dataModel.getKind());
        holder.showTitleTextView.setText(dataModel.getTitle());
        holder.showStartDateTextView.setText(dataModel.getStartDate());
        holder.showFinishDateTextView.setText(dataModel.getFinishDate());
        holder.showCommentTextView.setText(dataModel.getComment());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }
}

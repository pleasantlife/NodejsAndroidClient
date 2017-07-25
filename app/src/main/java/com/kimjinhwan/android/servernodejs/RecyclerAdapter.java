package com.kimjinhwan.android.servernodejs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by XPS on 2017-07-25.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    LayoutInflater inflater;
    List<Bbs> data;

    //생성자 만들기
    public RecyclerAdapter(Context context, List<Bbs> data){
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Bbs bbs = data.get(position);
        holder.setTitle(bbs.title);
        holder.setDate(bbs.date);
        holder.setAuthor(bbs.author);
        holder.setContent(bbs.content);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void upDateItem(List<Bbs> data){
        this.data = data;
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView textTitle, textAuthor, textDate, textContent;

        public Holder(View itemView) {
            super(itemView);

            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            textAuthor = (TextView) itemView.findViewById(R.id.textAuthor);
            textDate = (TextView) itemView.findViewById(R.id.textDate);
            textContent = (TextView) itemView.findViewById(R.id.textContent);
        }


        private void setTitle(String title){
            textTitle.setText(title);
        }

        private void setAuthor(String author){
            textAuthor.setText(author);
        }

        private void setContent(String content){
            textContent.setText(content);
        }

        private void setDate(String date){
            textDate.setText(date);
        }
    }
}

package android.example.chatapp;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class myAdapter extends RecyclerView.Adapter {

    List<String> msg;
    Context context;

    public myAdapter(Context ct, List<String> list) {
        msg = list;
        context = ct;
    }

    @Override
    public int getItemViewType(int position) {
        String message = msg.get(position);
        if (message.charAt(0) == '1') {
            return 1;
        }
        else if (message.charAt(0) == '0') {
            return 0;
        }
        return 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;

        if (viewType == 0) {
            view = inflater.inflate(R.layout.recycler_view_template, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == 1) {
            view = inflater.inflate(R.layout.chat_page_row, parent, false);
            return new MyViewHolder1(view);
        }
        view = inflater.inflate(R.layout.date, parent, false);
        return new MyViewHolder2(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String message = msg.get(position);
        int len = message.length();
        if (message.charAt(0) == '0') {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.myText2.setText(message.substring(1, len - 15));
            viewHolder.date_time1.setText(message.substring(len - 5));
        }
        else if (message.charAt(0) == '1'){
            MyViewHolder1 viewHolder = (MyViewHolder1) holder;
            viewHolder.myText23.setText(message.substring(1, len - 15));
            viewHolder.date_time2.setText(message.substring(len - 5));
        }
        else {
            MyViewHolder2 viewHolder = (MyViewHolder2) holder;
            viewHolder.date.setText(message.substring(10));
        }
    }

    @Override
    public int getItemCount() {
        return msg.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView myText2;
        TextView date_time1;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date_time1 = itemView.findViewById(R.id.dateTimeTeal);
            myText2 = itemView.findViewById(R.id.textView7);
        }
    }

    public static class MyViewHolder1 extends RecyclerView.ViewHolder {

        TextView myText23;
        TextView date_time2;

        public MyViewHolder1(@NonNull View itemView) {
            super(itemView);
            date_time2 = itemView.findViewById(R.id.dateTimeWhite);
            myText23 = itemView.findViewById(R.id.msgFieldRight);
        }
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder {

        TextView date;

        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateField);
        }
    }
}

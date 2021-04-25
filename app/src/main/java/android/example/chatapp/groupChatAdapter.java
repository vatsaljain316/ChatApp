package android.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class groupChatAdapter extends RecyclerView.Adapter {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userEmail = user.getEmail().replace('.', ',');
    List<String> chats;
    Context context;

    public groupChatAdapter(List<String> chats, Context context) {
        this.chats = chats;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        String message = chats.get(position);
        String[] splitMessage = message.split(":del:", -1);
        if(splitMessage[0].equals(userEmail)) {
            return 1;
        } else if(splitMessage[0].equals("dateChange")) {
            return 2;
        } else {
            if(splitMessage[1].equals("repeat")) {
                return 3;
            }
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        if(viewType == 0) {
            view = inflater.inflate(R.layout.group_chat_row, parent, false);
            return new ViewHolder(view);
        } else if(viewType == 1) {
            view = inflater.inflate(R.layout.chat_page_row, parent, false);
            return new ViewHolderSelf(view);
        } else if(viewType == 2) {
            view = inflater.inflate(R.layout.date, parent, false);
            return new ViewHolderDate(view);
        }
        view = inflater.inflate(R.layout.recycler_view_template, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String message = chats.get(position);
        String[] splitMessage = message.split(":del:", -1);
        if(splitMessage[0].equals(userEmail)) {
            ViewHolderSelf viewHolder = (ViewHolderSelf) holder;
            String text = splitMessage[1];
            if(text.equals("repeat")) {
                text = splitMessage[2];
            }
            int len = text.length();
            viewHolder.message.setText(text.substring(0, len - 15));
            viewHolder.time.setText(text.substring(len - 5));
        } else if(splitMessage[0].equals("dateChange")) {
            ViewHolderDate viewHolder = (ViewHolderDate) holder;
            viewHolder.date.setText(splitMessage[1]);
        } else if(!splitMessage[1].equals("repeat")) {
            ViewHolder viewHolder = (ViewHolder) holder;
            String text = splitMessage[1];
            int len = text.length();
            viewHolder.message.setText(text.substring(0, len - 15));
            viewHolder.time.setText(text.substring(len - 5));
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(splitMessage[0]).child("Username");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    viewHolder.userName.setText(snapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            String text = splitMessage[2];
            int len = text.length();
            viewHolder.myText2.setText(text.substring(0, len - 15));
            viewHolder.date_time1.setText(text.substring(len - 5));
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ViewHolderDate extends RecyclerView.ViewHolder {

        TextView date;

        public ViewHolderDate(@NonNull View view) {
            super(view);
            date = view.findViewById(R.id.dateField);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView message;
        TextView time;
        TextView userName;

        public ViewHolder(@NonNull View view) {
            super(view);
            message = view.findViewById(R.id.groupMessageField);
            time = view.findViewById(R.id.groupMessageTime);
            userName = view.findViewById(R.id.groupMessageUsername);
        }
    }

    public static class ViewHolderSelf extends RecyclerView.ViewHolder{

        TextView message;
        TextView time;

        public ViewHolderSelf(@NonNull View view) {
            super(view);
            message = view.findViewById(R.id.msgFieldRight);
            time = view.findViewById(R.id.dateTimeWhite);
        }
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
}

package android.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class chatsRowClass extends RecyclerView.Adapter<chatsRowClass.MyRowHolder> {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userEmail;

    {
        assert user != null;
        userEmail = user.getEmail().replace('.', ',');
    }

    List<String> client_email;
    Context context;

    public chatsRowClass(Context context, List<String> client_email) {
        this.client_email = client_email;
        this.context = context;
    }

    @NonNull
    @Override
    public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_row, parent, false);
        return new MyRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
        String str = client_email.get(position);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(str).child("Username");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.client_username.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref = FirebaseDatabase.getInstance().getReference().child(userEmail).child("Chats").child(str).child("Recent");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String message = snapshot.getValue().toString();
                int len = message.length();
                if(len != 0) {
                    if(len - 16 >= 40) {
                        String msg = message.substring(1, len - 15).substring(0, 40) + "...";
                        holder.client_recent.setText(msg);
                    } else {
                        holder.client_recent.setText(message.substring(1, len - 15));
                    }
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
                    String dateTime = formatter.format(date);
                    String substring = message.substring(len - 15, len - 7);
                    if(!dateTime.equals(substring)) {
                        holder.recent_time.setText(substring);
                    } else {
                        holder.recent_time.setText(message.substring(len - 5));
                    }
                    if(message.charAt(0) == '0') {
                        holder.client_recent.setTextColor(ContextCompat.getColor(context, R.color.purple_200));
                    }
                } else {
                    holder.recent_time.setText("");
                    holder.client_recent.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, chatPage.class);
                intent.putExtra("recipient", client_email.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return client_email.size();
    }

    public static class MyRowHolder extends RecyclerView.ViewHolder {

        TextView client_username;
        TextView client_recent;
        ConstraintLayout row;
        TextView recent_time;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            recent_time = itemView.findViewById(R.id.recentTime);
            client_username = itemView.findViewById(R.id.u_name_row_element);
            client_recent = itemView.findViewById(R.id.recent_row_element);
            row = itemView.findViewById(R.id.chat_row);
        }
    }
}

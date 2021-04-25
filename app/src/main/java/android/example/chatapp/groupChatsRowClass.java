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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class groupChatsRowClass extends RecyclerView.Adapter<groupChatsRowClass.MyGroupRowHolder> {

    List<String> groups;
    List<String> groupNames;
    Context context;
    String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');

    public groupChatsRowClass(List<String> groups, List<String> groupNames, Context context) {
        this.groups = groups;
        this.groupNames = groupNames;
        this.context = context;
    }

    @NonNull
    @Override
    public MyGroupRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.group_row_new, parent, false);
        return new MyGroupRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyGroupRowHolder holder, int position) {
        String str = groupNames.get(position);
        holder.client_username.setText(str);

        String groupID = groups.get(position);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("Recent");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String message = snapshot.getValue().toString();
                if(TextUtils.isEmpty(message)) {
                    holder.recent_time.setText("");
                    holder.client_recent.setText("");
                    holder.sender.setText("");
                }
                else {
                    String[] splitMessage = message.split(":del:", -1);
                    String text = splitMessage[1];
                    String ID = splitMessage[0];
                    holder.client_recent.setTag(ID);
                    int len = text.length();
                    if(ID.equals(userEmail)) {
                        if(len - 16 >= 40) {
                            String msg = text.substring(0, 40) + "...";
                            holder.client_recent.setText(msg);
                        }
                        else {
                            holder.client_recent.setText(text.substring(0, len - 15));
                        }
                        holder.sender.setText("");
                    }
                    else {
                        if(len - 16 >= 33) {
                            String msg = text.substring(0, 33) + "...";
                            holder.client_recent.setText(msg);
                        }
                        else {
                            holder.client_recent.setText(text.substring(0, len - 15));
                        }
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child(ID).child("Username");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String username = snapshot.getValue().toString();
                                username += "  ";
                                holder.sender.setText(username);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
                    String dateTime = formatter.format(date);
                    String substring = text.substring(len - 15, len - 7);
                    if(!dateTime.equals(substring)) {
                        holder.recent_time.setText(substring);
                    } else {
                        holder.recent_time.setText(text.substring(len - 5));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, groupChatPage.class);
                intent.putExtra("groupID", groups.get(position));
                intent.putExtra("groupName", groupNames.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class MyGroupRowHolder extends RecyclerView.ViewHolder {

        TextView client_username;
        TextView client_recent;
        ConstraintLayout row;
        TextView recent_time;
        TextView sender;

        public MyGroupRowHolder(@NonNull View itemView) {
            super(itemView);

            sender = itemView.findViewById(R.id.textViewSender);
            recent_time = itemView.findViewById(R.id.recentTime_new);
            client_username = itemView.findViewById(R.id.u_name_row_element_new);
            client_recent = itemView.findViewById(R.id.recent_row_element_new);
            row = itemView.findViewById(R.id.chat_row_new);
        }
    }
}

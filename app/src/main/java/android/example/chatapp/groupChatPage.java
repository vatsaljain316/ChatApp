package android.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class groupChatPage extends AppCompatActivity {

    private ImageButton send, back;
    private EditText msgField;
    private RecyclerView chatSpace;
    private TextView title;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_page);

        title = findViewById(R.id.groupName);
        back = findViewById(R.id.backToChatPage);
        String groupID = getIntent().getStringExtra("groupID");
        String groupName = getIntent().getStringExtra("groupName");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("Recent");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String message = snapshot.getValue().toString();
                String[] splitMessage = message.split(":del:", -1);
                back.setTag(splitMessage[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        send = findViewById(R.id.sendGroupMsg);

        msgField = findViewById(R.id.typeMessage);
        chatSpace = findViewById(R.id.groupChatDisplay);


        title.setText(groupName);

        ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("Recent");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String message = snapshot.getValue().toString();
                int len = message.length();
                if(len != 0) {
                    String recentDate = message.substring(len - 15, len - 7);
                    title.setTag(recentDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("Messages");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> messages = new LinkedList<String>();
                for(DataSnapshot snap : snapshot.getChildren()) {
                    String message = snap.getValue().toString();
                    messages.add(message);
                }
                groupChatAdapter myAdp = new groupChatAdapter(messages,groupChatPage.this);
                chatSpace.setAdapter(myAdp);
                LinearLayoutManager LLM = new LinearLayoutManager(groupChatPage.this);
                LLM.setStackFromEnd(true);                                                          // to auto scroll to bottom of recycler view
                chatSpace.setLayoutManager(LLM);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(groupChatPage.this, chats.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = msgField.getText().toString();
                if(!TextUtils.isEmpty(message)) {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy  HH:mm");
                    String dateTime = formatter.format(date);
                    String recentDate = title.getTag().toString();
                    if(!dateTime.substring(0, 8).equals(recentDate)) {
                        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("Messages").push().setValue("dateChange:del:" + dateTime.substring(0, 8));
                    }
                    if(back.getTag().toString().equals(user.getEmail().replace('.', ','))) {
                        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("Messages").push().setValue(user.getEmail().replace('.', ',') + ":del:" + "repeat:del:" + message + dateTime);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("Messages").push().setValue(user.getEmail().replace('.', ',') + ":del:" + message + dateTime);
                    }
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(groupID).child("Recent").setValue(user.getEmail().replace('.', ',') + ":del:" + message + dateTime);
                    msgField.setText("");
                }
            }
        });
    }
}
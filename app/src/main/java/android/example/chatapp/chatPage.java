package android.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class chatPage extends AppCompatActivity {

    private ImageButton back, sendMsg;
    private TextView client_name, msgField;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        client_name = findViewById(R.id.u_name_client);
        recyclerView = findViewById(R.id.chatDisplay);
        sendMsg = findViewById(R.id.sendMsg);
        msgField = findViewById(R.id.msgField);

        String email;
        email = getIntent().getStringExtra("recipient");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(email).child("Username");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                client_name.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        ref = FirebaseDatabase.getInstance().getReference().child(user.getEmail().replace('.', ',')).child("Chats").child(email).child("Recent");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String message = snapshot.getValue().toString();
                int len = message.length();
                if(len != 0) {
                    String recentDate = message.substring(len - 15, len - 7);
                    client_name.setTag(recentDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back = findViewById(R.id.back_to_chats);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chatPage.this, chats.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(user.getEmail().replace('.', ',')).child("Chats").child(email.replace('.', ',')).child("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new LinkedList<String>();
                for(DataSnapshot snap : snapshot.getChildren()) {
                    list.add(snap.getValue().toString());
                }
                myAdapter myAdp = new myAdapter(chatPage.this, list);
                recyclerView.setAdapter(myAdp);
                LinearLayoutManager LLM = new LinearLayoutManager(chatPage.this);
                LLM.setStackFromEnd(true);                                                          // to auto scroll to bottom of recycler view
                recyclerView.setLayoutManager(LLM);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = msgField.getText().toString();
                if(!TextUtils.isEmpty(text)) {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy  HH:mm");
                    String dateTime = formatter.format(date);
                    String recentDate = client_name.getTag().toString();
                    if(!dateTime.substring(0, 8).equals(recentDate)) {
                        FirebaseDatabase.getInstance().getReference().child(user.getEmail().replace('.', ',')).child("Chats").child(email.replace('.', ',')).child("Messages").push().setValue("dateChange" + dateTime.substring(0, 8));
                        FirebaseDatabase.getInstance().getReference().child(email.replace('.', ',')).child("Chats").child(user.getEmail().replace('.', ',')).child("Messages").push().setValue("dateChange" + dateTime.substring(0, 8));
                    }
                    FirebaseDatabase.getInstance().getReference().child(user.getEmail().replace('.', ',')).child("Chats").child(email.replace('.', ',')).child("Messages").push().setValue("1" + text + dateTime);
                    FirebaseDatabase.getInstance().getReference().child(user.getEmail().replace('.', ',')).child("Chats").child(email.replace('.', ',')).child("Recent").setValue("1" + text + dateTime);
                    FirebaseDatabase.getInstance().getReference().child(email.replace('.', ',')).child("Chats").child(user.getEmail().replace('.', ',')).child("Messages").push().setValue("0" + text + dateTime);
                    FirebaseDatabase.getInstance().getReference().child(email.replace('.', ',')).child("Chats").child(user.getEmail().replace('.', ',')).child("Recent").setValue("0" + text + dateTime);
                    msgField.setText("");
                }
            }
        });


    }
}
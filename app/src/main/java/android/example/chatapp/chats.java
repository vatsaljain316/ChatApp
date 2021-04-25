package android.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class chats extends AppCompatActivity {

    private ImageButton logout, backboy;
    private FloatingActionButton newChat;
    private FirebaseAuth auth;
    private RecyclerView chatRecyclerView, groupChatRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        chatRecyclerView = findViewById(R.id.chatView);
        groupChatRecyclerView = findViewById(R.id.grpChatView);
        newChat = findViewById(R.id.addChat);

        logout = findViewById(R.id.button_logout);

        assert user != null;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(user.getEmail().replace('.', ',')).child("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                List<String> listEmail = new LinkedList<String>();
                for(DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String str = snapshot.getKey();
                    listEmail.add(str);
                }
                chatsRowClass myRow = new chatsRowClass(chats.this, listEmail);
                chatRecyclerView.setAdapter(myRow);
                chatRecyclerView.setLayoutManager(new LinearLayoutManager(chats.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref = FirebaseDatabase.getInstance().getReference().child(user.getEmail().replace('.', ',')).child("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> groups = new LinkedList<String>();
                List<String> groupNames = new LinkedList<String>();
                for(DataSnapshot snap : snapshot.getChildren()) {
                    String group = snap.getKey();
                    String groupName = snap.getValue().toString();
                    groups.add(group);
                    groupNames.add(groupName);
                }
                groupChatsRowClass myRow = new groupChatsRowClass(groups, groupNames, chats.this);
                groupChatRecyclerView.setAdapter(myRow);
                groupChatRecyclerView.setLayoutManager(new LinearLayoutManager(chats.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(chats.this);
                View alertView = getLayoutInflater().inflate(R.layout.alert_box,null);
                materialAlertDialogBuilder.setView(alertView);
                alertView.findViewById(R.id.imageButton2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        auth.signOut();
                        startActivity(new Intent(chats.this, MainActivity.class));
                        finishAffinity();
                    }
                });
                AlertDialog dialog = materialAlertDialogBuilder.create();
                dialog.show();
            }
        });

        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chats.this, NewChat.class);
                intent.putExtra("flag", true);
                startActivity(intent);
            }
        });
    }
}
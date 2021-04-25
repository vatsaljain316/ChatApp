package android.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewChat extends AppCompatActivity {

    private TextInputEditText email, reference;
    private boolean mainflag = false;

    private ImageButton createNewChat, backboy;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        mainflag = getIntent().getBooleanExtra("flag", false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String userEmail = user.getEmail();

        email = findViewById(R.id.newEmail);
        reference = findViewById(R.id.refPin);

        createNewChat = findViewById(R.id.createNewChat);
        backboy = findViewById(R.id.back_to_chat_page);

        backboy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewChat.this, chats.class));
                finishAffinity();
            }
        });


        createNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEmail = email.getText().toString();
                String newRefPin = reference.getText().toString();

                if(TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(newRefPin)) {
                    Toast.makeText(NewChat.this, "Enter Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    createChat(newEmail, newRefPin, userEmail);
                }
            }
        });
    }

    private void createChat(String newEmail, String newRefPin, String userEmail) {
        if(userEmail.equals(newEmail)) {
            Toast.makeText(this, "Can't create chat with self", Toast.LENGTH_SHORT).show();
        }
        else if(mainflag) {
            mainflag = false;
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean flag = false;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snap.getKey().equals(newEmail.replace('.', ','))) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        Toast.makeText(NewChat.this, "No such user exists", Toast.LENGTH_SHORT).show();
                        mainflag = true;
                    } else {
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child(newEmail.replace('.', ',')).child("refPin");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue().toString().equals(newRefPin)) {
                                    FirebaseDatabase.getInstance().getReference().child(newEmail.replace('.', ',')).child("Chats").child(userEmail.replace('.', ',')).child("Messages").setValue("");
                                    FirebaseDatabase.getInstance().getReference().child(newEmail.replace('.', ',')).child("Chats").child(userEmail.replace('.', ',')).child("Recent").setValue("");
                                    FirebaseDatabase.getInstance().getReference().child(userEmail.replace('.', ',')).child("Chats").child(newEmail.replace('.', ',')).child("Messages").setValue("");
                                    FirebaseDatabase.getInstance().getReference().child(userEmail.replace('.', ',')).child("Chats").child(newEmail.replace('.', ',')).child("Recent").setValue("");
                                    Toast.makeText(NewChat.this, "New chat successfully created", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(NewChat.this, chats.class));
                                    finishAffinity();
                                } else {
                                    Toast.makeText(NewChat.this, "Wrong Reference Pin", Toast.LENGTH_SHORT).show();
                                    mainflag = true;

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
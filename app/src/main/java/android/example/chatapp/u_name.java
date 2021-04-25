package android.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class u_name extends AppCompatActivity {

    private ImageButton next;
    private TextInputEditText username_field, refpin_field;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_name);

        next = findViewById(R.id.imageButton);
        username_field = findViewById(R.id.enter_u_name);
        refpin_field = findViewById(R.id.customRefPin);
        mAuth = FirebaseAuth.getInstance();
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_field.getText().toString();
                String reference = refpin_field.getText().toString();
                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(reference)) {
                    Toast.makeText(u_name.this, "Empty fields", Toast.LENGTH_SHORT).show();

                }
                else if(username.length() > 15) {
                    Toast.makeText(u_name.this, "Username must be 8-15 characters long", Toast.LENGTH_SHORT).show();

                }
                else if(reference.length() != 4) {
                    Toast.makeText(u_name.this, "Reference pin length exceeded", Toast.LENGTH_SHORT).show();

                }
                else {
                    mAuth.signInWithEmailAndPassword(email, password);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user != null) {
                        String email = user.getEmail();
                        email = email.replace('.', ',');
                        FirebaseDatabase.getInstance().getReference().child(email).child("Username").setValue(username);
                        FirebaseDatabase.getInstance().getReference().child(email).child("refPin").setValue(reference);
                        Toast.makeText(u_name.this, "Username updated", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(u_name.this, chats.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else {
                        Toast.makeText(u_name.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
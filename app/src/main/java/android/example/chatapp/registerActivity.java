package android.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registerActivity extends AppCompatActivity {

    private ImageButton register;
    private TextInputEditText username, password, confirm_password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.button_reg);
        username = findViewById(R.id.u_name_edit);
        password = findViewById(R.id.pass_edit);
        confirm_password = findViewById(R.id.pass_conf_edit);
        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = username.getText().toString();
                String txt_password = password.getText().toString();
                String txt_conf_password = confirm_password.getText().toString();

                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_conf_password)) {
                    Toast.makeText(registerActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }
                else if(!txt_password.equals(txt_conf_password)) {
                    Toast.makeText(registerActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length() != 6) {
                    Toast.makeText(registerActivity.this, "Password must be a 6 digit pin", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(txt_email, txt_password);
                }
            }
        });

    }
    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(email.replace('.', ','));
                    ref.child("Username").setValue("");
                    ref.child("Chats").child("").setValue("");
                    Toast.makeText(registerActivity.this, "User successfully registered", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(registerActivity.this, "User already exists", Toast.LENGTH_SHORT).show();

                }
                startActivity(new Intent(registerActivity.this, loginActivity.class));
                finish();
            }
        });
    }
}
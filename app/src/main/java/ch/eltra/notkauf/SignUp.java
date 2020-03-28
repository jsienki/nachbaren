package ch.eltra.notkauf;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ch.eltra.notkauf.MainActivity.handler;

public class SignUp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button signUpButton = findViewById(R.id.signUpButton);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText rPasswordInput = findViewById(R.id.rPasswordInput);
        EditText usernameInput = findViewById(R.id.usernameInput);

        signUpButton.setOnClickListener(v -> {
            Map<String, String> params = new HashMap<String, String>();
            params.put("login", usernameInput.getText().toString());
            params.put("password", passwordInput.getText().toString());
            params.put("name", "test");
            String jsonString = new JSONObject(params).toString();
            try {
                int response = handler.post("/api/Auth/sign-up", jsonString);
                if (response == 200) {
                    Toast.makeText(SignUp.this, "Success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUp.this, "Sign Up failed", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Toast.makeText(SignUp.this, "Sign Up failed", Toast.LENGTH_LONG).show();
            }
        });

    }
}

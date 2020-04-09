package ch.eltra.notkauf;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
        Button closeButton = findViewById(R.id.closeButton);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText rPasswordInput = findViewById(R.id.rPasswordInput);
        EditText usernameInput = findViewById(R.id.usernameInput);
        TextView terms = findViewById(R.id.terms_text);

        terms.setMovementMethod(LinkMovementMethod.getInstance());

        signUpButton.setOnClickListener(v -> {
            boolean pwMatch = false;
            boolean input1 = false;
            boolean input2 = false;
            if (!usernameInput.getText().toString().equals("")) {
                input1 = true;
            } else {
                Toast.makeText(SignUp.this, getString(R.string.email_empty), Toast.LENGTH_LONG).show();
            }
            if (!passwordInput.getText().toString().equals("") && !(passwordInput.getText().toString().length() < 4)) {
                input2 = true;
            } else {
                Toast.makeText(SignUp.this, getString(R.string.length_warning), Toast.LENGTH_LONG).show();
            }
            if (input2) {
                if (passwordInput.getText().toString().equals(rPasswordInput.getText().toString())) {
                    pwMatch = true;
                } else {
                    Toast.makeText(SignUp.this, getString(R.string.no_match), Toast.LENGTH_LONG).show();
                }
            }

            if (input1 && input2 && pwMatch) {
                Map<String, String> params = new HashMap<>();
                params.put("login", usernameInput.getText().toString());
                params.put("password", passwordInput.getText().toString());
                params.put("name", "test");
                String jsonString = new JSONObject(params).toString();
                try {
                    int response = handler.post("/api/Auth/sign-up", jsonString);
                    if (response == 200) {
                        Toast.makeText(SignUp.this, getText(R.string.success), Toast.LENGTH_LONG).show();
                        finish();
                    } else if (response == 400){
                        Toast.makeText(SignUp.this, getText(R.string.taken), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignUp.this, getText(R.string.sign_up_failed), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        closeButton.setOnClickListener(v -> finish());

    }
}

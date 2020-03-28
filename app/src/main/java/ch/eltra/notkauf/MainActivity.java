package ch.eltra.notkauf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button loginButton;
    Button signUpButton;
    EditText usernameInput, passwordInput;
    CheckBox rememberCheck;
    String username;
    String password;
    int responseCode;
    static HTTPHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        handler = new HTTPHandler("https://notkauf.ch");
        setItems();
    }

    void setItems() {
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        rememberCheck = findViewById(R.id.rememberCheck);

        loginButton.setOnClickListener(v -> {
            login();
        });

        signUpButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, SignUp.class);
            MainActivity.this.startActivity(myIntent);
        });
    }

    void login() {
        if (!usernameInput.getText().toString().equals("")) {
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();
            if (rememberCheck.isChecked()) {
                Log.d("WIP", "Password saved!");
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("login", username);
            params.put("password", password);
            params.put("name", "test");
            String jsonString = new JSONObject(params).toString();

            try {
                responseCode = handler.post("/api/Auth/sign-in", jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (responseCode == 200) {
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(MainActivity.this, MenuActivity.class);
                MainActivity.this.startActivity(myIntent);
            } else {
                Toast.makeText(MainActivity.this, "Invalid Input", Toast.LENGTH_LONG).show();
            }

            try {
                int response = handler.get("/api/Emergency/number");
                Toast.makeText(MainActivity.this, "" + response, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Error", "Fields are empty!");
        }
    }

}

package ch.eltra.notkauf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button loginButton;
    Button signUpButton;
    EditText usernameInput, passwordInput;
    CheckBox rememberCheck;
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
        loadData();
    }

    void loadData() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String myJSON = mPrefs.getString("MyObject", "");
        MyCreds myObject = gson.fromJson(myJSON, MyCreds.class);
        if (myObject != null) {
            usernameInput.setText(myObject.username);
            passwordInput.setText(myObject.password);
            rememberCheck.setChecked(true);
        }
    }

    void setItems() {
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        rememberCheck = findViewById(R.id.rememberCheck);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            login(username, password);
        });

        signUpButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, SignUp.class);
            MainActivity.this.startActivity(myIntent);
        });
    }

    void login(String username, String password) {
        if (!username.equals("") && !password.equals("")) {
            Map<String, String> params = new HashMap<>();
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
                if (rememberCheck.isChecked()) {
                    saveLoginDetails(username, password);
                }
                Toast.makeText(MainActivity.this, getString(R.string.success), Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(MainActivity.this, MenuActivity.class);
                MainActivity.this.startActivity(myIntent);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(MainActivity.this, getString(R.string.fields_empty), Toast.LENGTH_LONG).show();
        }
    }

    void saveLoginDetails(String username, String password) {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        MyCreds myObject = new MyCreds();
        myObject.username = username;
        myObject.password = password;
        Gson gson = new Gson();
        String myJSON = gson.toJson(myObject);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        prefsEditor.putString("MyObject", myJSON);
        prefsEditor.apply();
    }

}

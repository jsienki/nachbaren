package ch.eltra.notkauf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.eltra.notkauf.MainActivity.handler;

public class TabFragment3 extends Fragment {
    private static final String TAG = "TabFragment3";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_frag3, container, false);
        Button save = view.findViewById(R.id.saveButton);
        EditText name = view.findViewById(R.id.nameEdit);
        EditText phone = view.findViewById(R.id.phoneEdit);
        EditText street = view.findViewById(R.id.streetEdit);
        EditText city = view.findViewById(R.id.cityEdit);
        EditText region = view.findViewById(R.id.regionEdit);
        EditText pCode = view.findViewById(R.id.pcEdit);
        EditText notice = view.findViewById(R.id.noticeEdit);

        JSONObject contact = null;
        try {
            contact = handler.getJSON("/api/Contacts/get");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contact != null) {
            try {
                name.setText(contact.get("Name").toString());
                phone.setText(contact.get("Phone").toString());
                street.setText(contact.get("Street").toString());
                city.setText(contact.get("City").toString());
                region.setText(contact.get("Region").toString());
                pCode.setText(contact.get("PostalCode").toString());
                notice.setText(contact.get("Notice").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        save.setOnClickListener(v -> {
            Map<String, String> params = new HashMap<>();
            params.put("name", name.getText().toString());
            params.put("phone", phone.getText().toString());
            params.put("street", street.getText().toString());
            params.put("city", city.getText().toString());
            params.put("region", region.getText().toString());
            params.put("postalCode", pCode.getText().toString());
            params.put("notice", notice.getText().toString());
            params.put("longitude", "0");
            params.put("latitude", "0");
            String jsonString = new JSONObject(params).toString();

            try {
                int responseCode = handler.post("/api/Contacts/set", jsonString);
                if (responseCode == 200) {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Saving contact failed", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return view;
    }
}

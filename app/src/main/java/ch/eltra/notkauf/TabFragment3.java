package ch.eltra.notkauf;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ch.eltra.notkauf.MainActivity.handler;

public class TabFragment3 extends Fragment {
    private Spinner country, region;
    private String[] items2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_frag3, container, false);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button signOutButton = view.findViewById(R.id.signOutButton);
        Button aboutButton = view.findViewById(R.id.aboutButton);
        EditText firstNameEdit = view.findViewById(R.id.firstNameEdit);
        EditText lastNameEdit = view.findViewById(R.id.lastNameEdit);
        EditText phoneEdit = view.findViewById(R.id.phoneEdit);
        EditText streetEdit = view.findViewById(R.id.streetEdit);
        EditText cityEdit = view.findViewById(R.id.cityEdit);

        region = view.findViewById(R.id.regionSpinner);
        country = view.findViewById(R.id.countrySpinner);

        EditText pcCodeEdit = view.findViewById(R.id.pcEdit);
        EditText noticeEdit = view.findViewById(R.id.noticeEdit);

        try {
            setupSpinner();
        } catch (IOException e) {
            e.printStackTrace();
        }

        aboutButton.setOnClickListener(v -> {
            
        });

        JSONObject contact = null;
        try {
            contact = handler.getJSON("/api/Contacts/get-contact");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contact != null) {
            try {
                String firstName = contact.get("FirstName").toString();
                String lastName = contact.get("LastName").toString();

                firstNameEdit.setText(contact.get("FirstName").toString());
                lastNameEdit.setText(contact.get("LastName").toString());
                phoneEdit.setText(contact.get("Phone").toString());
                streetEdit.setText(contact.get("Street").toString());
                cityEdit.setText(contact.get("City").toString());

                for (int i = 0; i < items2.length; i++)
                {
                   if (items2[i].equals(contact.get("Region").toString()))
                   {
                      region.setSelection(i);
                   }
                }

                pcCodeEdit.setText(contact.get("PostalCode").toString());
                noticeEdit.setText(contact.get("Notice").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        saveButton.setOnClickListener(v -> {
            boolean nameEmpty = TextUtils.isEmpty(firstNameEdit.getText().toString());
            boolean phoneEmpty = TextUtils.isEmpty(phoneEdit.getText().toString());
            boolean streetEmpty = TextUtils.isEmpty(streetEdit.getText().toString());
            boolean cityEmpty = TextUtils.isEmpty(cityEdit.getText().toString());
            boolean regionEmpty = TextUtils.isEmpty(region.getSelectedItem().toString());
            boolean postalEmpty = TextUtils.isEmpty(pcCodeEdit.getText().toString());

            if (!(nameEmpty || phoneEmpty || streetEmpty || cityEmpty || regionEmpty ||postalEmpty)) {
                Map<String, String> params = new HashMap<>();
                params.put("firstName", firstNameEdit.getText().toString());
                params.put("phone", phoneEdit.getText().toString());
                params.put("street", streetEdit.getText().toString());
                params.put("city", cityEdit.getText().toString());
                params.put("region", region.getSelectedItem().toString());
                params.put("postalCode", pcCodeEdit.getText().toString());
                params.put("notice", noticeEdit.getText().toString());
                params.put("longitude", "0");
                params.put("latitude", "0");
                String jsonString = new JSONObject(params).toString();

                try {
                    int responseCode = handler.post("/api/Contacts/set-contact", jsonString);
                    if (responseCode == 200) {
                        Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.contact_failed), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.required_fields_empty), Toast.LENGTH_LONG).show();
            }
        });

        signOutButton.setOnClickListener(v -> {
            Objects.requireNonNull(getActivity()).finish();
            handler.clearCookies();
        });


        return view;
    }

    private void setupSpinner() throws IOException {
        String[] items = new String[]{getString(R.string.switzerland)};
        ArrayAdapter adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, items);
        country.setAdapter(adapter);

        JSONArray regions = handler.getJSONArrayParams3("/api/Regional/get-regions", "CH", "de");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<RegionModel>>() {
        }.getType();

        List<RegionModel> reg = gson.fromJson(regions.toString(), listType);
        ArrayList<String> regNames = new ArrayList<>();
        for (int i = 0; i < reg.size(); i++) {
          regNames.add(reg.get(i).Name);
        }
        items2 = new String[regNames.size()];
        items2 = regNames.toArray(items2);

        ArrayAdapter adapter2 = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, items2);
        region.setAdapter(adapter2);


    }
}

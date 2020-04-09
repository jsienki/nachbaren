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
        Button save = view.findViewById(R.id.saveButton);
        Button out = view.findViewById(R.id.signOutButton);
        Button info = view.findViewById(R.id.aboutButton);
        EditText name = view.findViewById(R.id.nameEdit);
        EditText phone = view.findViewById(R.id.phoneEdit);
        EditText street = view.findViewById(R.id.streetEdit);
        EditText city = view.findViewById(R.id.cityEdit);
        region = view.findViewById(R.id.regionSpinner);
        country = view.findViewById(R.id.countrySpinner);
        EditText pCode = view.findViewById(R.id.pcEdit);
        EditText notice = view.findViewById(R.id.noticeEdit);

        try {
            setupSpinner();
        } catch (IOException e) {
            e.printStackTrace();
        }

        info.setOnClickListener(v -> {
            
        });

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
                for (int i = 0; i < items2.length; i++) {
                   if (items2[i].equals(contact.get("Region").toString())) {
                      region.setSelection(i);
                    }
                }
                pCode.setText(contact.get("PostalCode").toString());
                notice.setText(contact.get("Notice").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        save.setOnClickListener(v -> {
            boolean nameEmpty = TextUtils.isEmpty(name.getText().toString());
            boolean phoneEmpty = TextUtils.isEmpty(phone.getText().toString());
            boolean streetEmpty = TextUtils.isEmpty(street.getText().toString());
            boolean cityEmpty = TextUtils.isEmpty(city.getText().toString());
            boolean regionEmpty = TextUtils.isEmpty(region.getSelectedItem().toString());
            boolean postalEmpty = TextUtils.isEmpty(pCode.getText().toString());

            if (!(nameEmpty || phoneEmpty || streetEmpty || cityEmpty || regionEmpty ||postalEmpty)) {
                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString());
                params.put("phone", phone.getText().toString());
                params.put("street", street.getText().toString());
                params.put("city", city.getText().toString());
                params.put("region", region.getSelectedItem().toString());
                params.put("postalCode", pCode.getText().toString());
                params.put("notice", notice.getText().toString());
                params.put("longitude", "0");
                params.put("latitude", "0");
                String jsonString = new JSONObject(params).toString();

                try {
                    int responseCode = handler.post("/api/Contacts/set", jsonString);
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

        out.setOnClickListener(v -> {
            Objects.requireNonNull(getActivity()).finish();
            handler.clearCookies();
        });


        return view;
    }

    private void setupSpinner() throws IOException {
        String[] items = new String[]{getString(R.string.switzerland)};
        ArrayAdapter adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, items);
        country.setAdapter(adapter);

        JSONArray regions = handler.getJSONArrayParams3("/api/Regional/regions", "CH", "de");
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

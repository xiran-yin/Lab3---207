package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {
    private final Map<String, Map<String, String>> countryTranslations;
    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */

    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        countryTranslations = new HashMap<String, Map<String, String>>();
        // read the file to get the data to populate things...
        try {
            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject translationsObject = jsonArray.getJSONObject(i);
                String countryCode = translationsObject.getString("alpha2");

                List<String> languages = new ArrayList<>();
                Map<String, String> translations = new HashMap<String, String>();
                for (String key: translationsObject.keySet()) {
                    if (!"alpha3".equals(key) && !"id".equals(key) && !"alpha2".equals(key)) {
                        languages.add(key);
                        translations.put(key, translationsObject.getString(key));
                    }
                }
                countryTranslations.put(countryCode, translations);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        if (countryTranslations.containsKey(country)) {
            return new ArrayList<>(countryTranslations.get(country).keySet());
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(countryTranslations.keySet());
    }

    @Override
    public String translate(String country, String language) {
        if (countryTranslations.containsKey(country)) {
            Map<String, String> translations = countryTranslations.get(country);
            return translations.getOrDefault(language, null);
        }
        return null;
    }
}

package OfflineActivities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.R;
import com.example.viewpagertry2.TTSHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddCustomWordsActivity extends AppCompatActivity {
    private DBManager dbManager;
    private TextInputEditText unitNameEdit, wordsEdit;
    private MaterialButton saveButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_words);

        dbManager = new DBManager(this);
        dbManager.openDb();

        unitNameEdit = findViewById(R.id.unitNameEdit);
        wordsEdit = findViewById(R.id.wordsEdit);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.saveProgress);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> saveUnit());
    }

    private void saveUnit() {
        String unitName = unitNameEdit.getText().toString().trim();
        String wordsText = wordsEdit.getText().toString().trim();

        if (TextUtils.isEmpty(unitName)) {
            unitNameEdit.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(wordsText)) {
            wordsEdit.setError("Required");
            return;
        }

        saveButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            String[] lines = wordsText.split("\n");
            int count = 0;
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split by space or tab
                String[] parts = line.split("[\\s\\t]+", 2);
                if (parts.length < 2) continue;

                String p1 = parts[0].trim();
                String p2 = parts[1].trim();
                
                String word, meaning;
                // As requested: Russian is ALWAYS the word, English is the meaning
                if (containsRussian(p1)) {
                    word = p1;
                    meaning = p2;
                } else if (containsRussian(p2)) {
                    word = p2;
                    meaning = p1;
                } else {
                    // Fallback for English-English: p1=meaning, p2=word
                    meaning = p1;
                    word = p2;
                }

                dbManager.insertCustomWord(word, meaning, unitName);
                
                // Trigger audio download (fire and forget)
                TTSHelper.downloadAudio(AddCustomWordsActivity.this, word, null);
                count++;
            }

            final int finalCount = count;
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (finalCount > 0) {
                    Toast.makeText(AddCustomWordsActivity.this, "Added " + finalCount + " words to " + unitName, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    saveButton.setEnabled(true);
                    Toast.makeText(AddCustomWordsActivity.this, "No valid words found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private boolean containsRussian(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC) {
                return true;
            }
        }
        return false;
    }
}

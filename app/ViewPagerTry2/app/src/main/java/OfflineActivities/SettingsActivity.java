package OfflineActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import files.SettingsPrefs;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText questionsAmountEditText;
    private SwitchMaterial flipSwitch;
    private SwitchMaterial recordingOnlySwitch;
    private MaterialButton saveButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        questionsAmountEditText = findViewById(R.id.questionsAmountEditText);
        flipSwitch = findViewById(R.id.flipSwitch);
        recordingOnlySwitch = findViewById(R.id.recordingOnlySwitch);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        // Load current settings
        questionsAmountEditText.setText(String.valueOf(SettingsPrefs.getQuestionsAmount(this)));
        flipSwitch.setChecked(SettingsPrefs.isFlipped(this));
        recordingOnlySwitch.setChecked(SettingsPrefs.isRecordingOnly(this));

        saveButton.setOnClickListener(v -> saveSettings());
        backButton.setOnClickListener(v -> finish());
    }

    private void saveSettings() {
        String amountStr = questionsAmountEditText.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        SettingsPrefs.setQuestionsAmount(this, amount);
        SettingsPrefs.setFlipped(this, flipSwitch.isChecked());
        SettingsPrefs.setRecordingOnly(this, recordingOnlySwitch.isChecked());

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}

package OfflineActivities;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.viewpagertry2.R;
import com.google.android.material.card.MaterialCardView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialCardView premadeCard = findViewById(R.id.premadeUnitsCard);
        MaterialCardView customCard = findViewById(R.id.customUnitsCard);

        premadeCard.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, MenuOfflinePage.class);
            startActivity(intent);
        });

        customCard.setOnClickListener(v -> {
            // Will implement CustomUnitsActivity next
            Intent intent = new Intent(StartActivity.this, CustomUnitsActivity.class);
            startActivity(intent);
        });
    }
}

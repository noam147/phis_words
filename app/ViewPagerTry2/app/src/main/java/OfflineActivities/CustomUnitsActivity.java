package OfflineActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class CustomUnitsActivity extends AppCompatActivity {
    private DBManager dbManager;
    private RecyclerView recyclerView;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_units);

        dbManager = new DBManager(this);
        dbManager.openDb();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.unitsRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ExtendedFloatingActionButton addFab = findViewById(R.id.addUnitFab);
        addFab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCustomWordsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUnits();
    }

    private void loadUnits() {
        List<String> units = dbManager.getCustomUnits();
        if (units.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new UnitAdapter(units));
        }
    }

    private class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.ViewHolder> {
        private final List<String> units;

        UnitAdapter(List<String> units) {
            this.units = units;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String unit = units.get(position);
            holder.textView.setText(unit);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(CustomUnitsActivity.this, SortingWordsPage.class);
                intent.putExtra("customUnitName", unit);
                intent.putExtra("isEnglish", true);
                startActivity(intent);
            });
            holder.itemView.setOnLongClickListener(v -> {
                new MaterialAlertDialogBuilder(CustomUnitsActivity.this)
                        .setTitle("Delete Unit")
                        .setMessage("Are you sure you want to delete \"" + unit + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dbManager.deleteCustomUnit(unit);
                            loadUnits();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return units.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}

package OfflineActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;
import com.example.viewpagertry2.UnitAndCategoryOfWord;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class SeacrhWordInDbActivity extends AppCompatActivity {

    private DBManager dbManager;
    private WordSortAdapter adapter;
    private RecyclerView recyclerView;
    private String currRegexToSearch = "";
    private int currAmount;
    private boolean isSearchFlipped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seacrh_word_in_db);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbManager = new DBManager(this);
        dbManager.openDb();

        initViews();
        setupRecyclerView();
        addTextWatcher();
        
        // Initial search
        performSearch();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> exitImgButtonClick(v));
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_flip_search) {
                isSearchFlipped = !isSearchFlipped;
                updateSearchHint();
                performSearch();
                return true;
            }
            return false;
        });
        
        recyclerView = findViewById(R.id.searchRecyclerView);
        findViewById(R.id.loadMoreButton).setOnClickListener(v -> {
            currAmount += OperationsAndOtherUsefull.AMOUNT_OF_WORDS_EACH_TIME_SEARCHING;
            performSearch();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordSortAdapter(dbManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnWordClickListener(word -> {
            UnitAndCategoryOfWord unitAndC = new UnitAndCategoryOfWord(word.getWordProperties().getWord_id());
            Intent intent = new Intent(SeacrhWordInDbActivity.this, SortingWordsPage.class);
            intent.putExtra("unit", unitAndC.getUnit());
            intent.putExtra("category", unitAndC.getCategory());
            
            int finalAction = word.getUserDetailsOnWords().getAmountOfStars();
            if (finalAction > OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS) {
                finalAction = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
            }
            intent.putExtra("action", finalAction + 2);
            intent.putExtra("wordToMark", word.getWordProperties().getWord());
            startActivity(intent);
        });
    }

    private void addTextWatcher() {
        TextInputEditText searchEditText = findViewById(R.id.SearchWordEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                currAmount = OperationsAndOtherUsefull.AMOUNT_OF_WORDS_EACH_TIME_SEARCHING;
                performSearch();
            }
        });
    }

    private void performSearch() {
        TextInputEditText searchEditText = findViewById(R.id.SearchWordEditText);
        currRegexToSearch = searchEditText.getText().toString();
        
        FinalWordProperties[] words = dbManager.searchWordsBasedOnStart(currRegexToSearch, currAmount, isSearchFlipped);
        adapter.setWords(Arrays.asList(words));
        
        View loadMore = findViewById(R.id.loadMoreButton);
        if (words.length >= currAmount && words.length >= OperationsAndOtherUsefull.AMOUNT_OF_WORDS_EACH_TIME_SEARCHING) {
            loadMore.setVisibility(View.VISIBLE);
        } else {
            loadMore.setVisibility(View.GONE);
        }
    }

    private void updateSearchHint() {
        com.google.android.material.textfield.TextInputLayout layout = findViewById(R.id.searchTextInputLayout);
        if (isSearchFlipped) {
            layout.setHint("Meaning To Search");
        } else {
            layout.setHint("Word To Search");
        }
    }

    public void exitImgButtonClick(View view) {
        Intent intent = new Intent(this, MenuOfflinePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbManager != null) {
            dbManager.closeDb();
        }
    }
}

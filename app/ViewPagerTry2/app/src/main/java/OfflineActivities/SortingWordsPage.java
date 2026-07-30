package OfflineActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ExercisesPages.WordQuestionsPageMultipleAnswers;
import files.HistoryOfUnitAndCategoryPrefs;

public class SortingWordsPage extends AppCompatActivity {
    private static final int KEEP_UNIT_AND_CATEGORY_AS_IS = -1;

    private boolean isUserWantMeanings = true;
    private int currAction;
    private boolean isEnglish;
    private int unit;
    private int category;
    private DBManager dbManager;
    private String wordToMark = "";
    private String customUnitName = null;

    private FinalWordProperties[] allWordsInUnit;
    private WordSortAdapter adapter;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private Chip unitChip, categoryChip, meaningChip;
    private ExtendedFloatingActionButton testFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sorting_words_page);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setIntentData(getIntent());
        dbManager = new DBManager(this);
        dbManager.openDb();

        initViews();
        setupRecyclerView();
        loadData();
        setupListeners();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> exitImgButtonClick3(v));
        if (currAction == OperationsAndOtherUsefull.MARKED_WORDS_ACTION) {
            toolbar.setTitle("Marked Words");
        } else if (customUnitName != null) {
            toolbar.setTitle(customUnitName);
        }

        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.wordsRecyclerView);
        unitChip = findViewById(R.id.unitChip);
        categoryChip = findViewById(R.id.categoryChip);
        meaningChip = findViewById(R.id.meaningChip);
        testFab = findViewById(R.id.testFab);

        meaningChip.setChecked(isUserWantMeanings);
        unitChip.setText("Unit: " + unit);
        categoryChip.setText("Category: " + category);

        if (currAction == OperationsAndOtherUsefull.MARKED_WORDS_ACTION || customUnitName != null) {
            unitChip.setVisibility(View.GONE);
            categoryChip.setVisibility(View.GONE);
            if (currAction == OperationsAndOtherUsefull.MARKED_WORDS_ACTION) {
                tabLayout.setVisibility(View.GONE);
            }
        } else {
            // Map currAction to tab position
            int tabPos = 1; // Default to "To Sort"
            if (currAction == OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS) tabPos = 0;
            else if (currAction == OperationsAndOtherUsefull.DO_KNOW_WORDS) tabPos = 2;
            tabLayout.selectTab(tabLayout.getTabAt(tabPos));
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordSortAdapter(dbManager);
        adapter.setShowMeaning(isUserWantMeanings);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    WordSortAdapter.WordViewHolder holder = (WordSortAdapter.WordViewHolder) viewHolder;
                    boolean isRtl = recyclerView.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
                    
                    // Logic: swiping towards END is always towards 👍 (Known)
                    // In LTR: END is RIGHT (dX > 0)
                    // In RTL: END is LEFT (dX < 0)
                    boolean swipingTowardsEnd = (!isRtl && dX > 100) || (isRtl && dX < -100);
                    boolean swipingTowardsStart = (!isRtl && dX < -100) || (isRtl && dX > 100);

                    if (swipingTowardsEnd) {
                        holder.cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#E8F5E9")); // Light Green
                    } else if (swipingTowardsStart) {
                        holder.cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#FFEBEE")); // Light Red
                    } else {
                        holder.cardView.setCardBackgroundColor(com.google.android.material.color.MaterialColors.getColor(holder.cardView, com.google.android.material.R.attr.colorSurface));
                    }
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                WordSortAdapter.WordViewHolder holder = (WordSortAdapter.WordViewHolder) viewHolder;
                holder.cardView.setCardBackgroundColor(com.google.android.material.color.MaterialColors.getColor(holder.cardView, com.google.android.material.R.attr.colorSurface));
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                FinalWordProperties word = adapter.getWordAt(position);
                String wordStr = word.getWordProperties().getWord();

                if (direction == ItemTouchHelper.END) {
                    // Swipe towards END -> Move to KNOW (👍)
                    dbManager.setWordAsKnowWord(wordStr);
                    word.getUserDetailsOnWords().setAmountOfStars(1); // Update memory
                    Toast.makeText(SortingWordsPage.this, "Moved to Known", Toast.LENGTH_SHORT).show();
                } else {
                    // Swipe towards START -> Move to DON'T KNOW (👎)
                    dbManager.setWordAsDoesNOTKnowWord(wordStr);
                    word.getUserDetailsOnWords().setAmountOfStars(-1); // Update memory
                    Toast.makeText(SortingWordsPage.this, "Moved to Don't Know", Toast.LENGTH_SHORT).show();
                }
                adapter.removeWordAt(position);
                updateTabCounts();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setupListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currAction = OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS; break;
                    case 1: currAction = OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS; break;
                    case 2: currAction = OperationsAndOtherUsefull.DO_KNOW_WORDS; break;
                }
                filterAndDisplayWords();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        meaningChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isUserWantMeanings = isChecked;
            adapter.setShowMeaning(isChecked);
        });

        unitChip.setOnClickListener(v -> unitChooseButtonClicked(v));
        categoryChip.setOnClickListener(v -> categoryChooseButtonClicked(v));
        testFab.setOnClickListener(v -> TestOnSpecifWordsInUnitButtonClicked(v));
    }

    private void loadData() {
        if (currAction == OperationsAndOtherUsefull.MARKED_WORDS_ACTION) {
            allWordsInUnit = dbManager.getMarkedWords();
        } else if (customUnitName != null) {
            allWordsInUnit = dbManager.getWordsOfCustomUnit(customUnitName);
        } else {
            allWordsInUnit = dbManager.getWordsOfUnit(unit, category, isEnglish);
        }
        Arrays.sort(allWordsInUnit, Comparator.comparing(word -> word.getWordProperties().getWord()));
        filterAndDisplayWords();
    }

    private void filterAndDisplayWords() {
        List<FinalWordProperties> filteredList = new ArrayList<>();
        int scrollToIndex = -1;
        for (FinalWordProperties word : allWordsInUnit) {
            int stars = word.getUserDetailsOnWords().getAmountOfStars();
            boolean matches = false;
            if (currAction == OperationsAndOtherUsefull.MARKED_WORDS_ACTION) {
                matches = true;
            } else if (currAction == OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS && stars == -1) {
                matches = true;
            } else if (currAction == OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS && stars == 0) {
                matches = true;
            } else if (currAction == OperationsAndOtherUsefull.DO_KNOW_WORDS && stars > 0) {
                matches = true;
            }

            if (matches) {
                if (wordToMark != null && word.getWordProperties().getWord().equals(wordToMark)) {
                    scrollToIndex = filteredList.size();
                }
                filteredList.add(word);
            }
        }
        adapter.setWords(filteredList);
        adapter.setHighlightedWord(wordToMark);
        if (scrollToIndex != -1) {
            recyclerView.scrollToPosition(scrollToIndex);
        }
        updateTabCounts();
    }

    private void updateTabCounts() {
        if (currAction == OperationsAndOtherUsefull.MARKED_WORDS_ACTION) return;

        int dontKnow = 0, toSort = 0, know = 0;
        for (FinalWordProperties word : allWordsInUnit) {
            int stars = word.getUserDetailsOnWords().getAmountOfStars();
            if (stars == -1) dontKnow++;
            else if (stars == 0) toSort++;
            else if (stars > 0) know++;
        }

        TabLayout.Tab tab0 = tabLayout.getTabAt(0);
        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
        TabLayout.Tab tab2 = tabLayout.getTabAt(2);
        
        if (tab0 != null) tab0.setText("👎 (" + dontKnow + ")");
        if (tab1 != null) tab1.setText("To Sort (" + toSort + ")");
        if (tab2 != null) tab2.setText("👍 (" + know + ")");
    }

    protected void setIntentData(Intent intent) {
        currAction = intent.getIntExtra("action", OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS);
        if (currAction == OperationsAndOtherUsefull.ALL_WORDS_ACTION) {
            currAction = OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS;
        }

        unit = intent.getIntExtra("unit", 1);
        wordToMark = intent.getStringExtra("wordToMark");
        customUnitName = intent.getStringExtra("customUnitName");
        category = intent.getIntExtra("category", 1);
        if (currAction != OperationsAndOtherUsefull.MARKED_WORDS_ACTION && customUnitName == null) {
            HistoryOfUnitAndCategoryPrefs.updateUnitAndCategory(this, category, unit);
        }
        isEnglish = intent.getBooleanExtra("isEnglish", true);
        isUserWantMeanings = intent.getBooleanExtra("isUserWantMeanings", true);
    }

    public void categoryChooseButtonClicked(View view) {
        Intent intent = new Intent(this, CategoryChooser.class);
        intent.putExtra("isEnglish", isEnglish);
        intent.putExtra("action", currAction);
        intent.putExtra("isCategoryChoice", true);
        intent.putExtra("unit", unit);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void unitChooseButtonClicked(View view) {
        Intent intent = new Intent(this, CategoryChooser.class);
        intent.putExtra("isEnglish", isEnglish);
        intent.putExtra("action", currAction);
        intent.putExtra("isCategoryChoice", false);
        intent.putExtra("unit", unit);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void TestOnSpecifWordsInUnitButtonClicked(View view) {
        Intent intent = new Intent(this, WordQuestionsPageMultipleAnswers.class);
        if (currAction == OperationsAndOtherUsefull.MARKED_WORDS_ACTION || customUnitName != null) {
            intent.putExtra("questions", allWordsInUnit);
        } else {
            intent.putExtra("unit", unit);
            intent.putExtra("category", category);
        }
        intent.putExtra("isEnglish", isEnglish);
        intent.putExtra("action", this.currAction);
        startActivity(intent);
        finish();
    }

    public void exitImgButtonClick3(View view) {
        if (getIntent().getBooleanExtra("returnToSummary", false)) {
            finish();
            return;
        }
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

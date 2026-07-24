package OfflineActivities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.MakeViewPlayAudio;
import com.example.viewpagertry2.R;

import java.util.ArrayList;
import java.util.List;

public class WordSortAdapter extends RecyclerView.Adapter<WordSortAdapter.WordViewHolder> {

    public interface OnWordClickListener {
        void onWordClick(FinalWordProperties word);
    }

    private List<FinalWordProperties> words = new ArrayList<>();
    private final DBManager dbManager;
    private boolean showMeaning = true;
    private OnWordClickListener listener;
    private String highlightedWord = "";

    public WordSortAdapter(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setOnWordClickListener(OnWordClickListener listener) {
        this.listener = listener;
    }

    public void setHighlightedWord(String highlightedWord) {
        this.highlightedWord = highlightedWord;
        notifyDataSetChanged();
    }

    public void setWords(List<FinalWordProperties> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    public void setShowMeaning(boolean showMeaning) {
        this.showMeaning = showMeaning;
        notifyDataSetChanged();
    }

    public FinalWordProperties getWordAt(int position) {
        return words.get(position);
    }

    public void removeWordAt(int position) {
        words.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        words.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sort_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        FinalWordProperties word = words.get(position);
        holder.wordTextView.setText(word.getWordProperties().getWord());
        
        if (showMeaning) {
            holder.meaningTextView.setVisibility(View.VISIBLE);
            holder.meaningTextView.setText(word.getWordProperties().getMeaning());
        } else {
            holder.meaningTextView.setVisibility(View.GONE);
        }

        if (word.getWordProperties().getWord().equals(highlightedWord)) {
            holder.cardView.setStrokeColor(holder.itemView.getContext().getColor(R.color.red_orange));
            holder.cardView.setStrokeWidth(6);
        } else {
            holder.cardView.setStrokeWidth(0);
        }

        boolean isMarked = word.getUserDetailsOnWords().isWordMark();
        holder.markButton.setImageResource(isMarked ? R.drawable.baseline_check_24 : R.drawable.baseline_add_to_marked_words_24);

        holder.markButton.setOnClickListener(v -> {
            boolean currentMarked = word.getUserDetailsOnWords().isWordMark();
            boolean newMarked = !currentMarked;
            word.getUserDetailsOnWords().setWordMark(newMarked);
            dbManager.updateIsWordMarkedBasedOnWord(word.getWordProperties().getWord(), newMarked);
            
            holder.markButton.setImageResource(newMarked ? R.drawable.baseline_check_24 : R.drawable.baseline_add_to_marked_words_24);
            
            String message = newMarked ? "Word added to marked words" : "Word removed from marked words";
            Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
        });

        holder.audioButton.setOnClickListener(v -> {
            MakeViewPlayAudio.playRecordingOfWord(v.getContext(), 
                word.getWordProperties().getWord_id(), 
                word.getWordProperties().getWord());
        });

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWordClick(word);
            }
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.card.MaterialCardView cardView;
        TextView wordTextView, meaningTextView;
        ImageButton markButton, audioButton;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            wordTextView = itemView.findViewById(R.id.wordTextView);
            meaningTextView = itemView.findViewById(R.id.meaningTextView);
            markButton = itemView.findViewById(R.id.markButton);
            audioButton = itemView.findViewById(R.id.audioButton);
        }
    }
}

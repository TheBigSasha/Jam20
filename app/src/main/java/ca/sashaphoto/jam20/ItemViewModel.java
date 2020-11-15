package ca.sashaphoto.jam20;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ItemViewModel {
    private SuggestedItemRepository mRepository;

    private final LiveData<List<SuggestionItem>> mAllItems;

    public ItemViewModel (Application application) {
        super();
        mRepository = new SuggestedItemRepository(application);
        mAllItems = mRepository.getAllItems();
    }

    LiveData<List<SuggestionItem>> getAllWords() { return mAllItems; }

    public void insert(SuggestionItem item) { mRepository.insert(item); }
}

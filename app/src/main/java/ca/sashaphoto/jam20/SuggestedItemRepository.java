package ca.sashaphoto.jam20;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SuggestedItemRepository {
    public SuggestedItemDAO mItemDao;
    private LiveData<List<SuggestionItem>> mAllItems;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    SuggestedItemRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mItemDao = db.wordDao();
        mAllItems = mItemDao.getSuggestedItems();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<SuggestionItem>> getAllItems() {
        return mAllItems;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(SuggestionItem items) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mItemDao.insert(items);
        });
    }

    public void delete(SuggestionItem suggestionItem) {
        mItemDao.delete(suggestionItem);
    }
}

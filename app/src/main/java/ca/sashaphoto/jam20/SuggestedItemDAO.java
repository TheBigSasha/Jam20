package ca.sashaphoto.jam20;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SuggestedItemDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SuggestionItem item);

    @Query("SELECT * FROM SuggestedItems")
    LiveData<List<SuggestionItem>> getSuggestedItems();

    @Delete
    void delete(SuggestionItem item);

    @Query("SELECT * FROM SuggestedItems WHERE content == (:content)")
    LiveData<SuggestionItem> getItemByContent(String content);

}

package ca.sashaphoto.jam20;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;

public class JamBackend {
    private static final String[] hardCodes = new String[]{"Text a friend", "Make a pumpkin pie", "Watch a Movie" ,"Watch The Bee Movie", "Learn what a REST API is", "Binge a series", "Read an Educational Book", "Go on a walk",
            "Write a letter to a friend", "Make a cocktail", "Paint", "Write a Poem", "Learn Calligraphy", "Knit a Scarf", "Learn to Program", "Start a Blog", "Research your Family Tree",
            "Learn a Language", "Start a Collection", "Watch a TED Talk", "Watch a documentary", "Listen to a Podcast", "Take an Online Course", "Get a Gecko", "Grow a Plant", "Start a Garden",
            "Educate yourself about birds and embark on a \"Big Year\" in your city", "Get UberEats tonight", "Meditate", "Exercise", "Get into the habit of reading the news", "Become great at charades",
            "Sumo wrestle your roommate by tying a pillow to your arms, legs, and back", "Learn to ballroom dance", "Clean your closet", "Paint your nails", "Perfect your latte making skills", "Buy something at IKEA",
            "Take a Bath", "Try a new Recipe", "Do a puzzle", "Call a Relative and Get an Old Family Recipe to Try", "Have a Spa Day", "Learn Martial Arts so you can say \"I know Karate\"" };

    private static final Random rand = new Random(System.nanoTime());

    List<String> list = new LinkedList<>(Arrays.asList(hardCodes));

    private static JamBackend ref = new JamBackend();


    private String getSuggestion() {
        String string;
        if (list.size() > 0) {
            int i = rand.nextInt(list.size());
            string = list.get(i);
            list.remove(i);
        }
        else {
            list = new LinkedList<>(Arrays.asList(hardCodes));
            int i = rand.nextInt(list.size());
            string = list.get(i);
            list.remove(i);
        }
        return string;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getCurrentSuggestion(){
        if(SuggestionItem.hasCurrent()) {
            return SuggestionItem.get().getContent();
        }else{
            return fetchNewSuggestion();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String fetchNewSuggestion(){
        if(SuggestionItem.hasCurrent()) SuggestionItem.get().dismiss();
        SuggestionItem.create(getSuggestion());
        //TODO: JamWidget.updateAppWidget();
        return SuggestionItem.get().getContent();
    }

    /**
     * Fetches a new suggestion, and takes into account the goodness of the last one.
     * @param wasGood   whether the last one was good
     * @return          new suggestion
     */
    public String fetchNewSuggestion(boolean wasGood){
        if(SuggestionItem.hasCurrent()) SuggestionItem.get().dismiss(wasGood);
        SuggestionItem.create(getSuggestion());
        //TODO: JamWidget.updateAppWidget();
        return SuggestionItem.get().getContent();
    }


    public static JamBackend build(){
        if(ref == null) ref = new JamBackend();
        return ref;
    }

    public boolean hasSuggestion() {
        return SuggestionItem.hasCurrent();
    }
}
@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(tableName = "SuggestedItems")
class SuggestionItem{

    @NonNull
    public Boolean getGood() {
        return isGood;
    }

    public void setGood(@NonNull Boolean good) {
        isGood = good;
    }




        private Boolean isGood;

    public void setCreated(String created) {
        this.created = created;
    }

    public void setFirstShown(String firstShown) {
        this.firstShown = firstShown;
    }

    public void setFirstDismissed(String firstDismissed) {
        this.firstDismissed = firstDismissed;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String created;

    public String getCreated() {
        return created;
    }

    public String getFirstShown() {
        return firstShown;
    }

    public String getFirstDismissed() {
        return firstDismissed;
    }

    private String firstShown;
        private String firstDismissed;
        private static SuggestionItem activeItem;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "content")
        private String content;

    public SuggestionItem(@NonNull Boolean isGood ){
        this.isGood = isGood;
    }


    public Time getTimeCreated() {
        return Time.valueOf(created);
    }

    public Time getTimeFirstShown() {
        return Time.valueOf(firstShown);
    }

    public Time getTimeFirstDismissed() {
        return Time.valueOf(firstDismissed);
    }

    public String getContent() {
        return content;
    }

    protected SuggestionItem(String suggestion){
        content = suggestion;
        created = String.valueOf(LocalDateTime.now().toLocalTime());

        MainActivity.suggestedItemRepository.mItemDao.insert(this);

        if(activeItem != null) activeItem.dismiss();
        activeItem = this;
    }

    public static boolean hasCurrent(){
        return activeItem != null;
    }

    public String show(){
        if(firstShown == null){
            firstShown = String.valueOf(LocalDateTime.now().toLocalTime());
        }
        activeItem = this;
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestionItem that = (SuggestionItem) o;
        return Objects.equals(created, that.created) &&
                Objects.equals(firstShown, that.firstShown) &&
                Objects.equals(firstDismissed, that.firstDismissed) &&
                content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(created, firstShown, firstDismissed, content);
    }

    public boolean isActive(){
        return this == activeItem;
    }

    public void dismiss(){
        if(this != activeItem) throw new IllegalArgumentException("Cannot dismiss an inactive item");
        if(firstDismissed == null){
            firstDismissed = String.valueOf(LocalDateTime.now().toLocalTime());
        }
        activeItem.isGood = false;
        MainActivity.suggestedItemRepository.mItemDao.insert(activeItem);

        activeItem = null;


    }


    public void dismiss(boolean good){
        if(this != activeItem) throw new IllegalArgumentException("Cannot dismiss an inactive item");
        if(firstDismissed == null){
            firstDismissed = String.valueOf(LocalDateTime.now().toLocalTime());
        }

        if(good){
            activeItem.isGood = true;
            MainActivity.suggestedItemRepository.mItemDao.insert(activeItem);
        }else{
            activeItem.isGood = false;
            MainActivity.suggestedItemRepository.mItemDao.insert(activeItem);
        }

        activeItem = null;
    }


    public static SuggestionItem create(String message){
        SuggestionItem item = MainActivity.suggestedItemRepository.mItemDao.getItemByContent(message).getValue();
        if(item != null){
            item.setActive();
            activeItem = item;
            MainActivity.suggestedItemRepository.mItemDao.delete(item);
        }

        return new SuggestionItem(message);
    }

    private void setActive() {
        firstDismissed = null;
        firstShown = null;
        if(activeItem != null) {
            activeItem.dismiss();
            activeItem = this;
        }

        MainActivity.suggestedItemRepository.delete(this);
    }

    public static SuggestionItem get(){
        return activeItem;
    }

}

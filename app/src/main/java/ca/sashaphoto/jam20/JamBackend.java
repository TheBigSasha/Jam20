package ca.sashaphoto.jam20;

import android.os.Build;

import androidx.annotation.RequiresApi;

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

    public static JamBackend build(){
        if(ref == null) ref = new JamBackend();
        return ref;
    }

    public boolean hasSuggestion() {
        return SuggestionItem.hasCurrent();
    }
}
@RequiresApi(api = Build.VERSION_CODES.O)
class SuggestionItem{
    private static HashMap<String, SuggestionItem> items = new HashMap<>();
    private static ArrayList<SuggestionItem> badItems = new ArrayList<>();
    private static ArrayList<SuggestionItem> goodItems = new ArrayList<>();
    private LocalDateTime created;
    private LocalDateTime firstShown;
    private LocalDateTime firstDismissed;
    private static SuggestionItem activeItem;
    private String content;

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getFirstShown() {
        return firstShown;
    }

    public LocalDateTime getFirstDismissed() {
        return firstDismissed;
    }

    public String getContent() {
        return content;
    }

    protected SuggestionItem(String suggestion){
        content = suggestion;
        created = LocalDateTime.now();
        items.put(suggestion,this);
        if(activeItem != null) activeItem.dismiss();
        activeItem = this;
    }

    public static boolean hasCurrent(){
        return activeItem != null;
    }

    public String show(){
        if(firstShown == null){
            firstShown = LocalDateTime.now();
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
            firstDismissed = LocalDateTime.now();
        }
        badItems.add(activeItem);

        activeItem = null;
    }

    public void dismiss(boolean good){
        if(this != activeItem) throw new IllegalArgumentException("Cannot dismiss an inactive item");
        if(firstDismissed == null){
            firstDismissed = LocalDateTime.now();
        }

        if(good){
            goodItems.add(activeItem);
        }else{
            badItems.add(activeItem);
        }

        activeItem = null;
    }

    public static SuggestionItem getInactive(String message){
        if(items.containsKey(message)) return items.get(message);

        return new SuggestionItem(message);
    }

    public static SuggestionItem create(String message){
        if(items.containsKey(message)){
            SuggestionItem item = items.get(message);
            item.setActive();
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
        goodItems.remove(this);
        badItems.remove(this);
    }

    public static SuggestionItem get(){
        return activeItem;
    }

}

package ca.sashaphoto.jam20;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import io.radar.sdk.Radar;
import io.radar.sdk.model.RadarCoordinate;
import io.radar.sdk.model.RadarPlace;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class JamBackend {
    private static final String[] hardCodes = new String[]{"Text a friend", "Make a pumpkin pie", "Watch a Movie" ,"Watch The Bee Movie", "Learn what a REST API is", "Go on a walk",
            "Write a letter to a friend", "Make a cocktail", "Paint the view from your window", "Write a Poem", "Learn Calligraphy", "Knit a Scarf", "How do you make a program that recites n bottles of beer on the wall",
            "How do birds keep track of their altitude?", "Who's your great uncle's father?", "How do you say hello world in Cantonese?", "How many rare Pokemon cards are there", "Listen to a Podcast","Grow a Plant", "Start a Garden",
            "Educate yourself about birds and embark on a \"Big Year\" in your city", "Ever tried fish tacos?", "What are the health benefits of meditation?", "What's going on in the world today?", "How do I become great at charades",
            "Sumo wrestle your roommate by tying a pillow to your arms, legs, and back", "Where do I learn to ballroom dance", "How can I clean my closet", "How can people paint such detailed pictures on their nails?", "How do people do latte art?",
            "How to level up your bath?", "How do I cook pumpkin bread?", "Call Mom, it's been a while!",  "Learn Martial Arts so you can say \"I know Karate\"", "Check out 3 Idiots, it's a great film!" };

    private static final Random rand = new Random(System.nanoTime());

    ConcurrentLinkedDeque<SuggestionItem> list = new ConcurrentLinkedDeque<>();
    static         ArrayList<SuggestionItem> hardCodeList = new ArrayList<>();


    private static void fillHardCodes(){
        for(String s : hardCodes){
            hardCodeList.add(new SuggestionItem(s));
        }
    }




    private static JamBackend ref = new JamBackend();


    private String getSuggestion() {
        try {
            Radar.getLocation(new Radar.RadarLocationCallback() {
                @Override
                public void onComplete(Radar.RadarStatus status, Location location, boolean stopped) {
                    // do something with location
                    Radar.searchPlaces(
                            location,
                            1000, // radius (meters)
                            new String[]{"starbucks"}, // chains
                            //, "religion", "outdoor-places", "food-beverage", "arts-entertainment"
                            null, // categories
                            null, //groups
                            10, // limit
                            new Radar.RadarSearchPlacesCallback() {
                                @Override
                                public void onComplete(Radar.RadarStatus status, Location location, RadarPlace[] places) {
                                    try {
                                        for (RadarPlace place : places) {
                                            Location l = new Location(location.getProvider());
                                            l.setLongitude(place.getLocation().getLongitude());
                                            l.setLatitude(place.getLocation().getLatitude());
                                            SuggestionItem item = SuggestionItem.create("Ever explored " + place.getName() + " nearby?").addLocation(place.getLocation());
                                            if (item != null && !SuggestionItem.pastItems.containsValue(item) && item.getLocation() != null) {
                                                try {
                                                    item.getLocation().getLatitude();
                                                } catch (Exception e) {
                                                    break;
                                                }
                                                list.add(item);
                                            }
                                        }
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                    );

                }
            });
        }catch (Exception ex){

        }


        SuggestionItem item = null;

        int counter = 0;
        while((item == null || SuggestionItem.wasShown(item.getContent())) && counter < 10){

            ArrayList<SuggestionItem> tempList = new ArrayList();
            for(SuggestionItem si : list){
                if(si != null) tempList.add(si);
            }
            if (!tempList.isEmpty()) {
                item = tempList.get(tempList.size()-1);
                tempList.remove(item);
                list.remove(item);
            }
            //TODO: Check past entries with persistence! For now we just save a database and do nothing with it.
            else {
                if(!hardCodeList.isEmpty()) {
                    int i = rand.nextInt(hardCodeList.size());
                    item = hardCodeList.get(i);
                    hardCodeList.remove(item);
                }else{
                    fillHardCodes();
                    return getSuggestion();
                }
            }
            counter++;
        }
        item.show();
        return item.getContent();
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
        if(SuggestionItem.hasCurrent()) SuggestionItem.get().dismiss(false);
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
class SuggestionItem implements Comparable<SuggestionItem> {

    static  HashMap<String, SuggestionItem> pastItems = new HashMap<>();

    public static void createAsync(String suggestion) {

        String result;
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                return create(strings[0]).getContent();
            }

            @Override
            protected void onPostExecute(String s) {
            }
        };
         task.execute(suggestion);
    }

    @NonNull
    public Boolean getGood() {
        return isGood;
    }

    public void setGood(@NonNull Boolean good) {
        isGood = good;
    }

    public void setLocation(RadarCoordinate location) {
        this.location = location;
    }

    public RadarCoordinate getLocation() {
        return location;
    }

    private transient RadarCoordinate location;



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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestionItem that = (SuggestionItem) o;
        return Objects.equals(location, that.location) &&
                Objects.equals(isGood, that.isGood) &&
                Objects.equals(created, that.created) &&
                Objects.equals(firstShown, that.firstShown) &&
                Objects.equals(firstDismissed, that.firstDismissed) &&
                content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, isGood, created, firstShown, firstDismissed, content);
    }

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

    public SuggestionItem addLocation(RadarCoordinate l){
        this.setLocation(l);
        return this;
    }

    public String show(){
        if(firstShown == null){
            firstShown = String.valueOf(LocalDateTime.now().toLocalTime());
        }
        setActive();
        return content;
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
        pastItems.put(this.getContent(),this);
        if(activeItem != null) {
            activeItem.dismiss();
            activeItem = this;
        }
        try {
            MainActivity.suggestedItemRepository.delete(this);
        }catch (Exception ignored){}
        }

    public static SuggestionItem get(){
        return activeItem;
    }

    public static boolean wasShown(String message){
        return pastItems.containsKey(message);
    }

    public void dismissAsync(boolean wasGood) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Boolean, Void, Boolean> task = new AsyncTask<Boolean, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                dismiss(booleans[0]);
                return true;
            }
        };

        task.execute(isGood);
    }

    @Override
    public int compareTo(SuggestionItem o) {
        return (int) (o.getTimeCreated().getTime() - this.getTimeCreated().getTime());
    }
}

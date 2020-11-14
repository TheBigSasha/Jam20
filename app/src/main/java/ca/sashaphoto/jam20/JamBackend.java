package ca.sashaphoto.jam20;

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


    public String getSuggestion() {
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

    public static JamBackend build(){
        if(ref == null) ref = new JamBackend();
        return ref;
    }
}

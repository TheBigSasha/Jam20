package ca.sashaphoto.jam20;

import java.util.Random;

public class JamBackend {
    private static final String[] hardCodes = new String[]{"Text a friend", "Make a pumpkin pie", "Learn what a REST API is"};
    private static final Random rand = new Random(System.nanoTime());
    public String getSuggestion() {
        return hardCodes[rand.nextInt(hardCodes.length -1)];
        //TODO: Write this!
    }
}

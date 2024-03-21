package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    private static final int NUM_STRINGS = 37;
    private static final double BASE_FREQUENCY = 440.0;
    private static final double OFFSET = 24.0;
    private static final double DIVIDED = 12.0;

    public static void main(String[] args) {

        GuitarString[] concertArray = new GuitarString[NUM_STRINGS];

        for (int i = 0; i < NUM_STRINGS; i += 1) {
            concertArray[i] = new GuitarString(BASE_FREQUENCY * Math.pow(2, (i - OFFSET) / DIVIDED));
        }
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (!(keyboard.contains(String.valueOf(key)))) {
                    continue;
                } else {
                    int index = keyboard.indexOf(key);
                    concertArray[index].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0;
            for (int i = 0; i < NUM_STRINGS; i += 1) {
                sample += concertArray[i].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < NUM_STRINGS; i += 1) {
                concertArray[i].tic();
            }
        }
    }
}



package gh2;

import deque.Deque;

public class Harp extends GuitarString {
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private Deque<Double> buffer;

    public Harp(double frequency) {
        super(frequency / 2);
    }

    public void tic() {
        double front = buffer.removeFirst();
        double newSample = (front + buffer.get(0)) * DECAY * 0.5;
        buffer.addLast(-newSample);
    }

}

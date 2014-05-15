package com.sonyseng.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

// The Model object to store and transfer data to the client
public final class Game implements Serializable {
	private static final long serialVersionUID = -4508242717561702142L;
	
	// Ideally, this should be an enum that the client can know about
	private static final int STATE_WON = 1;
	private static final int STATE_LOST = -1;
	private static final int STATE_PLAYABLE = 0;
	
	private final int id;
    private final int letterCount;
	private final String word;

    private int remainingGuesses;
    private int state;
    private String currentGuess;
    
    public Game(int id, String word) {
        this.id = id;
        this.word = word;
        this.remainingGuesses = 10;
        this.currentGuess = word.replaceAll(".", "_");
        this.letterCount = word.length();
        this.state = STATE_PLAYABLE;
    }

    // Don't let the client know what the actual word is
    @JsonIgnore public String getWord() { return word; }
    
    public int getId() { return id; }   
    public int getLetterCount() { return letterCount; } 
    public int getRemainingGuesses() { return remainingGuesses; }
    public int getState() { return state; };
    
    public String getCurrentGuess() { return currentGuess; }
	public boolean isPlayable() { return state == STATE_PLAYABLE; }
	
	private void reduceRemainingGuesses() { remainingGuesses--; }
	
	// A guess is correct when the letter exists and hasn't already been played
	public void makeGuess(String letter) {
		if (word.contains(letter) && !currentGuess.contains(letter)) {
			updateCurrentGuess(letter);
		} else {
			reduceRemainingGuesses();
		}
		
		// Always keep the state uo to date after a guess
		updateState();
	}
	
	private void updateState() {
		if (word.equals(currentGuess)) {
			state = STATE_WON;
		} else if (remainingGuesses <= 0) {
			state = STATE_LOST;
		}
	}
	
	// Keep track of the where the guessed letters fit into the word
	private void updateCurrentGuess(String letter) {
		StringBuffer sb = new StringBuffer(currentGuess);
		char character = letter.toCharArray()[0];
		int index = word.indexOf(letter);
		
		while (index >= 0) {
		    sb.setCharAt(index, character);
		    index = word.indexOf(character, index + 1);
		}
		
		currentGuess = sb.toString();
	}	
	
}

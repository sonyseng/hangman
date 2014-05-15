package com.sonyseng.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.sonyseng.exceptions.ResourceNotFoundException;
import com.sonyseng.model.Game;

// Notes: Synchronization on session mutations should be enforced. Our shared store is the session object. 
// Each user gets their own session object but each user may make many concurrent reads/writes for themselves.

@SuppressWarnings("unchecked")
@Controller
public class GameController {
    // Shared only use it as a read only variable
    private List<String> randomWordList;
    private @Autowired ServletContext servletContext;
    private Random randomGen;
    
    @PostConstruct
    public void initialize() {
    	this.randomGen = new Random(System.nanoTime());
    	initializeRandomWordList();
    }
    
    // Get a list of games that the player is currently or has played in the browser 
    // during the current session. Useful for stats later on if that's required.
    @RequestMapping("/games")
    public @ResponseBody List<Game> getGameList(HttpSession session) {
    	List<Game> games = (List<Game>) session.getAttribute("games");
        return games;
    }
     
    // Get an existing game resource
    @RequestMapping(value = "/game/{id}", method=RequestMethod.GET)
    public @ResponseBody Game getGame(@PathVariable int id, HttpSession session) {
    	Game game = getGameInSession(id, session);    	
    	return game;
    }
    
    // Updates a game resource. This is the part where the player makes guesses.
    @RequestMapping(value = "/game/{id}", method=RequestMethod.PUT)
    public @ResponseBody Game makeGuess(@PathVariable int id, 
    		@RequestParam(value="letter", required=true) String letter, HttpSession session) {
    	
    	Game game = getGameInSession(id, session);
    	
    	if (game.isPlayable()) {
    		// Sanitize the input. In case the player sends more than one character
    		String singleLetter = String.valueOf(letter.charAt(0));
	    	game.makeGuess(singleLetter);
    	}
    	
    	return game;
    }    
    
    // Creates a new game resource
    @RequestMapping(value="/game", method=RequestMethod.POST)
    public @ResponseBody Game createGame(HttpSession session) {
    	List<Game> games = createOrGetGamesInSession(session);
    	int gameIndex = games.size();
    	
    	// Create a game with a random word
    	Game newGame = new Game(gameIndex, randomWordList.get(randomGen.nextInt(randomWordList.size())));
    	
    	// Add the game to the list of played games and indirectly into the session
    	games.add(newGame);
    	
        return newGame;
    }
    
    // Note: Ideally should do some synchronization here to avoid race conditions 
    // when the same client makes concurrent requests.
    public List<Game> createOrGetGamesInSession(HttpSession session) {
    	List<Game> games = (List<Game>) session.getAttribute("games");
    	
    	if (games == null) {
    		games = new ArrayList<Game>();
    		session.setAttribute("games", games);
    	}
    	
    	return games;
    }
    
    // Find an existing game but send back a 404 if none found
    public Game getGameInSession(int id, HttpSession session) {
    	List<Game> games = (List<Game>) session.getAttribute("games");
    	
    	if (games == null || id >= games.size() || games.get(id) == null) {
    		throw new ResourceNotFoundException();
    	}
    	
    	return games.get(id);
    }
    
    // Load all the words from our dictionary
    private void initializeRandomWordList() {   	
    	InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/classes/dictionary.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		try {
			this.randomWordList = new ArrayList<String>();
			
			String line;
			while ((line = reader.readLine()) != null) { this.randomWordList.add(line);	}
			
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			try {
				if (reader != null) { reader.close(); }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}      	
    }
}




$(function() {
	
	// NOTE: This is a quick and dirty UI for the backend. In normal situations, 
	// there would be separation between the game controller, the view, and the game model.
	
	var newGameButton = $('#newGameButton'),
		guessButton = $('#guessButton'),
		letterInputBox = $('#letterInput'),
		caption = $('#caption'),
		currentGuess = $('#currentGuess'),
		gamePanel = $('#gamePanel'),
		message = $('#message'),
		hangmanImage = $('#hangmanImage'),
		stats = $('#stats'),
		currentGame = {},
		gamesPlayed = 0,
		gamesWon = 0;
	
	newGameButton.click(createNewGame);
	guessButton.click(makeGuess);
	
	function createNewGame () {
		$.ajax({
			url : "game",
			type: 'POST'
		}).done(function (game) {
			currentGame = game;
			renderStats();
			renderGame(game);
			gamePanel.removeClass('hidden');
			message.text('');
		});		
	}
	
	function makeGuess() {
		$.ajax({
			url : evalTempl('game/{id}?letter={letter}', {id: currentGame.id, letter: letterInputBox.val()}),
			type: 'PUT'
		}).done(function (game) {
			renderGame(game);
			checkGameState(currentGame, game);
			currentGame = game;
			renderStats();
			letterInputBox.focus();
			letterInputBox.val('');
		});
	}
	
	function checkGameState(currentGame, game) {
		if (game.state === 1) {
			message.text('You won!');
			gamesWon++;
			gamesPlayed++;
		} else if (game.state === -1) {
			message.text('You lost!');
			gamesPlayed++;
		} else if (game.remainingGuesses === currentGame.remainingGuesses) {
			message.text('Good Job. Keep going!');
		} else if (game.remainingGuesses < currentGame.remainingGuesses) {
			message.text('Keep trying, but think carefully!');
		}
	}
	
	function renderGame(game) {
		caption.text(evalTempl("You have {remainingGuesses} tries. Guess this {letterCount} letter word: ", game));
		currentGuess.text(game.currentGuess);
		hangmanImage.attr('src', evalTempl('resources/img/{imageIndex}.png', {imageIndex: 10-game.remainingGuesses})); 
	}
	
	function renderStats() {
		stats.text(evalTempl('{gamesWon} game(s) won. {gamesPlayed} game(s) played.', {gamesWon: gamesWon, gamesPlayed: gamesPlayed}));
	}
	
	// Useful string replace util for basic templating
	function evalTempl (str, context) {
		return str.replace(/\{(.*?)\}/g, function(match, key) {return context[key];});
	}
});

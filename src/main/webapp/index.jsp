<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<meta charset="utf-8">
<title>Welcome to Hang That Developer!</title>
<script type="text/javascript" src="resources/js/jquery-1.11.1.js"></script>
<script type="text/javascript" src="resources/js/main.js"></script>

<link rel="stylesheet" href="resources/css/bootstrap.css" />
<link rel="stylesheet" href="resources/css/main.css" />

</head>
<body>
	<section class="jumbotron">
		<h1>Hang That Developer!</h1>
		<p>The fun game where developers try to guess a word. If that developer doesn't know how to spell... Well he's going to be
			Technical Debt pretty soon!</p>
		<p>
			<a id="newGameButton" class="btn btn-primary btn-lg">Start A New Game</a>
			<span id="stats"></span>
		</p>
	</section>

	<section id="gamePanel" class="panel panel-default hidden">
		<div class="panel-body">
			<span id="caption">You have {10} tries. Guess this {7} letter word:</span> 
			<div id="currentGuess">G__GLE</div>
			
			<div class="inputWrapper">
				<input id="letterInput" type="text" placeholder="Enter your letter here"/>
				<a id="guessButton" class="btn btn-primary">Guess</a>
			</div>
		</div>
		
		<div>
			<p id="message"></p>
			<img id="hangmanImage" src="resources/img/0.png" alt="hangman image"/>
			<div><small>Picture taken from http://www.thegamegal.com/ and modified for this app</small></div>
		</div>
	</section>

</body>
</html>

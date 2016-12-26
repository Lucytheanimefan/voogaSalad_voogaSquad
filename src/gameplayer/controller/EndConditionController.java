// This entire file is part of my masterpiece.
// Lucy Zhang (lz107)

package gameplayer.controller;

import java.util.Queue;

import gameplayer.model.GamePlayData;
import gameplayer.model.GamePlayModel;
import gameplayer.model.enemy.Enemy;
import gameplayer.model.enemy.EnemyManager;
import gameplayer.view.GameGUI;
import gameplayer.view.helper.GraphicsLibrary;

/**
 * This controller is responsible for triggering the end conditions of a game or
 * round. It has methods distinguishing when it is the end of a level versus the
 * end of a game. The game over and win game conditions consist of splashscreens
 * displaying text and images. The conditions for detecting whether a game has
 * been won or lost are constantly being checked as tower defense is a timed
 * game, with waves of enemies coming in at specific intervals and weapons
 * killing off enemies at largely varying intervals. This class is well
 * designed. The methods are named exactly for their purposes. There is no
 * ambiguity. The majority of the functionality is encapsulated, with the small
 * exception of the method that needs to be called inside the main timeline
 * animation. The only public method is incredibly easy to understand thanks to
 * the numerous private methods used to facilitate, the, what I like to call,
 * in-code documentation.
 * 
 * @author lucyzhang
 *
 */
public class EndConditionController {
	private GamePlayModel model;
	private GameGUI view;
	private GraphicsLibrary graphics;
	private EnemyManager enemyManager;
	private Queue<Enemy> currentWave;
	private GamePlayData data;

	public EndConditionController(GamePlayModel model, GameGUI view) {
		this.model = model;
		this.enemyManager = this.model.getEnemyManager();
		this.view = view;
		this.graphics = new GraphicsLibrary();
		this.currentWave = this.enemyManager.getPackOfEnemyComing();
		this.data = this.model.getData();
	}

	private void gameOver() {
		endCondition(GameGUI.GAMEOVER_SCREEN);
	}

	private void winGame() {
		endCondition(GameGUI.WINGAME_SCREEN);
	}

	/**
	 * Creates the webview for the endscreen splashscreen
	 * 
	 * @param url
	 */
	private void endCondition(String url) {
		this.view.getMainScreen().getChildren().clear();
		this.view.getMainScreen().setCenter(graphics.createBrowser(url));
	}

	private boolean loseCondition() {
		return (this.model.getData().getLife() <= 0);
	}

	private boolean noMoreEnemies() {
		return (enemyManager.getEnemyOnGrid().size() == 0 && currentWave.size() == 0);
	}

	private boolean winCondition() {
		return (data.won()
				|| (data.getLife() > 0 && noMoreEnemies() && data.getCurrentLevel() >= data.getLevelNumber()));
	}

	/**
	 * Checks for whether or not a new level should be triggered, and if so
	 * creates the new level by setting the level and triggering the frontend
	 * element to prompt the user to move on to the next level. Calling setLevel
	 * triggers an observer that immediately knows to prepare for the new level.
	 */
	public void checkCreateForNewLevel() {
		if (noMoreEnemies()) {
			data.setLevel(data.getCurrentLevel() + 1);
			this.view.newLevelPopUp(e -> {
				this.model.initializeLevelInfo();
				this.view.getMainScreen().getChildren().clear();
			});
		}
	}

	/**
	 * This method executes the win or lose splashscreen depending on which
	 * condition is satisfied. The method is used in GamePlayerController,
	 * constantly checking if a loser has won or lost and behaving accordingly.
	 */
	public void executeWinOrLose() {
		if (loseCondition()) {
			gameOver();
		} else if (winCondition()) {
			winGame();
		}
	}

}

package com.webalicioustech.moocgame;

//Other parts of the android libraries that we use
import com.webalicioustech.moocgame.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer; //enables sound effects

public class TheGame extends GameThread {
	
	// Initialise the level number
	private int levelNo = 1;
	
	// Initialise the number of lives
	private int noOfLives = 3;
	
	// Will store the image of a ball
	private Bitmap mBall;

	// The X and Y position of the ball on the screen (middle of ball)
	private float mBallX = 0;
	private float mBallY = 0;

	// The speed (pixel/second) of the ball in direction X and Y
	private float mBallSpeedX = 0;
	private float mBallSpeedY = 0;

	// Will store the image of the paddle
	private Bitmap mPaddle;

	private float mPaddleX = 0;

	private float mMinDistanceBetweenRedBallAndBigBall = 0;

	// Will store the image of the smiley ball
	private Bitmap mSmileyBall;

	// The X and Y position of the smiley ball on the screen (middle of ball)
	private float mSmileyBallX = -100;
	private float mSmileyBallY = -100;

	private float mMinDistanceBetweenRedBallAndSmileyBall = 0;

	// Will store the image of the sad ball
	private Bitmap mSadBall;

	// The X and Y position of the sad ball on the screen (middle of ball)
	private float mSadBallX = -100;
	private float mSadBallY = -100;

	private float mMinDistanceBetweenRedBallAndSadBall = 0;

	// Create a media player object for each sound in the game
	private MediaPlayer mLoseSound;
	private MediaPlayer mHitPaddle;
	private MediaPlayer mScoreSound;
	private MediaPlayer mStartSound;
	private MediaPlayer mBounceSound;

	// This is run before anything else, so we can prepare things here
	public TheGame(GameView gameView) {
		// House keeping
		super(gameView);

		// Prepare the ball image so we can draw it on the screen (using a
		// canvas)
		mBall = BitmapFactory.decodeResource(gameView.getContext()
				.getResources(), R.drawable.small_red_ball);

		// Prepare the paddle image so we can draw it on the screen (using a
		// canvas)
		mPaddle = BitmapFactory.decodeResource(gameView.getContext()
				.getResources(), R.drawable.yellow_ball);

		// Prepare the smiley ball image so we can draw it on the screen (using
		// a canvas)
		mSmileyBall = BitmapFactory.decodeResource(gameView.getContext()
				.getResources(), R.drawable.smiley_ball);

		// Prepare the sad ball image so we can draw it on the screen (using a
		// canvas)
		mSadBall = BitmapFactory.decodeResource(gameView.getContext()
				.getResources(), R.drawable.sad_ball);

		// Load sound clip for each media player
		mLoseSound = MediaPlayer.create(gameView.getContext(),
				R.raw.sadtrombone);
		mHitPaddle = MediaPlayer.create(gameView.getContext(), R.raw.click);
		mScoreSound = MediaPlayer.create(gameView.getContext(), R.raw.score);
		mStartSound = MediaPlayer.create(gameView.getContext(), R.raw.start);
		mBounceSound = MediaPlayer.create(gameView.getContext(), R.raw.bounce);
	}

	// This is run before a new game (also after an old game)
	@Override
	public void setupBeginning() {
		// Initialise speeds
		mBallSpeedX = mCanvasWidth / 3; 
		mBallSpeedY = mCanvasHeight / 3;

		// Place the ball in the middle of the screen.
		// mBall.Width() and mBall.getHeight() gives us the height and width of
		// the image of the ball
		mBallX = mCanvasWidth / 2;
		mBallY = mCanvasHeight / 2;

		// Place the paddle at the bottom of the screen, in the middle.
		mPaddleX = mCanvasWidth / 2;

		// Place the smiley ball near the top of the screen
		mSmileyBallX = mCanvasWidth / 3; // leftmost third of screen
		mSmileyBallY = mCanvasHeight / 4; // top quarter of screen

		// Place the sad ball near the top of the screen
		mSadBallX = mCanvasWidth / 1.5f; // rightmost third of screen
		mSadBallY = mCanvasHeight / 4; // top quarter of screen

		mMinDistanceBetweenRedBallAndBigBall = (mPaddle.getWidth() / 2 + mBall
				.getWidth() / 2) // BigBall is paddle
				* (mPaddle.getWidth() / 2 + mBall.getWidth() / 2);

		mMinDistanceBetweenRedBallAndSmileyBall = (mSmileyBall.getWidth() / 2 + mBall
				.getWidth() / 2)
				* (mSmileyBall.getWidth() / 2 + mBall.getWidth() / 2);

		mMinDistanceBetweenRedBallAndSadBall = (mSadBall.getWidth() / 2 + mBall
				.getWidth() / 2)
				* (mSadBall.getWidth() / 2 + mBall.getWidth() / 2);

		// Make sure a new game always begins with the daffodil background
		super.mBackgroundImage = BitmapFactory.decodeResource(super.mGameView
				.getContext().getResources(), R.drawable.background);

		super.setSurfaceSize(mCanvasWidth, mCanvasHeight);

		// Play a sound when the game starts
		mStartSound.start();
		
		SharedPreferences sharedPref = mContext.getSharedPreferences(SHARED_PREF,
		         Context.MODE_PRIVATE);
		mHighScore = sharedPref.getInt(HIGH_SCORE, 0);
		
		// Make sure we always start with 3 lives
		noOfLives = 3;
		
		// Bugfix in v1.2 - make sure we always start on Level 1
		levelNo = 1;

	}

	@Override
	protected void doDraw(Canvas canvas) {
		// If there isn't a canvas to draw on do nothing
		// It is ok not understanding what is happening here
		if (canvas == null)
			return;

		super.doDraw(canvas);

		// draw the image of the ball using the X and Y of the ball
		// drawBitmap uses top left corner as reference, we use middle of
		// picture
		// null means that we will use the image without any extra features
		// (called Paint)
		canvas.drawBitmap(mBall, mBallX - mBall.getWidth() / 2,
				mBallY - mBall.getHeight() / 2, null);

		// draw the image of the paddle using the X and Y of the paddle
		canvas.drawBitmap(mPaddle, mPaddleX - mPaddle.getWidth() / 2,
				mCanvasHeight - mPaddle.getHeight() / 2, null);

		// draw the image of the smiley ball using the X and Y of the smiley
		// ball
		canvas.drawBitmap(mSmileyBall, mSmileyBallX - mSmileyBall.getWidth()
				/ 2, mSmileyBallY - mSmileyBall.getHeight() / 2, null);

		// draw the image of the sad ball using the X and Y of the sad ball
		canvas.drawBitmap(mSadBall, mSadBallX - mSadBall.getWidth() / 2,
				mSadBallY - mSadBall.getHeight() / 2, null);
		
		// draw the level number in red at top left of screen
		String levelNoText = String.valueOf(levelNo);
		levelNoText = "Level " + levelNoText;
		Paint levelNoTextPaint = new Paint();
		levelNoTextPaint.setColor(Color.RED);
		levelNoTextPaint.setTextSize(36);
		canvas.drawText(levelNoText, 5, 40, levelNoTextPaint); 
		
		// draw the number of lives at top of the screen
		String noOfLivesText = String.valueOf(noOfLives);
		noOfLivesText = "Lives: " + noOfLivesText;
		Paint noOfLivesTextPaint = new Paint();
		noOfLivesTextPaint.setColor(Color.CYAN);
		noOfLivesTextPaint.setTextSize(36);
		canvas.drawText(noOfLivesText, 5, 80, noOfLivesTextPaint);

	}

	// This is run whenever the phone is touched by the user

	@Override
	protected void actionOnTouch(float x, float y) {

		// mPaddleX = x - mPaddle.getWidth() / 2;
		// Set the paddle's x position to the touched x position
		// unless this would set part of the paddle off screen
		// in which case set the paddle's x position so that the paddle is
		// touching the relevant screen edge
		if (x <= mPaddle.getWidth() / 2) {
			mPaddleX = mPaddle.getWidth() / 2;
		} else if (x >= mCanvasWidth - mPaddle.getWidth() / 2) {
			mPaddleX = mCanvasWidth - mPaddle.getWidth() / 2;
		} else {
			mPaddleX = x;
		}
	}

	// This is run whenever the phone moves around its axises
	@Override
	protected void actionWhenPhoneMoved(float xDirection, float yDirection,
			float zDirection) {

		// Check if the paddle is already touching one of the screen's edges
		// If it is not then move the paddle's x position in the direction of
		// tilt
		if ((mPaddleX >= 0) && (mPaddleX <= mCanvasWidth)) {
			mPaddleX = mPaddleX - xDirection;
			if (mPaddleX < 0)
				mPaddleX = 0;
			if (mPaddleX > mCanvasWidth)
				mPaddleX = mCanvasWidth;
		}
	}

	// This is run just before the game "scenario" is printed on the screen
	@Override
	protected void updateGame(float secondsElapsed) {

		// Test for collisions between the moving ball and the paddle
		if (mBallSpeedY > 0) { // if the ball is moving down the screen
			if (updateBallCollision(mPaddleX, mCanvasHeight)) {
				// Play sound when paddle hit
				mHitPaddle.start();
			}
		}

		// Test for collisions between the moving ball and the smiley ball
		if (updateBallCollision(mSmileyBallX, mSmileyBallY)) {
			// Play sound when ball hits target
			mScoreSound.start();
			// Increase the score by 1
			updateScore(1);
			// Call the method to reposition the paddle in the middle of the
			// screen after scoring
			repositionPaddleAfterScoring();
			// Call the method to move the smiley ball after scoring
			if (mSmileyBallX >= 0 + mSmileyBall.getWidth() / 2 ) {
				moveSmileyBallToLeft();
			}
			if (mSmileyBallY >= 0 + mSmileyBall.getHeight() / 2) {
				moveSmileyBallToTop();
			}
			// When score reaches 5, change the background to a space-themed
			// background
			if (getScore() == 5) {
				super.mBackgroundImage = BitmapFactory.decodeResource(
						super.mGameView.getContext().getResources(),
						R.drawable.backgroundscoreplus10);

				super.setSurfaceSize(mCanvasWidth, mCanvasHeight);
				
				//Increase the speed of the ball
				increaseBallSpeed();
				
				levelNo = 2;
			}
			// When score reaches 10, change the background to a sparkler-themed
			// background
			if (getScore() == 10) {
				super.mBackgroundImage = BitmapFactory.decodeResource(
						super.mGameView.getContext().getResources(),
						R.drawable.backgroundscoreplus20);

				super.setSurfaceSize(mCanvasWidth, mCanvasHeight);
				
				//Increase the speed of the ball
				increaseBallSpeed();
				
				levelNo = 3;
			}
			// When score reaches 15, change the background to a snow-themed
			// background
			if (getScore() == 15) {
				super.mBackgroundImage = BitmapFactory.decodeResource(
						super.mGameView.getContext().getResources(),
						R.drawable.backgroundscoreplus30);

				super.setSurfaceSize(mCanvasWidth, mCanvasHeight);
				
				//Increase the speed of the ball
				increaseBallSpeed();
				
				levelNo = 4;
			}
			// When score reaches 20, change the background to a sunset-themed
			// background
			if (getScore() == 20) {
				super.mBackgroundImage = BitmapFactory.decodeResource(
						super.mGameView.getContext().getResources(),
						R.drawable.backgroundscoreplus40);

				super.setSurfaceSize(mCanvasWidth, mCanvasHeight);
				
				//Increase the speed of the ball
				increaseBallSpeed();
				
				levelNo = 5;
			}
		}

		// Test for collisions between the moving ball and the sad ball
		if (updateBallCollision(mSadBallX, mSadBallY)) {
			updateScore(-1);
			// Call the method to reposition the paddle in the middle of the
			// screen after scoring
			repositionPaddleAfterScoring();
			// Call method to move the sad ball after scoring
			if (mSadBallX <= mCanvasWidth - mSadBall.getWidth() / 2) {
				moveSadBallToRight();
			}
			if (mSadBallY <= mCanvasHeight - mSadBall.getHeight() / 2) {
				moveSadBallToBottom();
			}
			// When score drops to 19, change the background back to the
			// snow-theme
			if (getScore() == 19) {
				super.mBackgroundImage = BitmapFactory.decodeResource(
						super.mGameView.getContext().getResources(),
						R.drawable.backgroundscoreplus30);

				super.setSurfaceSize(mCanvasWidth, mCanvasHeight);
				
				//Decrease the speed of the ball
				decreaseBallSpeed();
				
				levelNo = 4;
			}
			// When score drops to 14, change the background back to the
			// sparkler-theme
			if (getScore() == 14) {
				super.mBackgroundImage = BitmapFactory.decodeResource(
						super.mGameView.getContext().getResources(),
						R.drawable.backgroundscoreplus20);

				super.setSurfaceSize(mCanvasWidth, mCanvasHeight);
				
				//Decrease the speed of the ball
				decreaseBallSpeed();
				
				levelNo = 3;
			}
			// When score drops to 9, change the background back to the
			// space-theme
			if (getScore() == 9) {
				super.mBackgroundImage = BitmapFactory.decodeResource(
						super.mGameView.getContext().getResources(),
						R.drawable.backgroundscoreplus10);

				super.setSurfaceSize(mCanvasWidth, mCanvasHeight);
				
				//Decrease the speed of the ball
				decreaseBallSpeed();
				
				levelNo = 2;
			}
			// When score drops to 4, change the background back to the daffodil
			if (getScore() == 4) {
				super.mBackgroundImage = BitmapFactory.decodeResource(
						super.mGameView.getContext().getResources(),
						R.drawable.background);

				super.setSurfaceSize(mCanvasWidth, mCanvasHeight);
				
				//Decrease the speed of the ball
				decreaseBallSpeed();
				
				levelNo = 1;
			}
		}

		// Move the ball's X and Y using the speed (pixel/sec)
		mBallX = mBallX + secondsElapsed * mBallSpeedX;
		mBallY = mBallY + secondsElapsed * mBallSpeedY;

		// Check if the ball hits either the left side or the right side of the
		// screen.
		// But only do something if the ball is moving towards that side of the
		// screen.
		// If it does that, then change the direction of the ball in the X
		// direction.
		if ((mBallX <= mBall.getWidth() / 2 && mBallSpeedX < 0)
				|| (mBallX >= mCanvasWidth - mBall.getWidth() / 2 && mBallSpeedX > 0)) {
			mBallSpeedX = -mBallSpeedX;

			// Play sound when ball hits left or right side of screen
			mBounceSound.start();
		}

		// Check if the ball hits either the top or the bottom of the screen.
		// But only do something if the ball is moving towards that side of the
		// screen.
		// If it does that, then change the direction of the ball in the Y
		// direction.
		if ((mBallY <= mBall.getHeight() / 2 && mBallSpeedY < 0)) {
			mBallSpeedY = -mBallSpeedY;

			// Play sound when ball hits top of screen
			mBounceSound.start();
		}

		if (mBallY >= mCanvasHeight - mBall.getHeight() / 2 && mBallSpeedY > 0) {
			//---------------------------------------HIT BOTTOM OF SCREEN----------------
			//either bounce and lose a life, or lose the game
			noOfLives = noOfLives - 1; //Lose a life
			//NEED TO BOUNCE THE BALL UPWARDS AGAIN
			mBallSpeedY = -mBallSpeedY;
			
			// If the ball hits the bottom of the screen and number of lives is zero, it's "Game over"
			if (noOfLives <= 0)
			{
				// Play sound when game is lost
				mLoseSound.start();
				setState(GameThread.STATE_LOSE);
			}
		}
	}

	// collision control between mBall and another big ball
	private boolean updateBallCollision(float x, float y) {
		/*
		 * Get actual distance (without square root - remember?) between the
		 * mBall and the ball being checked
		 */
		float distanceBetweenBallAndPaddle = (x - mBallX) * (x - mBallX)
				+ (y - mBallY) * (y - mBallY);
		// Check if the actual distance is lower than the allowed => collision
		if (mMinDistanceBetweenRedBallAndBigBall >= distanceBetweenBallAndPaddle) {
			/*
			 * Get the present velocity (this should also be the velocity going
			 * away after the collision)
			 */
			float velocityOfBall = (float) Math.sqrt(mBallSpeedX * mBallSpeedX
					+ mBallSpeedY * mBallSpeedY);
			// Change the direction of the ball
			mBallSpeedX = mBallX - x;
			mBallSpeedY = mBallY - y;
			// Get the velocity after the collision
			float newVelocity = (float) Math.sqrt(mBallSpeedX * mBallSpeedX
					+ mBallSpeedY * mBallSpeedY);
			/*
			 * using the fraction between the original velocity and present
			 * velocity to calculate the needed
			 */
			/*
			 * speeds in X and Y to get the original velocity but with the new
			 * angle
			 */
			mBallSpeedX = mBallSpeedX * velocityOfBall / newVelocity;
			mBallSpeedY = mBallSpeedY * velocityOfBall / newVelocity;
			// A collision happened so we return true here
			return true;
		}
		// No collision happened so we return false here
		return false;
	}

	// Reposition the paddle in the middle of the screen every time the user
	// scores
	private void repositionPaddleAfterScoring() {
		mPaddleX = mCanvasWidth / 2;
	}

	private void moveSadBallToRight() {
		mSadBallX = mSadBallX + 5;// move mSadBallX towards the right
	}

	private void moveSadBallToBottom() {
		mSadBallY = mSadBallY + 5;// move mSadBallY towards the bottom
	}

	private void moveSmileyBallToLeft() {
		mSmileyBallX = mSmileyBallX - 5;// move mSmileyBallX towards the left
	}

	private void moveSmileyBallToTop() {
		mSmileyBallY = mSmileyBallY - 5;// move mSmileyBallX towards the top
	}
	
	private void increaseBallSpeed() {
		mBallSpeedX = mBallSpeedX * (float)1.10;
        mBallSpeedY = mBallSpeedY * (float)1.10;
	}
	
	private void decreaseBallSpeed() {
		mBallSpeedX = mBallSpeedX * (float)0.90;
        mBallSpeedY = mBallSpeedY * (float)0.90;
	}
	
}

// This file is part of the course
// "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// Modified by Victoria Holland - March 2014
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// Sound effects are from the SoundBible.com website
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
//
// You should have received a copy of the GNU General Public License
// along with it. If not, see <http://www.gnu.org/licenses/>.

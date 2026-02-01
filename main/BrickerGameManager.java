package bricker.main;


import bricker.brick_strategies.BasicCollisionStrategy;
import bricker.brick_strategies.StrategyFactory;
import bricker.brick_strategies.CollisionStrategy;
import bricker.gameobjects.Ball;
import bricker.gameobjects.Brick;
import bricker.gameobjects.LivesUI;
import bricker.gameobjects.Paddle;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import danogl.util.Counter;
import java.awt.event.KeyEvent;


import java.util.Random;

/**
 * Main game manager for the Bricker game. Responsible for initializing the game scene,
 * creating the ball, paddle, walls, and bricks, and handling game state such as lives
 * and win/lose dialogs.
 */
public class BrickerGameManager extends GameManager{
    private static final int BORDER_WIDTH = 30;
    private static final int PADDLE_HEIGHT = 20;
    private static final int PADDLE_WIDTH = 150;
    private static final int BALL_SIZE = 35;
    private static final float BALL_SPEED = 250;
    private static final float PUCK_SIZE_MULTIPLIER = 0.75f;
    private static final float PUCK_SIZE = BALL_SIZE * PUCK_SIZE_MULTIPLIER;
    private static final float PUCK_SPEED = BALL_SPEED;
    private final int BRICKS_PER_ROW;
    private final int NUMBER_OF_ROWS;
    private static final int MAX_FALLS = 4;
    private static final int INITIAL_FALLS = 3;
    private int remainingFalls;
    private Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private LivesUI livesUI;
    private Counter bricksLeft;
    private UserInputListener input;
    private GameObject originalPaddle;


    /**
     * Create a default BrickerGameManager with a standard grid size (8 columns x 7 rows).
     *
     * @param windowTitle Window title shown in the application frame
     * @param windowDimensions Initial window dimensions - width, height
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        this(windowTitle, windowDimensions, 8, 7);
    }

    /**
     * Create a BrickerGameManager with a custom brick grid size.
     *
     * @param windowTitle Window title shown in the application frame
     * @param windowDimensions Initial window dimensions - width, height
     * @param BRICKS_PER_ROW Number of bricks per row
     * @param NUMBER_OF_ROWS Number of rows of bricks
     */
    public BrickerGameManager(String windowTitle,
                              Vector2 windowDimensions,
                              int BRICKS_PER_ROW,
                              int NUMBER_OF_ROWS) {
        super(windowTitle, windowDimensions);
        this.BRICKS_PER_ROW = BRICKS_PER_ROW;
        this.NUMBER_OF_ROWS = NUMBER_OF_ROWS;
    }

    /**
     * Initialize the entire game scene: UI, ball, paddles, walls, background, and bricks.
     *
     * @param imageReader image resource loader
     * @param soundReader sound resource loader
     * @param inputListener user input listener for keyboard/mouse
     * @param windowController window controller used for dialogs and window operations
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowDimensions = windowController.getWindowDimensions();
        this.windowController = windowController;
        this.input = inputListener;

        //initialize falls counter
        remainingFalls = INITIAL_FALLS;

        livesUI = new LivesUI(gameObjects(), imageReader, windowDimensions, MAX_FALLS, remainingFalls);

        bricksLeft = new Counter(BRICKS_PER_ROW * NUMBER_OF_ROWS);

        createBall(imageReader, soundReader);

        createUserPaddle(imageReader, inputListener);

        makeWalls();

        createBackground(imageReader);

        spawnBrick(imageReader, soundReader);
    }

    /**
     * Restore a life (called by falling heart).
     */
    public void restoreLife() {
        if (remainingFalls < MAX_FALLS) {
            remainingFalls++;
            livesUI.updateLives(remainingFalls);
        }
    }

    /**
     * Entry point for the game application. The game is run via {@link #run()} provided by GameManager.
     * Provide optional command-line arguments for columns and rows counts.
     *
     * @param args optional arguments: cols rows
     */
    public static void main (String[] args) {
        Vector2 window = new Vector2(1000, 700);

        // If two args are provided, use them as (bricksPerRow, numRows); else defaults (8×7)
        BrickerGameManager game;
        if (args != null && args.length == 2) {
            int cols = Integer.parseInt(args[0]);
            int rows = Integer.parseInt(args[1]);
            game = new BrickerGameManager("Bricker", window, cols, rows);
        } else {
            game = new BrickerGameManager("Bricker", window); // defaults
        }
        game.run();
    }

    /*====== Helpers =======*/
    /**
     * Add the background image to the scene.
     *
     * @param imageReader image resource loader used to read the background image
     */
    private void createBackground(ImageReader imageReader) {
        Renderable backgroundImage = imageReader.readImage("assets/DARK_BG2_small.jpeg", true);
        GameObject background = new GameObject(
                Vector2.ZERO,
                new Vector2(windowDimensions.x(), windowDimensions.y()),
                backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    /**
     * Create the original user-controlled paddle and add it to game objects.
     *
     * @param imageReader image reader used to load the paddle renderable
     * @param inputListener user input listener for paddle control
     */
    private void createUserPaddle(ImageReader imageReader,
                                  UserInputListener inputListener) {
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        originalPaddle = new Paddle(
                Vector2.ZERO,
                new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage,
                inputListener,
                windowDimensions);
        originalPaddle.setCenter(
                new Vector2(windowDimensions.x()/2, windowDimensions.y() - PADDLE_HEIGHT));
        gameObjects().addGameObject(originalPaddle);
    }

    /**
     * Create the main ball, add it to the scene and initialize its velocity.
     *
     * @param imageReader image reader for the ball renderable
     * @param soundReader sound reader for the collision sound
     */
    private void createBall(ImageReader imageReader,
                            SoundReader soundReader) {
        Renderable ballImage = imageReader.readImage("assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop.wav");

        this.ball = new Ball(Vector2.ZERO,
                new Vector2(BALL_SIZE, BALL_SIZE),
                ballImage,
                collisionSound);

        gameObjects().addGameObject(ball);
        resetBall();
    }

    /**
     * Reset ball position to the center of the window and assign a new random velocity.
     */
    private void resetBall() {
        ball.setCenter(windowDimensions.mult(0.5f));
        float ballVelY = BALL_SPEED;
        float ballVelX = BALL_SPEED;
        Random rand = new Random();
        if (rand.nextBoolean()) {
            ballVelY *= -1;
            ballVelX *= -1;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));
    }

    /**
     * Update the game manager every frame. This method advances game state and checks for
     * end-of-game conditions - life lost or win.
     *
     * @param deltaTime time elapsed since last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        checkForGameEnd();
    }

    /**
     * Check whether the player lost a life or won and act accordingly.
     * This method is called every frame from {@link #update(float)}.
     */
    private void checkForGameEnd() {
        float ballHeight = this.ball.getCenter().y();
        if (ballHeight > windowDimensions.y()) {
            onLifeLost();
        } else if (bricksLeft.value() <= 0 || input.isKeyPressed(KeyEvent.VK_W)) {
            onWin();
        }
    }

    /**
     * Handle life-lost: decrement remaining lives, update UI and either reset the ball
     * or present the lose dialog if no lives remain.
     */
    private void onLifeLost() {
        remainingFalls--;
        livesUI.updateLives(remainingFalls);

        if (remainingFalls > 0) {
            resetBall();
        } else {
            String prompt = "You lose! Play again?";
            if (windowController.openYesNoDialog(prompt)) {
                windowController.resetGame();
            } else {
                windowController.closeWindow();
            }
        }
    }

    /**
     * Handle win condition: show a dialog and reset or close the game depending on player's choice.
     */
    private void onWin() {
        String prompt = "You win! Play again?";
        if (windowController.openYesNoDialog(prompt)) {
            windowController.resetGame();
        } else {
            windowController.closeWindow();
        }
    }

    /**
     * Spawn the bricks grid and decorate some bricks with special strategies.
     * This method constructs the Brick[][] grid.
     *
     * @param imageReader image reader for brick renderable
     * @param soundReader sound reader for brick-related sounds
     */
    private void spawnBrick(ImageReader imageReader, SoundReader soundReader) {
        final float BRICK_HEIGHT = 15f;
        final float H_GAP = 5f; // horizontal gap between bricks
        final float V_GAP = 5f; // vertical gap between rows
        final float LEFT_X  = BORDER_WIDTH + H_GAP;
        final float RIGHT_X = windowDimensions.x() - BORDER_WIDTH - H_GAP;
        final float USABLE_W = RIGHT_X - LEFT_X;

        final float brickWidth = (USABLE_W - (BRICKS_PER_ROW - 1) * H_GAP) / BRICKS_PER_ROW;
        final float TOP_Y = BORDER_WIDTH + H_GAP; // place grid below the top wall

        Renderable brickImage = imageReader.readImage("assets/brick.png", true);
        BasicCollisionStrategy basic = new BasicCollisionStrategy(gameObjects(), bricksLeft);

        Brick[][] brickGrid = new Brick[NUMBER_OF_ROWS][BRICKS_PER_ROW];

        // First pass: create bricks and place them in the grid
        for (int row = 0; row < NUMBER_OF_ROWS; row++) {
            float y = TOP_Y + row * (BRICK_HEIGHT + V_GAP);
            for (int col = 0; col < BRICKS_PER_ROW; col++) {
                float x = LEFT_X + col * (brickWidth + H_GAP);
                Brick brick = new Brick(
                        new Vector2(x, y),
                        new Vector2(brickWidth, BRICK_HEIGHT),
                        brickImage,
                        row, col,
                        basic
                );
                brickGrid[row][col] = brick;
                gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
            }
        }

        // Second pass: decorate bricks with special strategies
        Random rand = new Random();
        float specialChance = 0.5f; // 50% of bricks will be special

        for (int row = 0; row < NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < BRICKS_PER_ROW; col++) {
                Brick brick = brickGrid[row][col];
                if (brick == null) continue;

                // decide if this brick becomes special
                if (rand.nextFloat() < specialChance) {
                    CollisionStrategy decorated = StrategyFactory.createRandomSpecialStrategy(
                            basic,
                            gameObjects(),
                            imageReader,
                            soundReader,
                            windowDimensions,
                            PUCK_SIZE,
                            PUCK_SPEED,
                            new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                            imageReader.readImage("assets/paddle.png", true),
                            input,
                            bricksLeft,
                            brickGrid,
                            originalPaddle,
                            new bricker.gameobjects.FallingHeart.LifeRestoreCallback() {
                                @Override
                                public void restoreLife() {
                                    BrickerGameManager.this.restoreLife();
                                }
                            },
                            rand
                    );

                     // Replace the brick's strategy by constructing a new Brick with same position
                     Brick newBrick = new Brick(brick.getTopLeftCorner(),
                             brick.getDimensions(),
                             brickImage,
                             row,
                             col,
                             decorated);
                     // remove old and add new
                     gameObjects().removeGameObject(brick, Layer.STATIC_OBJECTS);
                     brickGrid[row][col] = newBrick;
                     gameObjects().addGameObject(newBrick, Layer.STATIC_OBJECTS);
                 }
             }
         }
     }

    /**
     * Create the left, right and upper static walls used for collision.
     */
    private void makeWalls() {
        GameObject leftWall = new GameObject(
                Vector2.ZERO,
                new Vector2(BORDER_WIDTH, windowDimensions.y()),
                null);

        GameObject rightWall = new GameObject(
                new Vector2(windowDimensions.x()-BORDER_WIDTH, 0),
                new Vector2(BORDER_WIDTH, windowDimensions.y()),
                null);

        GameObject upperWall = new GameObject(
                Vector2.ZERO,
                new Vector2(windowDimensions.x(), BORDER_WIDTH),
                null);

        gameObjects().addGameObject(leftWall, Layer.STATIC_OBJECTS);
        gameObjects().addGameObject(rightWall, Layer.STATIC_OBJECTS);
        gameObjects().addGameObject(upperWall, Layer.STATIC_OBJECTS);
    }
}

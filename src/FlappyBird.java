import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Objects;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image bgImg, birdImg, topPipeImg, bottomPipeImg;

    // Bird properties
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipes properties
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        Image img;
        boolean birdPassed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic
    Bird bird;
    int velocityX = -4; // move pipes to the left
    double velocityY = 0; // move bird vertically
    double gravity = 0.5;

    ArrayList<Pipe> pipes;
    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    int score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images
        bgImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./resources/bg.png"))).getImage();
        birdImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./resources/bird.png"))).getImage();
        topPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./resources/toppipe.png"))).getImage();
        bottomPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./resources/bottompipe.png"))).getImage();

        // Initialize bird and pipes
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        // Pipe timer
        placePipesTimer = new Timer(1500, _ -> placePipes());
        placePipesTimer.start();

        // Game timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    void placePipes() {
        // (0-1) * pipeHeight/2.
        // 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight
        int randomPipeY = (int) (pipeY - (double) pipeHeight / 4 - Math.random() * ((double) pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw background
        g.drawImage(bgImg, 0, 0, this.boardWidth, this.boardHeight, null);

        // Draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Draw pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipeWidth, pipeHeight, null);
        }

        // Draw score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (gameOver) {
            g.drawString("Game Over! Score: " + score, 10, boardHeight / 2);
        } else {
            g.drawString("Score: " + score, 10, 35);
        }
    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += (int) velocityY;
        bird.y = Math.max(0, bird.y);

        // pipes
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.birdPassed && pipe.img == bottomPipeImg && bird.x > pipe.x + pipeWidth) {
                score++;
                pipe.birdPassed = true;
            }

            if (checkCollision(bird, pipe)) {
                gameOver = true;
            }
        }
        pipes.removeIf(p -> p.x + pipeWidth < 0);

        // Check if bird hit the ground
        if (bird.y + bird.height >= boardHeight) {
            gameOver = true;
        }
    }

    boolean checkCollision(Bird bird, Pipe pipe) {
        return bird.x < pipe.x + pipeWidth && bird.x + bird.width > pipe.x && bird.y < pipe.y + pipeHeight && bird.y + bird.height > pipe.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;

            if (gameOver) {
                // Reset game
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

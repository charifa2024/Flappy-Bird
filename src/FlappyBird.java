import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;



public class FlappyBird extends JPanel implements ActionListener , KeyListener{ 
    int boardwidth = 360;
    int boardheight = 640;

    //images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImage;
    Image bottomPipeImage;

    //bird 
    int birdX = boardwidth/8;
    int birdY= boardheight/2;

    int birdwidth = 34;
    int birdheight = 24;

    class  Bird{
        int x=birdX;
        int y=birdY;
        
        int width = birdwidth;
        int height = birdheight;
        Image img ;

        Bird(Image img){
            this.img = img;
        }
    }

    //pipes
    int pipeX = boardwidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;


    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean isPassed = false;
        Pipe(Image img){
            this.img = img;
        }



    }
    //game logic
    Bird bird;
    int velocityX=-4;
    int velocityY = 0;
    int gravity = 1;
    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;

    ArrayList<Pipe> pipes;

    FlappyBird(){
        setPreferredSize(new Dimension(boardwidth, boardheight));
        //setBackground(Color.blue);

        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();


        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        placePipesTimer.start();
        //game timer

        gameLoop = new Timer(1000/40,this);
        gameLoop.start();




    }

    public void placePipes(){
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random() * (pipeHeight/2) );

        int openingSpace = boardheight /4;
        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + topPipe.height + openingSpace;
        pipes.add(bottomPipe);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {

       // System.out.println("drawing");
        g.drawImage(backgroundImg,0,0,boardwidth,boardheight,null);
        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        //pipes
        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if(gameOver){
            g.drawString("Game Over : " + String.valueOf((int)score), 10, 35);
        }else{
            g.drawString("Score : " + String.valueOf((int)score), 10, 35);
        }
    }

    public void move(){
        //bird
        velocityY +=gravity;
        bird.y += velocityY;
        bird.y=Math.max(bird.y, 0);
        //pipes
        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.isPassed && bird.x > pipe.x + pipe.width){
                pipe.isPassed = true;
                score=score+0.5;
            }
            if(collision(bird, pipe)){
                gameOver = true;
            }
            
        }

        if(bird.y > boardheight){
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return
        a.x <  b.x + b.width &&
        a.x + a.width > b.x &&
        a.y < b.y + b.height &&
        a.y + a.height > b.y;

    }
        
    
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;

            if(gameOver){
                //restart game
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                placePipesTimer.start();
                gameLoop.start();
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
}

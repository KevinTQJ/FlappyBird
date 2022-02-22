/*
    Jan.8, 2020
    Kevin Wu
    Summative Task - Flappy Bird
    This game illustrates almost the original Flappy Bird with selectable difficulties
    Use space bar to raise the bird
    See other instructions in User Manual
 */
package pt;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

/**
 *
 * @author Kevin Wu
 */
public class Final extends JPanel implements KeyListener {

    Image bird, background, upperPipe, lowerPipe, title;
    static int width = 800, height = 800;//frame's width and height
    double bx = 385, by = 385, byUps = 40, byDos = 1, bacc = 0.75;//birds related variables
    int score, bw = 50, bl = 50, pipe = 3, pix[] = new int[pipe], gap = 180, p1 = 800, pw = 100, pyUp[] = new int[pipe], distance = 240;//game score, bird's width and height, pipe related variables
    boolean up, gameover, start, level, begin = true;//boolean variables

    Final() {
        this.addKeyListener(this);//add keylistener
        setFocusable(true);//focus on keyboard
        try {
            bird = ImageIO.read(new File("bird.png")).getScaledInstance(bw, bl, Image.SCALE_DEFAULT);
            background = ImageIO.read(new File("background.png")).getScaledInstance(width, height, Image.SCALE_DEFAULT);
            upperPipe = ImageIO.read(new File("upper_pipe.png")).getScaledInstance(pw, height, Image.SCALE_DEFAULT);
            lowerPipe = ImageIO.read(new File("lower_pipe.png")).getScaledInstance(pw, height, Image.SCALE_DEFAULT);
            title = ImageIO.read(new File("title.png")).getScaledInstance(600, 120, Image.SCALE_DEFAULT);
        } //load images
        catch (IOException e) {
            System.out.println("File not found");
            System.exit(-1);
        }
        for (int j = 0; j < pipe; j++) {
            pix[j] = width + j * (pw + distance);
            pyUp[j] = (int) (Math.random() * (height - gap));
        }//initialize the pipes' location and their gaps' location
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);//clear frame
        g.setColor(Color.white);
        g.setFont(new Font("Nunito", Font.PLAIN, 40));
        g.drawImage(background, 0, 0, null);//fill the background with the "background" image
        if (begin) {
            g.drawImage(title, 100, 170, null);
            g.drawString("Press ENTER to start!", 200, 420);
        }//display initial interface

        if (start) {
            g.drawImage(bird, (int) bx, (int) by, null);
            for (int i = 0; i < pipe; i++) {
                g.drawImage(upperPipe, pix[i], pyUp[i] - height, null);
                g.drawImage(lowerPipe, pix[i], pyUp[i] + gap, null);
            }
            g.setFont(new Font("Arial", Font.BOLD, 70));
            g.drawString("" + score, 400, 70);//display the score while the game is running
            if (!gameover && !level) {
                delay();
            }
        }//draw the bird and pipes if the game is start
        if (level) {
            g.setFont(new Font("Nunito", Font.PLAIN, 40));
            g.drawString("1 -------- Easy", 270, 290);
            g.drawString("2 ----- Moderate", 270, 360);
            g.drawString("3 -------- Hard", 270, 430);
            g.setFont(new Font("Nunito", Font.PLAIN, 55));
            g.drawString("Select Difficulty", 200, 220);
        }//inform user to select difficulty
        if (gameover) {
            g.setFont(new Font("Nunito", Font.PLAIN, 60));
            g.drawString("Game Over!", 230, 250);
            g.drawString("Your Score: " + score, 210, 330);
            g.drawString("Press ENTER to Restart", 70, 410);
            g.drawString("Press ESC to Exit", 160, 490);
        }//inform the user the game is over with the score, and press Enter key to replay, press ESC to exit
        repaint();
    }

    public void delay() {
        try {
            Thread.sleep(20);//set a delay of 20 ms
        } catch (InterruptedException e) {
            System.out.println("error in delay");
        }
        bird();//call "bird" method
        pipe();//call "pipe" method
        score();//call "score" method
        collision();//call "collison" method
    }

    public void bird() {
        if (byDos > 3) {
            byUps = 10;
        }//reset bird's upwards speed after the bird drops a bit, so that the bird will not continuly accelerate upwards
        if (up) {
            double acc = 3;
            acc += 2.5;
            byUps += acc;
            by -= byUps;
        }//raise the bird with acceleration when up == true
        byDos += bacc;
        by += byDos;//let the bird be affected by gravity(drop with accleration)
    }

    public void pipe() {
        for (int i = 0; i < pipe; i++) {
            if (pix[i] + pw <= 0) {
                pix[i] = width + distance / 2;
                pyUp[i] = (int) (Math.random() * (height - gap));
            }//re-determine the gap within the pipe and relocate the pipe to its initial location if a pipe disappears from the left side
            pix[i] -= 5;//let each pipe move 5 units per repaint horizontally
        }
    }

    public void collision() {
        for (int i = 0; i < pipe; i++) {
            if (bx + bw > pix[i] && by < pyUp[i] && bx < pix[i] + pw || bx + bw > pix[i] && by + bl > pyUp[i] + gap && bx < pix[i] + pw) {
                gameover = true;
            }
        }
    }//collision detection, gameover when the bird hits any part of the pipe

    public void score() {
        for (int i = 0; i < pipe; i++) {
            if (bx + bw / 2 == pix[i] + pw / 2 && by > pyUp[i] && by + bl < pyUp[i] + gap) {
                score++;
            }
        }
    }//increase the score by 1 when the bird passes the middle of a pipe

    public void restart() {
        level = false;
        score = 0;
        bx = 385;
        by = 385;
        byDos = 1;
        for (int j = 0; j < pipe; j++) {
            pix[j] = width + j * (pw + distance);
            pyUp[j] = (int) (Math.random() * (height - gap));
        }
        gameover = false;
        start = true;
    }//reset variables needed for restarting the game

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            byDos = 1;//reset the downwards speed
            up = true;
        }//raise the bird while pressing space bar
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);//exit the game
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (begin) {
                begin = false;
                level = true;
            }
            if (gameover) {
                gameover = false;
                level = true;
            }
        }//used for start/restarting the game
        if (level) {
            if (e.getKeyCode() == KeyEvent.VK_1) {
                gap = 250;
                restart();
            }//difficulty 1(easy)
            if (e.getKeyCode() == KeyEvent.VK_2) {
                gap = 180;
                restart();
            }//difficulty 2(moderate)
            if (e.getKeyCode() == KeyEvent.VK_3) {
                gap = 130;
                restart();
            }//difficulty 3(hard)
        }//user can only select difficulty at the begining or after gameover
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            up = false;
        }
    }//stop raising the bird when space bar is released

    public void keyTyped(KeyEvent e) {
    }//needed for KeyListener interface

    public static void main(String args[]) {
        JFrame frame = new JFrame("Flappy Bird");
        frame.getContentPane().add(new Final());
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }//creat the frame
}

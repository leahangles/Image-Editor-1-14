import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class ImageEditorPanel extends JPanel  implements KeyListener{

    Color[][] pixels;
    
    public ImageEditorPanel() {
        BufferedImage imageIn = null;
        try {
            // the image should be in the main project folder, not in \src or \bin
            imageIn = ImageIO.read(new File("nature.jpg"));
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        setPreferredSize(new Dimension(pixels[0].length, pixels.length));
        setBackground(Color.BLACK);
        addKeyListener(this);
    }
    
    public void paintComponent(Graphics g) {
        // paints the array pixels onto the screen
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                g.setColor(pixels[row][col]);
                g.fillRect(col, row, 1, 1);
            }
        }
    }
    
    public Color[][] makeColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row), true);
                result[row][col] = c;
            }
        }
        return result;
    }
    
    public Color[][] flipHorizontal(Color[][] og){
        Color[][] horiz = new Color[og.length][og[0].length];
        for (int r = 0; r < horiz.length; r++) {
            for (int c = 0; c < horiz[0].length; c++) {
                horiz[r][pixels[0].length - c - 1] = og[r][c];
            }
        }
        return horiz;
    }
    
    public Color[][] flipVertical(Color[][] og){
        Color[][] vert = new Color[og.length][og[0].length];
        for (int c = 0; c < vert[0].length; c++) {
            for (int r = 0; r < vert.length; r++) {
                vert[pixels.length - r - 1][c] = og[r][c];
            }
        }
        return vert;
    }
    
    public Color[][] rotate(Color[][] og){
        Color[][] rot = new Color[og[0].length][og.length];
        for (int r = 0; r < rot.length; r++) {
            for (int c = 0; c < rot[0].length; c++) {
                rot[r][c] = og[c][r];
            }
        }
        setPreferredSize(new Dimension(rot[0].length, rot.length));
        JFrame jf = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
        jf.pack();
        return rot;
    }
    
    public Color[][] grayScale(Color[][] og){
        Color[][] fin = new Color[og.length][og[0].length];
        for (int r = 0; r < og.length; r++) {
            for (int c = 0; c < og[0].length; c++) {
                Color first = og[r][c];
                int avg = (first.getRed() + first.getBlue() + first.getGreen()) / 3;
                fin[r][c] = new Color (avg, avg, avg);
            }
        }
        return fin;
    }
    
    public Color[][] blur(Color[][] og){
        Color [][] blurred = new Color [og.length][og[0].length];
        final int RADIUS = 6;
        for (int r = 0; r < blurred.length; r++) {
            for (int c = 0; c < blurred[0].length; c++) {
                int red = 0;
                int blue = 0;
                int green = 0;
                int totPixels = 0;
                for (int i = r - RADIUS; i < r + RADIUS; i++) {
                    for (int j = c - RADIUS; j < c + RADIUS; j++) {
                        if (onScreen(i,j)){
                            red += og[i][j].getRed();
                            green += og[i][j].getGreen();
                            blue += og[i][j].getBlue();
                            totPixels++;
                        }
                    }
                }
                blurred[r][c] = new Color (red/totPixels, green/totPixels, blue/totPixels);
            }
        }
        return blurred;
    }

    public boolean onScreen (int i, int j){
        if (i >= 0 && j >= 0 && i < pixels.length && j < pixels[0].length){
            return true;
        }
        return false;
    }
    
    public Color[][] blueLight(Color[][] og){
        Color[][] blueImg = new Color[og.length][og[0].length];
        final double R_SCALE = 0.4;
        final double G_SCALE = 0.7;
        final double B_SCALE = 1.3;
        final int COLOR_MAX = 255;
        for (int r = 0; r < blueImg.length; r++) {
            for (int c = 0; c < blueImg[0].length; c++) {
                int red = (int) (og[r][c].getRed() * R_SCALE);
                int green = (int) (og[r][c].getGreen() * G_SCALE);
                int blue = (int) (og[r][c].getBlue() * B_SCALE);
                if (blue > COLOR_MAX){
                    blue = COLOR_MAX;
                }
                blueImg[r][c] = new Color (red, green, blue);
            }
        }
        return blueImg;
    }
    
    public Color[][] posterize(Color[][] og){
        Color[][] poster = new Color[og.length][og[0].length];
        Color yellow = new Color(250, 250, 200);
        Color pink = new Color(245, 60, 160);
        Color blue = new Color(60, 110, 220);
        Color purple = new Color(100, 5, 210);
        for (int r = 0; r < poster.length; r++) {
            for (int c = 0; c < poster[0].length; c++) {
                int y = difDist(og[r][c], yellow);
                int pi = difDist(og[r][c], pink);
                int b = difDist(og[r][c], blue);
                int pu = difDist(og[r][c], purple);
                if (y <= pi && y <= b && y <= pu){
                    poster[r][c] = yellow;
                } else if (pi <= b && y <= pu){
                    poster[r][c] = pink;
                } else if (b <= pu){
                    poster[r][c] = blue;
                } else {
                    poster[r][c] = purple;
                }
            }
        }
        return poster;
    }

     public int difDist(Color post, Color color){
        int r = post.getRed() - color.getRed();
        int g = post.getGreen() - color.getGreen();
        int b = post.getBlue() - color.getBlue();
        return (int)Math.sqrt((r*r) + (g*g) + (b*b));
     }
    
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'h'){
            pixels = flipHorizontal(pixels);        
        }
        if (e.getKeyChar() == 'v'){
            pixels = flipVertical(pixels);        
        }
        if (e.getKeyChar() == 'g'){
            pixels = grayScale(pixels);        
        }
        if (e.getKeyChar() == 'r'){
            pixels = rotate(pixels);        
        }
        if (e.getKeyChar() == 'b'){
            pixels = blur(pixels);        
        }
        if (e.getKeyChar() == 'p'){
            pixels = posterize(pixels);        
        }
        if (e.getKeyChar() == 't'){
            pixels = blueLight(pixels);        
        }
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    }
}



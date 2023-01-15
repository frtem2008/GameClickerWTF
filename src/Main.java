//основной игровой класс

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static final boolean enableFullScreen = false;
    public static boolean win = false;
    public static boolean drawAboba = false;
    //изображения
    private static Image Girl, Red, Blue, Chel, Background;
    //клавиатура + мышь
    public final Mouse mouse = new Mouse();
    private final Keyboard keyboard = new Keyboard(10);

    private final List<Player> players = new ArrayList<>();

    //начало игры ()
    public void startDrawing(JFrame frame) {
        new Thread(() -> {
            Audio track = new Audio("Sound bg", 1.0, 85000);
            track.sound();
            track.setVolume();
            track.repeat();
        }).start();

        new Thread(() -> {
            players.add(new Player(228, 219, 30, 30, 731, KeyEvent.VK_SHIFT));
            players.add(new Player(304, 170, 30, 30, 827, KeyEvent.VK_BACK_SPACE));
            //731, 609 <- 1
            //827, 575 <- 2

            //подгружаем изображения и прогружаем игру
            loadImages();
            //привязываем слушатели
            frame.addKeyListener(keyboard);
            frame.addMouseListener(mouse);
            frame.addMouseMotionListener(mouse);

            //изображение для отрисовки (для изменения пикселей после рисования объектов)
            BufferedImage frameImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);

            //создание буфера
            frame.createBufferStrategy(2);
            BufferStrategy bs = frame.getBufferStrategy();

            //для использования tab, alt и т.д
            frame.setFocusTraversalKeysEnabled(false);

            //для стабилизации и ограничения фпс
            long start, end, len;
            double frameLength;

            //графика итогового окна
            Graphics2D frameGraphics;

            //длина кадра (число после дроби - фпс)
            frameLength = 1000.0 / 60;
            int frames = 0;

            //размер JFrame на самом деле
            Dimension frameSize;


            //главный игровой цикл
            while (true) {
                //время начала кадра
                start = System.currentTimeMillis();

                //обновление размера JFrame
                frameSize = frame.getContentPane().getSize();
                //получение информации о буфере
                frameGraphics = (Graphics2D) bs.getDrawGraphics();

                //очистка экрана перед рисованием
                frameGraphics.clearRect(0, 0, frame.getWidth(), frame.getHeight());
                frameImage.getGraphics().clearRect(0, 0, frameImage.getWidth(), frameImage.getHeight());
                frameImage.getGraphics().drawImage(Background, 0, 0, frame.getWidth(), frame.getHeight(), null);
                //рисование на предварительном изображении
                this.draw(frameImage.getGraphics());
                //отрисовка миникарты

                //рисование на итоговом окне
                frameGraphics.drawImage(frameImage, 0, 0, frameImage.getWidth(), frameImage.getHeight(), null);

                //очистка мусора
                frameImage.getGraphics().dispose();
                frameGraphics.dispose();

                //показ буфера на холсте
                bs.show();

                //разворот на полный экран
                if (Keyboard.getF11() && enableFullScreen) {
                    while (Keyboard.getF11()) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    frame.dispose();
                    if (Display.isFullScreen) {
                        frame.setUndecorated(false);
                        frame.setExtendedState(Frame.NORMAL);
                        frame.setBounds(Display.x, Display.y, (int) frameSize.getWidth(), (int) frameSize.getHeight());
                    } else {
                        frame.setUndecorated(true);
                        frame.setExtendedState(6);
                    }
                    Display.isFullScreen = !Display.isFullScreen;
                    frame.setVisible(true);
                }

                //код для выхода из игры
                if (Keyboard.getQ()) {
                    System.out.println("Выход");
                    System.exit(20);
                }

                //перезагрузка игры
                if (Keyboard.getR()) {
                    System.out.println("Reloading...");
                    loadImages();
                    System.out.println("Reloading finished");
                }

                //обновления клавиатуры и игрока
                players.forEach(Player::move);
                frames++;


                //замер времени, ушедшего на отрисовку кадра
                end = System.currentTimeMillis();
                len = end - start;

                //стабилизация фпс
                if (len < frameLength) {
                    try {
                        Thread.sleep((long) (frameLength - len));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public void draw(Graphics g) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).draw(g, i % 2 == 1 ? Blue : Red);
        }
        g.drawImage(Girl, 558, 382, 34, 103, null);

        if (drawAboba)
            g.drawImage(Chel, -120, -364, 1600, 1600, null);


        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).win() && !win) {
                g.setFont(new Font("Comic sans", Font.BOLD, 46));
                g.setColor(new Color(255, 204, 255));
                g.drawString("Player " + (i + 1) + " WON!!!", 425, 354);

                new Timer().schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                System.exit(30);
                            }
                        }, 3000
                );
                if (Math.random() < 0.5)
                    drawAboba = true;
                win = true;
                break;
            }
        }

    }

    //функция загрузки изображений (путь к папке: Images/)
    public void loadImages() {
        System.out.println("Loading images");
        try {
            Girl = ImageIO.read(Main.class.getClassLoader().getResource("Images/Girl.png"));
            System.out.println("Girl");
            Red = ImageIO.read(Main.class.getClassLoader().getResource("Images/Player red.png"));
            System.out.println("Red");
            Blue = ImageIO.read(Main.class.getClassLoader().getResource("Images/Player blue.png"));
            System.out.println("Blue");
            Chel = ImageIO.read(Main.class.getClassLoader().getResource("Images/Chel.png"));
            System.out.println("Fire");
            Background = ImageIO.read(Main.class.getClassLoader().getResource("Images/Bg.png"));
            System.out.println("BG");
        } catch (IOException e) {
            System.out.println("Failed loading images");
            e.printStackTrace();
            return;
        }
        System.out.println("Finished loading images");
    }
}


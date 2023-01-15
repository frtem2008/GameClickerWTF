import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Audio {
    public boolean pl_audio;// ��������������� �����
    public long timer_p = 0;// ��������� ����� ��� ������� ��������
    public long timer_d;// (���������� �����������) ������������ �����
    private final String track; // ����� �����(�����)
    private Clip clip = null;// ������ �� ������ ������
    private FloatControl volumeC = null;// ��������� ���������
    private double wt; //������� ���������
    private long timer_f = 0;// �������� ����� ��������

    //����������� (����� �����, ������� ���������)
    public Audio(String track, double wt) {
        this.track = track;
        this.wt = wt;
        this.pl_audio = false;
    }

    //����������� (����� �����, ������� ���������, ���.������ ���� ����)
    public Audio(String track, double wt, long timer_d) {
        this.timer_d = timer_d;
        this.track = "Sounds/" + track + ".wav";
        this.pl_audio = false;
        this.wt = wt;//
    }


    public void sound() {
        //����� ��� ������ � ����������
        AudioInputStream tr = null; // ������ ������ AudioInputStream ����
        try {
            tr = AudioSystem.getAudioInputStream(Audio.class.getClassLoader().getResource(this.track)); // �������� AudioInputStream (������ ����)
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        try {
            clip = AudioSystem.getClip();//�������� ���������� ���������� Clip
            clip.open(tr); //��������� ��� �������� ����� � Clip
            //�������� ���������� ���������
            volumeC = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            clip.setFramePosition(0); //������������� ��������� �� �����
            clip.start(); //�������!!!

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    // ������������ ���� ������ ��������� ������������ � stop()
    public void play() {
        File f = new File(this.track);// �������� �������� ���� � f
        //����� ��� ������ � ����������
        AudioInputStream tr = null; // ������ ������ AudioInputStream ����
        try {
            tr = AudioSystem.getAudioInputStream(Audio.class.getClassLoader().getResource(this.track)); // �������� AudioInputStream (������ ����)
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        try {
            clip = AudioSystem.getClip();//�������� ���������� ���������� Clip
            clip.open(tr); //��������� ��� �������� ����� � Clip
            //�������� ���������� ���������
            volumeC = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            if (!this.pl_audio) {
                clip.setFramePosition(0); //������������� ��������� �� �����
                clip.start(); //�������!!!
                this.pl_audio = true; // ������
            }

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }


    }

    //����
    public void stop() {
        clip.stop();
        clip.close(); //���������
        this.pl_audio = false;
    }

    //������� ���������
    public void setVolume() {
        if (wt < 0) wt = 0;
        if (wt > 1) wt = 1;
        float min = volumeC.getMinimum();
        float max = volumeC.getMaximum();
        volumeC.setValue((max - min) * (float) wt + min);
    }

    // ������ (�������� � ������� ����������� ������� play())
    public void repeat() {
        if (this.pl_audio)
            clip.loop(5); //��������� n ���
    }

    // ������ ����� ( ��� ������ �����. play() � ����������� � timer_d)
    public void timer_play() {
        if (timer_p == 0) { //���� ������ �� �������
            timer_p = System.currentTimeMillis();// �������� ������� ����� ������
            timer_f = timer_p + timer_d; // �������� ����� �����
        }
        if (timer_f <= System.currentTimeMillis()) { //���� ����� �������� ������ �������������
            this.stop();//���� ����
            this.pl_audio = false;
            timer_p = 0;// �������� �������
        }
    }


}
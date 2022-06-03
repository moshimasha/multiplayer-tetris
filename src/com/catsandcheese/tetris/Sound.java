package com.catsandcheese.tetris;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
   public static final AudioClip BACK = Applet.newAudioClip(Sound.class.getResource("tetris-gameboy-02.wav"));
   public static final AudioClip FALL = Applet.newAudioClip(Sound.class.getResource("fall.wav"));
   public static final AudioClip CLEAR = Applet.newAudioClip(Sound.class.getResource("line.wav"));
}

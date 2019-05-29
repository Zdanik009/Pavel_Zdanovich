package com.epam.melotrack.converter;

import ua_parser.UserAgent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.epam.melotrack.converter.AudioFormat.*;
import static com.epam.melotrack.service.Service.EMPTY_STRING;

public class BrowserSupportedAudioFormats {

    private static BrowserSupportedAudioFormats instance;
    private static Map<UserAgent, AudioFormat[]> validAudioFormats;
    public final static String EDGE = "Edge";
    public final static String INTERNET_EXPLORER = "Internet Explorer";
    public final static String CHROME = "Chrome";
    public final static String OPERA = "Opera";
    public final static String FIREFOX = "Firefox";
    public final static String SAFARI = "Safari";

    private BrowserSupportedAudioFormats() {
        validAudioFormats = new HashMap<>();
        validAudioFormats.put(new UserAgent(EDGE, "14", "14291", EMPTY_STRING), new AudioFormat[]{MP3, MP4, WAV, AAC, FLAC});
        validAudioFormats.put(new UserAgent(INTERNET_EXPLORER, "9.0", "8112.16421", EMPTY_STRING), new AudioFormat[]{MP3, MP4});
        validAudioFormats.put(new UserAgent(CHROME, "3.0", "195", EMPTY_STRING), new AudioFormat[]{MP3, MP4, WAV, AAC, FLAC});
        validAudioFormats.put(new UserAgent(OPERA, "25", EMPTY_STRING, EMPTY_STRING), new AudioFormat[]{MP3, MP4, WAV, OGG, AAC, FLAC});
        validAudioFormats.put(new UserAgent(FIREFOX, "3.5", EMPTY_STRING, EMPTY_STRING), new AudioFormat[]{WAV, OGG});
        validAudioFormats.put(new UserAgent(SAFARI, "3.1", EMPTY_STRING, EMPTY_STRING), new AudioFormat[]{MP3, MP4, WAV});
    }

    public static BrowserSupportedAudioFormats getInstance() {
        if (instance == null) {
            instance = new BrowserSupportedAudioFormats();
        }
        return instance;
    }

    public AudioFormat defineSupportedAudioFromat(UserAgent userAgent) {
        AudioFormat audioFormat = MP3;
        if (userAgent != null) {
            for (UserAgent current : validAudioFormats.keySet()) {
                if (current.family.equalsIgnoreCase(userAgent.family)) {
                    audioFormat = Arrays.stream(validAudioFormats.get(current)).findFirst().orElse(MP3);
                }
            }
        }
        return audioFormat;
    }

}

package com.epam.melotrack.converter;

public enum AudioFormat {

    MP3, WAV, OGG, MP4, AAC, FLAC;

    @Override
    public String toString() {
        return "." + this.name().toLowerCase();
    }
}

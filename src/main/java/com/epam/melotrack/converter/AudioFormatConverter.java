package com.epam.melotrack.converter;

import it.sauronsoftware.jave.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static com.epam.melotrack.service.Service.TEMP;

public class AudioFormatConverter {

    private final static Logger logger = LogManager.getLogger();

    public final static int DEFAULT_BITRATE = 256_000;
    public final static int MIN_BITRATE = 32_000;
    public final static int MAX_BITRATE = 320_000;
    public final static int DEFAULT_STEREO_CHANNELS = 2;
    public final static int DEFAULT_MONO_CHANNEL = 1;
    public final static int DEFAULT_SAMPLING_RATE = 44_100;
    public final static int MIN_SAMPLING_RATE = 25_000;
    public final static int MAX_SAMPLING_RATE = 48_000;

    private final Encoder encoder;
    private AudioAttributes audioAttributes;
    private EncodingAttributes encodingAttributes;

    private int bitrate = DEFAULT_BITRATE;
    private int channels = DEFAULT_STEREO_CHANNELS;
    private int samplingRate = DEFAULT_SAMPLING_RATE;
    private String codec;

    public AudioFormatConverter() {
        this.encoder = new Encoder();
        audioAttributes = new AudioAttributes();
        encodingAttributes = new EncodingAttributes();
        audioAttributes.setBitRate(bitrate);
        audioAttributes.setChannels(channels);
        audioAttributes.setSamplingRate(samplingRate);
        encodingAttributes.setAudioAttributes(audioAttributes);
    }

    public AudioFormatConverter(int bitrate, int channels, int samplingRate, String codec) {
        this.encoder = new Encoder();
        audioAttributes = new AudioAttributes();
        encodingAttributes = new EncodingAttributes();
        setBitrate(bitrate);
        setChannels(channels);
        setSamplingRate(samplingRate);
        setCodec(codec);
        encodingAttributes.setAudioAttributes(audioAttributes);
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        if (bitrate >= MIN_BITRATE && bitrate <= MAX_BITRATE) {
            this.bitrate = bitrate;
        }
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        if (channels >= DEFAULT_MONO_CHANNEL && channels <= DEFAULT_STEREO_CHANNELS) {
            this.channels = channels;
        }
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        if (samplingRate >= MIN_SAMPLING_RATE && samplingRate <= MAX_SAMPLING_RATE) {
            this.samplingRate = samplingRate;
        }
    }

    public String getCodec() {
        return codec;
    }

    public boolean setCodec(String codec) {
        boolean result = false;
        if (codec != null) {
            try {
                List<String> encoders = Arrays.asList(encoder.getAudioEncoders());
                if (encoders.contains(codec)) {
                    this.codec = codec;
                    result = true;
                }
            } catch (EncoderException e) {
                logger.error("Checking supported decoder : " + codec + " failed due to ", e);
            }
        }
        return result;
    }

    private File convert(File source, File target) {
        String sourceFormat = FilenameUtils.getExtension(source.getName());
        String targetFormat = FilenameUtils.getExtension(target.getName());
        try {
            if (!source.exists() || source.isDirectory()) {
                throw new FileNotFoundException(source.getPath());
            }
            if ((isSupportedEncodingFormat(sourceFormat) && isSupportedDecodingFormat(targetFormat))) {
                encodingAttributes.setFormat(targetFormat);
                encoder.encode(source, target, encodingAttributes);
            }
        } catch (FileNotFoundException e) {
            logger.error("Invalid file : " + e.getMessage(), e);
        } catch (InputFormatException e) {
            logger.error("Invalid formats of converting files : " + source.getName() + ", " + target.getName(), e);
        } catch (EncoderException e) {
            logger.error("Converting " + sourceFormat + " to " + targetFormat + " failed due to ", e);
        }
        return target;
    }

    public File convert(InputStream inputStream, AudioFormat sourceFormat, File target) throws IOException {
        File file = null;
        if (inputStream != null && sourceFormat != null && target != null) {
            File source = File.createTempFile(TEMP, sourceFormat.toString());
            source.deleteOnExit();
            FileUtils.copyInputStreamToFile(inputStream, source);
            file = convert(source, target);
        }
        return file;
    }

    private boolean isSupportedEncodingFormat(String format) throws EncoderException {
        List<String> supportedEncodingAudioFormats = Arrays.asList(encoder.getSupportedEncodingFormats());
        return supportedEncodingAudioFormats.contains(format);
    }

    private boolean isSupportedDecodingFormat(String format) throws EncoderException {
        List<String> supportedDecodingAudioFormats = Arrays.asList(encoder.getSupportedDecodingFormats());
        return supportedDecodingAudioFormats.contains(format);
    }

}


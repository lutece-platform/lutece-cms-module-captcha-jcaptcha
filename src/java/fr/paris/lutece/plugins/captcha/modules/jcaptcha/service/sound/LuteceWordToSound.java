/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.captcha.modules.jcaptcha.service.sound;

import fr.paris.lutece.plugins.captcha.modules.jcaptcha.service.sound.filter.FilteredSoundStream;
import fr.paris.lutece.plugins.captcha.modules.jcaptcha.service.sound.filter.SoundFilter;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.octo.captcha.component.sound.soundconfigurator.SoundConfigurator;
import com.octo.captcha.component.sound.wordtosound.AbstractWordToSound;


/**
 *
 */
public class LuteceWordToSound extends AbstractWordToSound
{
    private static final String BLANK_SOUND_FILE_NAME = "white_sound";
    private static final String WAV_EXTENSION = ".wav";
    private static final String JCAPTCHA_SOUND_DIRECTORY = "jcaptcha.sound.directory";
    private static final float DEFAULT_SOUND_SAMPLE_RATE = 22050;
    private int _minAcceptedWordLength;
    private int _maxAcceptedWordLength;
    private int _minWhiteSoundNumber;
    private int _maxWhiteSoundNumber;
    private LuteceBackgroundSoundMixerConfigurator _backgroundSoundMixerConfigurator;
    private SoundFilter[] _filters;

    /**
     * 
     * @param configurator the configurator
     * @param minAcceptedWordLength the min Accepted Word Length
     * @param maxAcceptedWordLength the max Accepted Word Length
     * @param minWhiteSoundNumber the min White Sound Number
     * @param maxWhiteSoundNumber the max White Sound Number
     * @param mixerConfigurator the mixer Configurator
     * @param filters the filters
     */
    public LuteceWordToSound( SoundConfigurator configurator, int minAcceptedWordLength, int maxAcceptedWordLength,
            int minWhiteSoundNumber, int maxWhiteSoundNumber, LuteceBackgroundSoundMixerConfigurator mixerConfigurator,
            SoundFilter... filters )
    {
        super( configurator, minAcceptedWordLength, maxAcceptedWordLength );
        _minAcceptedWordLength = minAcceptedWordLength;
        _maxAcceptedWordLength = maxAcceptedWordLength;
        _minWhiteSoundNumber = minWhiteSoundNumber;
        _maxWhiteSoundNumber = maxWhiteSoundNumber;
        _backgroundSoundMixerConfigurator = mixerConfigurator;
        _filters = filters;
    }

    /**
     * @param word the word
     * @return the audio input stream
     */
    public AudioInputStream getSound( String word )
    {
        return getSound( word, Locale.getDefault( ) );
    }

    /**
     * @param word the word
     * @param locale the locale
     * @return the audio input stream
     */
    public AudioInputStream getSound( String word, Locale locale )
    {
        try
        {
            String soundFolder = AppPathService.getPath( JCAPTCHA_SOUND_DIRECTORY );
            File emptySound = new File( soundFolder + BLANK_SOUND_FILE_NAME + WAV_EXTENSION );
            int random;
            int whiteSoundCount = 0;
            int[] randomArray = new int[word.length( )];

            for ( int i = 0; i < word.length( ); i++ )
            {
                if ( i != ( word.length( ) - 1 ) )
                {
                    random = (int) ( Math.random( ) * ( ( _maxWhiteSoundNumber + 1 ) - _minWhiteSoundNumber ) )
                            + _minWhiteSoundNumber;
                }
                else
                {
                    random = 1;
                }

                randomArray[i] = random;
                whiteSoundCount += random;
            }

            File[] finalFileArray = new File[word.length( ) + whiteSoundCount];
            File soundFile;
            int finalArraySent = 0;

            for ( int i = 0; i < word.length( ); i++ )
            {
                soundFile = new File( soundFolder + word.charAt( i ) + WAV_EXTENSION );

                if ( soundFile.exists( ) )
                {
                    finalFileArray[finalArraySent++] = soundFile;
                }

                for ( int j = 0; j < randomArray[i]; j++ )
                {
                    finalFileArray[finalArraySent++] = emptySound;
                }
            }

            AudioInputStream result = AudioSystem.getAudioInputStream( new ByteArrayInputStream( AudioConcat.concat(
                    finalFileArray ).toByteArray( ) ) );

            return addEffects( result );
        }
        catch ( Exception e )
        {
            AppLogService.error( "Problem during sound generation", e );
        }

        return null;
    }

    /**
     * @return the Max Accepted Word Lenght
     */
    public int getMaxAcceptedWordLenght( )
    {
        return _maxAcceptedWordLength;
    }

    /**
     * @return the Max Accepted Word Length
     */
    public int getMaxAcceptedWordLength( )
    {
        return _maxAcceptedWordLength;
    }

    /**
     * @return the Min Accepted Word Lenght
     */
    public int getMinAcceptedWordLenght( )
    {
        return _minAcceptedWordLength;
    }

    /**
     * @return the Min Accepted Word Length
     */
    public int getMinAcceptedWordLength( )
    {
        return _minAcceptedWordLength;
    }

    /**
     * @param inputStream the input Stream
     * @return audio input stream
     */
    protected AudioInputStream addEffects( AudioInputStream inputStream )
    {
        try
        {
            AudioInputStream is = inputStream;
            AudioFormat originalFormat = inputStream.getFormat( );

            for ( SoundFilter filter : _filters )
            {
                filter.reset( );
                is = new AudioInputStream( new FilteredSoundStream( is, filter ), filter.getAudioFormat( is ),
                        is.available( ) );
            }

            if ( null != _backgroundSoundMixerConfigurator )
            {
                File backSound = _backgroundSoundMixerConfigurator.getRandomBackgroundFile( );
                AudioInputStream backStream = AudioSystem.getAudioInputStream( backSound );
                List<AudioInputStream> streamList;

                while ( backStream.available( ) < is.available( ) )
                {
                    streamList = new ArrayList<AudioInputStream>( );
                    streamList.add( backStream );
                    streamList.add( AudioSystem.getAudioInputStream( backSound ) );
                    backStream = AudioSystem.getAudioInputStream( new ByteArrayInputStream( AudioConcat.concat(
                            streamList, originalFormat ).toByteArray( ) ) );
                }

                is = new MixingFloatAudioInputStream( is.getFormat( ), is, backStream,
                        _backgroundSoundMixerConfigurator.getAttenuationValue( ) );
            }

            // return filtered sound stream
            return is;
        }
        catch ( Exception e )
        {
            AppLogService.error( "Problem during add effects", e );
        }

        // if failed return natural generated sound
        return inputStream;
    }

    /**
     * Static method to get used sounds sample rate
     * @return used sounds sample rate or default value (22050 Hz) if there is
     *         problem during analysis
     */
    public static float getSoundsSampleRate( )
    {
        File emptySound = new File( AppPathService.getPath( JCAPTCHA_SOUND_DIRECTORY ) + BLANK_SOUND_FILE_NAME
                + WAV_EXTENSION );

        try
        {
            return AudioSystem.getAudioInputStream( emptySound ).getFormat( ).getSampleRate( );
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return DEFAULT_SOUND_SAMPLE_RATE;
    }
}

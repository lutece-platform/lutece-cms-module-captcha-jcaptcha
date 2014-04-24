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

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.component.sound.wordtosound.WordToSound;
import com.octo.captcha.component.word.worddecorator.WordDecorator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.sound.SoundCaptchaFactory;
import com.octo.captcha.sound.gimpy.GimpySound;


/**
 *
 * @author lutecer
 *
 */
public class LuteceGimpySoundFactory extends SoundCaptchaFactory
{
    /**
     * The bundle question key for CaptchaQuestionHelper
     */
    public static final String MESSAGE_INVALID_EXCEPTION = "Invalid configuration for a ";
    public static final String BUNDLE_QUESTION_KEY = GimpySound.class.getName(  );
    private WordGenerator _wordGenerator;
    private WordToSound _word2Sound;
    private Random _myRandom = new SecureRandom(  );

    /**
     * Construct a GimpySoundFactory from a word generator component and a wordtosound component
     *
     * @param thewordGenerator component
     * @param theword2Sound    component
     */
    public LuteceGimpySoundFactory( WordGenerator thewordGenerator, WordToSound theword2Sound )
    {
        if ( thewordGenerator == null )
        {
            throw new CaptchaException( MESSAGE_INVALID_EXCEPTION + "GimpySoundFactory : WordGenerator can't be null" );
        }

        if ( theword2Sound == null )
        {
            throw new CaptchaException( MESSAGE_INVALID_EXCEPTION + "GimpySoundFactory : Word2Sound can't be null" );
        }

        _wordGenerator = thewordGenerator;
        _word2Sound = theword2Sound;
    }

    /**
     * Construct a GimpySoundFactory from a word generator component and a wordtosound component
     *
     * @param wordGenerator the wordGenerator component
     * @param word2Sound the word2Sound component
     * @param wordDecorator the word Decodator
     */
    public LuteceGimpySoundFactory( WordGenerator wordGenerator, WordToSound word2Sound, WordDecorator wordDecorator )
    {
        if ( wordGenerator == null )
        {
            throw new CaptchaException( MESSAGE_INVALID_EXCEPTION +
                "SpellingSoundFactory : WordGenerator can't be null" );
        }

        if ( word2Sound == null )
        {
            throw new CaptchaException( MESSAGE_INVALID_EXCEPTION + "SpellingSoundFactory : Word2Sound can't be null" );
        }

        if ( wordDecorator == null )
        {
            throw new CaptchaException( MESSAGE_INVALID_EXCEPTION +
                "SpellingSoundFactory : wordAbstractor can't be null" );
        }

        _wordGenerator = wordGenerator;
        _word2Sound = word2Sound;
    }

    /**
     *
     * @return the word to sound
     */
    public WordToSound getWordToSound(  )
    {
        return _word2Sound;
    }

    /**
     *
     * @return the word generator
     */
    public WordGenerator getWordGenerator(  )
    {
        return _wordGenerator;
    }

    /**
     * @return a Sound Captcha
     */
    public SoundCaptcha getSoundCaptcha(  )
    {
        String word = _wordGenerator.getWord( getRandomLength(  ), Locale.getDefault(  ) ).toLowerCase(  );
        AudioInputStream sound = _word2Sound.getSound( word );
        SoundCaptcha soundCaptcha = new GimpySound( getQuestion( Locale.getDefault(  ) ), sound, word );

        return soundCaptcha;
    }

    /**
     * @param locale the locale
     * @return a localized sound captcha
     */
    public SoundCaptcha getSoundCaptcha( Locale locale )
    {
        String word = _wordGenerator.getWord( getRandomLength(  ), locale ).toLowerCase(  );
        AudioInputStream sound = _word2Sound.getSound( word, locale );
        SoundCaptcha soundCaptcha = new GimpySound( getQuestion( locale ), sound, word );

        return soundCaptcha;
    }

    /**
     *
     * @param locale the locale
     * @return the question
     */
    protected String getQuestion( Locale locale )
    {
        return CaptchaQuestionHelper.getQuestion( locale, BUNDLE_QUESTION_KEY );
    }

    /**
     *
     * @return the random length of word
     */
    protected Integer getRandomLength(  )
    {
        Integer wordLength;
        int range = getWordToSound(  ).getMaxAcceptedWordLength(  ) - getWordToSound(  ).getMinAcceptedWordLength(  );
        int randomRange = ( range != 0 ) ? _myRandom.nextInt( range + 1 ) : 0;
        wordLength = Integer.valueOf( randomRange + getWordToSound( ).getMinAcceptedWordLength( ) );

        return wordLength;
    }
}

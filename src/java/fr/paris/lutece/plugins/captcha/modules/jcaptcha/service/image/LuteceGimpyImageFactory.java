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
package fr.paris.lutece.plugins.captcha.modules.jcaptcha.service.image;

import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.gimpy.Gimpy;


/**
 * Factories for Gimpies. Built on top of WordGenerator and WordToImage. It uses thoses interfaces to build an
 * ImageCaptha answered by a String and for which the question is : Spell the word.
 */
public class LuteceGimpyImageFactory extends com.octo.captcha.image.ImageCaptchaFactory
{
    public static final String BUNDLE_QUESTION_KEY = Gimpy.class.getName(  );
    private Random _myRandom = new SecureRandom(  );
    private WordToImage _wordToImage;
    private WordGenerator _wordGenerator;

    /**
     *
     * @param generator the generator
     * @param word2image the word to image
     */
    public LuteceGimpyImageFactory( WordGenerator generator, WordToImage word2image )
    {
        if ( word2image == null )
        {
            throw new CaptchaException( "Invalid configuration" + " for a GimpyFactory : WordToImage can't be null" );
        }

        if ( generator == null )
        {
            throw new CaptchaException( "Invalid configuration" + " for a GimpyFactory : WordGenerator can't be null" );
        }

        _wordToImage = word2image;
        _wordGenerator = generator;
    }

    /**
     * gimpies are ImageCaptcha
     *
     * @return the image captcha with default locale
     */
    public ImageCaptcha getImageCaptcha(  )
    {
        return getImageCaptcha( Locale.getDefault(  ) );
    }

    /**
     *
     * @return the word to the image
     */
    public WordToImage getWordToImage(  )
    {
        return _wordToImage;
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
     * gimpies are ImageCaptcha
     *
     * @param locale the locale
     *
     * @return a pixCaptcha with the question :"spell the word"
     */
    public ImageCaptcha getImageCaptcha( Locale locale )
    {
        //length
        Integer wordLength = getRandomLength(  );

        String word = getWordGenerator(  ).getWord( wordLength, locale );

        BufferedImage image = null;

        try
        {
            image = getWordToImage(  ).getImage( word );
        }
        catch ( Throwable e )
        {
            throw new CaptchaException( e );
        }

        ImageCaptcha captcha = new LuteceGimpyImage( CaptchaQuestionHelper.getQuestion( locale, BUNDLE_QUESTION_KEY ),
                image, word.toLowerCase(  ) );

        return captcha;
    }

    /**
     *
     * @return a random length for the word image
     */
    protected Integer getRandomLength(  )
    {
        Integer wordLength;
        int range = getWordToImage(  ).getMaxAcceptedWordLength(  ) - getWordToImage(  ).getMinAcceptedWordLength(  );
        int randomRange = ( range != 0 ) ? _myRandom.nextInt( range + 1 ) : 0;
        wordLength = Integer.valueOf( randomRange + getWordToImage( ).getMinAcceptedWordLength( ) );

        return wordLength;
    }
}

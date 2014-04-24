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
package fr.paris.lutece.plugins.captcha.modules.jcaptcha.service.sound.filter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;


/**
 *
 */
public class PitchFilter extends SoundFilter
{
    private float _pitchMinValue;
    private float _pitchMaxValue;

    /**
     * Creates an PitchFilter with the specified minimum and maximum relative original sound pitch (in percent)
     * <p>
     * @param pitchMinValue the pitch Min Value
     * @param pitchMaxValue the pitch Max Value
     */
    public PitchFilter( float pitchMinValue, float pitchMaxValue )
    {
        _pitchMinValue = pitchMinValue / 100f;
        this._pitchMaxValue = pitchMaxValue / 100f;
    }

    /**
     * Filter do nothing, only AudioFormat was modified
     *
     * @param samples the samples
     * @param offset the offset
     * @param length the length
     * @param sampleSizeInBits the sample size in bits
     */
    public void filter( byte[] samples, int offset, int length, int sampleSizeInBits )
    {
        // Do nothing
    }

    /**
     * Apply coefficient to the original audio input format pitch
     *
     * @param audioInputStream the audio input stream
     * @return the audio format
     */
    public AudioFormat getAudioFormat( AudioInputStream audioInputStream )
    {
        AudioFormat originalFormat = audioInputStream.getFormat(  );
        float coef = (float) ( Math.random(  ) * ( _pitchMaxValue - _pitchMinValue ) ) + _pitchMinValue;
        float messagePitchRate = originalFormat.getFrameRate(  ) * coef;

        return new AudioFormat( messagePitchRate, originalFormat.getSampleSizeInBits(  ),
            originalFormat.getChannels(  ), originalFormat.getEncoding(  ).equals( Encoding.PCM_SIGNED ),
            originalFormat.isBigEndian(  ) );
    }

    /**
     *
     * @return the pitch min value
     */
    public float getPitchMinValue(  )
    {
        return _pitchMinValue;
    }

    /**
     * ste the pitch min value
     *
     * @param pitchMinValue the pitch min value
     */
    public void setPitchMinValue( float pitchMinValue )
    {
        _pitchMinValue = pitchMinValue;
    }

    /**
     *
     * @return the pitch max value
     */
    public float getPitchMaxValue(  )
    {
        return _pitchMaxValue;
    }

    /**
     * set the pitch max value
     *
     * @param pitchMaxValue the pitch max value
     */
    public void setPitchMaxValue( float pitchMaxValue )
    {
        this._pitchMaxValue = pitchMaxValue;
    }
}

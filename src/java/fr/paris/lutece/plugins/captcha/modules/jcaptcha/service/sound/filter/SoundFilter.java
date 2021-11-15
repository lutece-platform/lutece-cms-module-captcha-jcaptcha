/*
 * Copyright (c) 2002-2021, City of Paris
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
import javax.sound.sampled.AudioInputStream;

/**
 * A abstract class designed to filter sound samples. Since SoundFilters may use internal buffering of samples, a new SoundFilter object should be created for
 * every sound played. However, SoundFilters can be reused after they are finished by called the reset() method.
 * <p>
 *
 * @see FilteredSoundStream
 */
public abstract class SoundFilter
{
    protected static final int SAMPLE_SIZE_16_BIT = 16;
    protected static final int SAMPLE_SIZE_8_BIT = 8;

    /**
     * Resets this SoundFilter. Does nothing by default.
     */
    public void reset( )
    {
        // do nothing
    }

    /**
     * Gets the remaining size, in bytes, that this filter plays after the sound is finished. An example would be an echo that plays longer than it's original
     * sound. This method returns 0 by default.
     *
     * @return remaining size
     */
    public int getRemainingSize( )
    {
        return 0;
    }

    /**
     * Filters an array of samples. Samples should be in 16-bit, signed, little-endian format. This method should be implemented by subclasses.
     *
     * @param samples
     *            the samples
     * @param offset
     *            the offset
     * @param length
     *            the length
     * @param sampleSizeInBits
     *            the sample size in bits
     */
    public abstract void filter( byte [ ] samples, int offset, int length, int sampleSizeInBits );

    /**
     * Convenience method for getting a 8-bit sample from a byte array. Samples should be in 8-bit, unsigned, little-endian format.
     *
     * @param buffer
     *            the buffer
     * @param position
     *            the position
     * @return 8 bit sample
     */
    public static short get8bitSample( byte [ ] buffer, int position )
    {
        return (short) ( buffer [position] & 0xff );
    }

    /**
     * Convenience method for setting a 8-bit sample in a byte array. Samples should be in 8-bit, unsigned, little-endian format.
     *
     * @param buffer
     *            the buffer
     * @param position
     *            the position
     * @param sample
     *            the sample
     *
     */
    public static void set8bitSample( byte [ ] buffer, int position, short sample )
    {
        buffer [position] = (byte) ( sample & 0xff );
    }

    /**
     * Convenience method for getting a 16-bit sample from a byte array. Samples should be in 16-bit, signed, little-endian format.
     *
     * @param buffer
     *            the buffer
     * @param position
     *            the position
     *
     * @return 16 bit sample
     *
     */
    public static short get16bitSample( byte [ ] buffer, int position )
    {
        return (short) ( ( ( buffer [position + 1] & 0xff ) << 8 ) | ( buffer [position] & 0xff ) );
    }

    /**
     * Convenience method for setting a 16-bit sample in a byte array. Samples should be in 16-bit, signed, little-endian format.
     *
     * @param buffer
     *            the buffer
     * @param position
     *            the position
     * @param sample
     *            the sample
     *
     */
    public static void set16bitSample( byte [ ] buffer, int position, short sample )
    {
        buffer [position] = (byte) ( sample & 0xff );
        buffer [position + 1] = (byte) ( ( sample >> 8 ) & 0xff );
    }

    /**
     * Return the audio format corresponding to the input stream Overrided by the filters that mofify AudioFormat
     *
     * @param audioInputStream
     *            the audio input stream
     * @return the audio format corresponding to the input stream
     */
    public AudioFormat getAudioFormat( AudioInputStream audioInputStream )
    {
        return audioInputStream.getFormat( );
    }
}

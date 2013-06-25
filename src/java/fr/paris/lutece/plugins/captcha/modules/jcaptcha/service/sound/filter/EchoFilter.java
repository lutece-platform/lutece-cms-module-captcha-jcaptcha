/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.captcha.modules.jcaptcha.service.sound.LuteceWordToSound;


/**
 *
 */
public class EchoFilter extends SoundFilter
{
    private short[] _delayBuffer;
    private int _delayBufferPos;
    private float _decay;

    /**
     * Creates an EchoFilter with the specified number of delay samples and the
     * specified decay rate.
     * <p>
     * The number of delay samples specifies how long before the echo is
     * initially heard. For a 1 second echo with mono, 44100Hz sound, use 44100
     * delay samples.
     * <p>
     * The decay value is how much the echo has decayed from the source. A decay
     * value of .5 means the echo heard is half as loud as the source.
     *
     * @param echoDelay the echoDelay
     * @param decay the decay
     */
    public EchoFilter( float echoDelay, float decay )
    {
        int numSampleDelay = ( Math.round( LuteceWordToSound.getSoundsSampleRate( ) * ( echoDelay / 1000 ) ) +
            1 );
        _delayBuffer = new short[numSampleDelay];
        _decay = decay / 100;
    }

    /**
     * Gets the remaining size, in bytes, of samples that this filter can echo
     * after the sound is done playing. Ensures that the sound will have decayed
     * to below 1% of maximum volume (amplitude).
     *
     * @return the Remaining Size
     */
    public int getRemainingSize(  )
    {
        float finalDecay = 0.01f;

        // derived from Math.pow(decay,x) <= finalDecay
        int numRemainingBuffers = (int) Math.ceil( Math.log( finalDecay ) / Math.log( _decay ) );
        int bufferSize = _delayBuffer.length;

        return bufferSize * numRemainingBuffers;
    }

    /**
     * Clears this EchoFilter's internal delay buffer.
     */
    public void reset(  )
    {
        for ( int i = 0; i < _delayBuffer.length; i++ )
        {
            _delayBuffer[i] = 0;
        }

        _delayBufferPos = 0;
    }

    /**
     * Filters the sound samples to add an echo. The samples played are added to
     * the sound in the delay buffer multipied by the decay rate. The result is
     * then stored in the delay buffer, so multiple echoes are heard.
     *
     * @param samples the samples
     * @param offset the offset
     * @param length the length
     * @param sampleSizeInBits the sample size in bits
     */
    public void filter( byte[] samples, int offset, int length, int sampleSizeInBits )
    {
        if ( sampleSizeInBits == SAMPLE_SIZE_8_BIT )
        {
            for ( int i = offset; i < ( offset + length ); i++ )
            {
                // update the sample
                short oldSample = get8bitSample( samples, i );
                short newSample = (short) ( oldSample + ( _decay * _delayBuffer[_delayBufferPos] ) );
                set8bitSample( samples, i, newSample );
                // update the delay buffer
                _delayBuffer[_delayBufferPos] = newSample;
                _delayBufferPos++;

                if ( _delayBufferPos == _delayBuffer.length )
                {
                    _delayBufferPos = 0;
                }
            }
        }
        else if ( sampleSizeInBits == SAMPLE_SIZE_16_BIT )
        {
            for ( int i = offset; i < ( offset + length ); i += 2 )
            {
                // update the sample
                short oldSample = get16bitSample( samples, i );
                short newSample = (short) ( oldSample + ( _decay * _delayBuffer[_delayBufferPos] ) );
                set16bitSample( samples, i, newSample );
                // update the delay buffer
                _delayBuffer[_delayBufferPos] = newSample;
                _delayBufferPos++;

                if ( _delayBufferPos == _delayBuffer.length )
                {
                    _delayBufferPos = 0;
                }
            }
        }
    }
}

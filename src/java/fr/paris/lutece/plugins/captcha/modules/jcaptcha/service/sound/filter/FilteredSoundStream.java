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

import java.io.FilterInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;


/**
 * The FilteredSoundStream class is a FilterInputStream that applies a
 * SoundFilter to the underlying input stream.
 *
 * @see SoundFilter
 */
public class FilteredSoundStream extends FilterInputStream
{
    private static final int REMAINING_SIZE_UNKNOWN = -1;
    private SoundFilter _soundFilter;
    private int _remainingSize;
    private int _sampleSizeInBits;

    /**
     * Creates a new FilteredSoundStream object with the specified InputStream
     * and SoundFilter.
     *
     * @param in the file sound stream
     * @param soundFilter the sound filter
     */
    public FilteredSoundStream( AudioInputStream in, SoundFilter soundFilter )
    {
        super( in );
        _soundFilter = soundFilter;
        _remainingSize = REMAINING_SIZE_UNKNOWN;
        _sampleSizeInBits = in.getFormat(  ).getSampleSizeInBits(  );
    }

    /**
     * Overrides the FilterInputStream method to apply this filter whenever
     * bytes are read
     *
     * @param samples the samples
     * @param offset the offset
     * @param length the length of sample
     * @return the byte read
     * @throws IOException the IOException
     */
    public int read( byte[] samples, int offset, int length )
        throws IOException
    {
        int nLengthReturn = length;

        // read and filter the sound samples in the stream
        int bytesRead = super.read( samples, offset, nLengthReturn );

        if ( bytesRead > 0 )
        {
            _soundFilter.filter( samples, offset, bytesRead, _sampleSizeInBits );

            return bytesRead;
        }

        // if there are no remaining bytes in the sound stream,
        // check if the filter has any remaining bytes ("echoes").
        if ( _remainingSize == REMAINING_SIZE_UNKNOWN )
        {
            _remainingSize = _soundFilter.getRemainingSize(  );
            // round down to nearest multiple of 4
            // (typical frame size)
            _remainingSize = _remainingSize / 4 * 4;
        }

        if ( _remainingSize > 0 )
        {
            nLengthReturn = Math.min( nLengthReturn, _remainingSize );

            // clear the buffer
            for ( int i = offset; i < ( offset + nLengthReturn ); i++ )
            {
                samples[i] = 0;
            }

            // filter the remaining bytes
            _soundFilter.filter( samples, offset, nLengthReturn, _sampleSizeInBits );
            _remainingSize -= nLengthReturn;

            return nLengthReturn;
        }
        else
        {
            // end of stream
            return -1;
        }
    }
}

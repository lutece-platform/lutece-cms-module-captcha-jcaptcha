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
package fr.paris.lutece.plugins.captcha.modules.jcaptcha.service.sound;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


/**
 *
 * @author lutecer
 *
 */
public class SequenceAudioInputStream extends AudioInputStream
{
    private List<AudioInputStream> _audioInputStreamList;
    private int _nCurrentStream;

    /**
     *
     * @param audioFormat the audio Format
     * @param audioInputStreams the audio InputStreams
     */
    public SequenceAudioInputStream( AudioFormat audioFormat, Collection<AudioInputStream> audioInputStreams )
    {
        super( new ByteArrayInputStream( new byte[0] ), audioFormat, AudioSystem.NOT_SPECIFIED );
        _audioInputStreamList = new ArrayList<AudioInputStream>( audioInputStreams );
        _nCurrentStream = 0;
    }

    /**
     *
     * @return the audio inputStream
     */
    private AudioInputStream getCurrentStream(  )
    {
        return (AudioInputStream) _audioInputStreamList.get( _nCurrentStream );
    }

    /**
     *
     * @return true if mcurrent stream < audioInputStreamList size
     */
    private boolean advanceStream(  )
    {
        _nCurrentStream++;

        boolean bAnotherStreamAvailable = ( _nCurrentStream < _audioInputStreamList.size(  ) );

        return bAnotherStreamAvailable;
    }

    /**
     * @return frame length
     */
    public long getFrameLength(  )
    {
        long lLengthInFrames = 0;
        Iterator<AudioInputStream> streamIterator = _audioInputStreamList.iterator(  );

        while ( streamIterator.hasNext(  ) )
        {
            AudioInputStream stream = (AudioInputStream) streamIterator.next(  );
            long lLength = stream.getFrameLength(  );

            if ( lLength == AudioSystem.NOT_SPECIFIED )
            {
                return AudioSystem.NOT_SPECIFIED;
            }
            else
            {
                lLengthInFrames += lLength;
            }
        }

        return lLengthInFrames;
    }

    /**
     *  @return the byte read
     *  @throws IOException the IOException
     */
    public int read(  ) throws IOException
    {
        AudioInputStream stream = getCurrentStream(  );
        int nByte = stream.read(  );

        if ( nByte == -1 )
        {
            /*
             * The end of the current stream has been signaled. We try to
             * advance to the next stream.
             */
            boolean bAnotherStreamAvailable = advanceStream(  );

            if ( bAnotherStreamAvailable )
            {
                /*
                 * There is another stream. We recurse into this method to read
                 * from it.
                 */
                return read(  );
            }
            else
            {
                /*
                 * No more data. We signal EOF.
                 */
                return -1;
            }
        }
        else
        {
            /*
             * The most common case: We return the byte.
             */
            return nByte;
        }
    }

    /**
     * @param abData the data
     * @param nOffset the offset
     * @param nLength the length
     * @return the bytes read
     * @throws IOException the IOException
     */
    public int read( byte[] abData, int nOffset, int nLength )
        throws IOException
    {
        AudioInputStream stream = getCurrentStream(  );
        int nBytesRead = stream.read( abData, nOffset, nLength );

        if ( nBytesRead == -1 )
        {
            /*
             * The end of the current stream has been signaled. We try to
             * advance to the next stream.
             */
            boolean bAnotherStreamAvailable = advanceStream(  );

            if ( bAnotherStreamAvailable )
            {
                /*
                 * There is another stream. We recurse into this method to read
                 * from it.
                 */
                return read( abData, nOffset, nLength );
            }
            else
            {
                /*
                 * No more data. We signal EOF.
                 */
                return -1;
            }
        }
        else
        {
            /*
             * The most common case: We return the length.
             */
            return nBytesRead;
        }
    }

    /**
     * @return the current stream available
     * @throws IOException the IOException
     */
    public int available(  ) throws IOException
    {
        return getCurrentStream(  ).available(  );
    }
}
/** * SequenceAudioInputStream.java ** */

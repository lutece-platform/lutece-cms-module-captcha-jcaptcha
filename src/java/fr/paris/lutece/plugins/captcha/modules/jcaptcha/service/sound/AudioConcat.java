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

import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


/**
 *
 */
public final class AudioConcat
{
    /**
     *
     *
     */
    private AudioConcat(  )
    {
    }

    /**
     * Return byte array of concatened audio files
     *
     * @param files
     *            to cancatenate
     * @return byte array of concatened audio files
     */
    public static ByteArrayOutputStream concat( File... files )
    {
        List<AudioInputStream> audioInputStreamList = new ArrayList<AudioInputStream>(  );
        AudioInputStream audioInputStream = null;
        AudioFormat audioFormat = null;

        for ( File soundFile : files )
        {
            try
            {
                audioInputStream = AudioSystem.getAudioInputStream( soundFile );
            }
            catch ( Exception e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }

            audioInputStreamList.add( audioInputStream );

            AudioFormat format = audioInputStream.getFormat(  );

            if ( audioFormat == null )
            {
                audioFormat = format;
            }
        }

        return concat( audioInputStreamList, audioFormat );
    }

    /**
     * Return byte array of concatened stream list.
     *
     * @param audioInputStreamList
     *            the list to contatenate
     * @param audioFormat
     *            the result format
     * @return byte array of concatened stream list.
     */
    public static ByteArrayOutputStream concat( List<AudioInputStream> audioInputStreamList, AudioFormat audioFormat )
    {
        AudioInputStream audioInputStream = new SequenceAudioInputStream( audioFormat, audioInputStreamList );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(  );

        try
        {
            AudioSystem.write( audioInputStream, AudioFileFormat.Type.WAVE, outputStream );
            outputStream.flush(  );
            outputStream.close(  );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return outputStream;
    }
}

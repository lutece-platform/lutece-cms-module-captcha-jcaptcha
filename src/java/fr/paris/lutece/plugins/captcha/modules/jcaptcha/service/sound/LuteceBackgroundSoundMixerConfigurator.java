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

import fr.paris.lutece.portal.service.util.AppPathService;

import java.io.File;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author lutecer
 *
 */
public class LuteceBackgroundSoundMixerConfigurator
{
    private static final String STRING_SLASH = "/";
    private static final String WAV_EXTENSION = ".wav";
    private static final String JCAPTCHA_SOUND_DIRECTORY = "jcaptcha.sound.directory";
    private float _attenuationValue;
    private File[] _backgroundSoundFiles = new File[0];

    /**
     *
     * @param attenuationValue the attenuation value
     * @param backGroundFileNames the backGround files Names
     */
    public LuteceBackgroundSoundMixerConfigurator( int attenuationValue, String... backGroundFileNames )
    {
        _attenuationValue = attenuationValue / 100f;

        String soundFolder = AppPathService.getPath( JCAPTCHA_SOUND_DIRECTORY );
        List<File> tempFileList = new ArrayList<File>(  );
        File soundFile;

        for ( int i = 0; i < backGroundFileNames.length; i++ )
        {
            soundFile = new File( soundFolder + STRING_SLASH + backGroundFileNames[i] + WAV_EXTENSION );

            if ( soundFile.exists(  ) )
            {
                tempFileList.add( soundFile );
            }
        }

        _backgroundSoundFiles = (File[]) tempFileList.toArray( _backgroundSoundFiles );
    }

    /**
     *
     * @return the attenuation value
     */
    public float getAttenuationValue(  )
    {
        return _attenuationValue;
    }

    /**
     *
     * @return the random background file
     */
    public File getRandomBackgroundFile(  )
    {
        int randomFileInt = (int) Math.rint( _backgroundSoundFiles.length * Math.random(  ) );

        if ( randomFileInt >= _backgroundSoundFiles.length )
        {
            randomFileInt = _backgroundSoundFiles.length - 1;
        }

        return _backgroundSoundFiles[randomFileInt];
    }
}

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

import com.octo.captcha.component.sound.soundconfigurator.SoundConfigurator;


/**
 *
 * @author lutecer
 *
 */
public class LuteceSoundConfigurator implements SoundConfigurator
{
    private static final String CAPTCHA_CONFIGURATION = "Lutèce JCaptcha Configuration";
    private String _location;
    private String _name;
    private float _pitch;
    private float _rate;
    private float _volume;

    /**
     *
     * @param pitch the pitch
     * @param rate the rate
     * @param volume the volume
     */
    public LuteceSoundConfigurator( float pitch, float rate, float volume )
    {
        _location = "./";
        _name = CAPTCHA_CONFIGURATION;
        _pitch = pitch;
        _rate = rate;
        _volume = volume;
    }

    /**
     *  @return the location
     */
    public String getLocation(  )
    {
        return _location;
    }

    /**
     * @return the name
     */
    public String getName(  )
    {
        return _name;
    }

    /**
     * @return the pitch
     */
    public float getPitch(  )
    {
        return _pitch;
    }

    /**
     * @return the rate
     */
    public float getRate(  )
    {
        return _rate;
    }

    /**
     * @return the volume
     */
    public float getVolume(  )
    {
        return _volume;
    }
}

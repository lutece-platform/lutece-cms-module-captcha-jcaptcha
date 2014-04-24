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
package fr.paris.lutece.plugins.captcha.modules.jcaptcha.service;

import com.octo.captcha.Captcha;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.engine.bufferedengine.QuartzBufferedEngineContainer;
import com.octo.captcha.service.multitype.GenericManageableCaptchaService;

import java.util.Locale;


/**
 *
 */
public class QuartzBufferedManageableCaptchaService extends GenericManageableCaptchaService
{
    private QuartzBufferedEngineContainer _container;

    /**
     *
     * @param engine the engine
     * @param container the container
     * @param minGuarantedStorageDelayInSeconds the min Guaranted Storage Delay In Seconds
     * @param maxCaptchaStoreSize max Captcha Store Size
     */
    public QuartzBufferedManageableCaptchaService( CaptchaEngine engine, QuartzBufferedEngineContainer container,
        int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize )
    {
        super( engine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, 0 );
        _container = container;
    }

    /**
     * @param locale the locale
     * @param strID the ID
     *
     * @return Captcha the Captcha
     *
     */
    protected Captcha generateAndStoreCaptcha( Locale locale, String strID )
    {
        Captcha captcha = _container.getNextCaptcha( locale );
        this.store.storeCaptcha( strID, captcha, locale );

        return captcha;
    }

    /**
     *
     * @return the container
     */
    public QuartzBufferedEngineContainer getContainer(  )
    {
        return _container;
    }

    /**
     *
     * @param container the container
     */
    public void setContainer( QuartzBufferedEngineContainer container )
    {
        _container = container;
    }
}

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
package fr.paris.lutece.plugins.captcha.modules.jcaptcha.service;

import fr.paris.lutece.plugins.captcha.service.ICaptchaEngine;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.octo.captcha.service.sound.SoundCaptchaService;


/**
 *
 */
public class JCaptchaService implements ICaptchaEngine
{
    private static final String CAPTCHA_PROVIDER = "JCaptcha";
    private static final String TEMPLATE_JCAPTCHA = "jcaptcha.template.captchaTemplate";
    private static final String LOGGER = "lutece.captcha";
    private static final String PARAMETER_HONEY_POT = "jcaptchahoneypot";
    private static final String PARAMETER_J_CAPTCHA_RESPONSE = "j_captcha_response";
    private static final String BEAN_NAME_JCAPTCHA_IMAGE_SERVICE = "jcaptcha.imageCaptchaService";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate( HttpServletRequest request )
    {
        AppLogService.debug( LOGGER, "Validate captcha response for id : " + request.getSession(  ).getId(  ) );

        // We use a honey pot : this parameter has an empty value and is hidden, thus it can not be modified by humans 
        String strHoneyPot = request.getParameter( PARAMETER_HONEY_POT );
        if ( StringUtils.isNotBlank( strHoneyPot ) )
        {
            return false;
        }

        String captchaReponse = request.getParameter( PARAMETER_J_CAPTCHA_RESPONSE ).toLowerCase( );
        ImageCaptchaService imageCaptcha = (ImageCaptchaService) SpringContextService
                .getBean( BEAN_NAME_JCAPTCHA_IMAGE_SERVICE );
        SoundCaptchaService soundCaptcha = (SoundCaptchaService) SpringContextService
                .getBean( BEAN_NAME_JCAPTCHA_IMAGE_SERVICE );
        boolean validImage = false;
        boolean validSound = false;
        String sessionId = request.getSession(  ).getId(  );

        try
        {
            validImage = imageCaptcha.validateResponseForID( sessionId, captchaReponse );
            validSound = soundCaptcha.validateResponseForID( sessionId, captchaReponse );
        }
        catch ( CaptchaServiceException e )
        {
            AppLogService.debug( LOGGER, e );
        }

        if ( validImage || validSound )
        {
            AppLogService.debug( LOGGER, "Valid response" );

            return true;
        }
        AppLogService.debug( LOGGER, "Unvalid response" );
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlCode( )
    {
        String strCaptchaTemplate = AppPropertiesService.getProperty( TEMPLATE_JCAPTCHA );

        HtmlTemplate captchaTemplate = AppTemplateService.getTemplate( strCaptchaTemplate );

        return captchaTemplate.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCaptchaEngineName( )
    {
        return CAPTCHA_PROVIDER;
    }
}

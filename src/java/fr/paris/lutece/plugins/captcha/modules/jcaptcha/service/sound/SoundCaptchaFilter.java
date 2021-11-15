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
/**
 * Nom du Fichier : $RCSfile: CaptchaServlet.java,v $
 * Version CVS : $Revision: 1.6 $
 * Auteur : A.Floquet
 * Description : Servlet de génération de l'image captcha.
 *
 */
package fr.paris.lutece.plugins.captcha.modules.jcaptcha.service.sound;

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.multitype.GenericManageableCaptchaService;

/**
 * Generation captcha class
 *
 * This class invok captcha service from applicationContext and get a challenge. The challenge response temp stored in service map, with user session id
 * key.<br>
 * this servlet throw generated sound, wav formatted.
 */
public class SoundCaptchaFilter implements Filter
{
    private static final String SOUND_CAPTCHA_SERVICE_NAME = "jcaptcha.soundCaptchaService";
    private static final String LOGGER = "lutece.captcha";
    private static final long serialVersionUID = -1806578484091247923L;

    /**
     * {@inheritDoc}
     */
    public void init( FilterConfig config ) throws ServletException
    {
    }

    /**
     * Apply the filter
     * 
     * @param req
     *            The HTTP request
     * @param res
     *            The HTTP response
     * @param filterChain
     *            The Filter Chain
     * @throws IOException
     *             If an error occured
     * @throws ServletException
     *             If an error occured
     */
    public void doFilter( ServletRequest req, ServletResponse res, FilterChain filterChain ) throws IOException, ServletException
    {
        AppLogService.debug( LOGGER, "challenge captcha generation start" );

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        byte [ ] captchaChallengeSound = null;
        ByteArrayOutputStream soundOutputStream = new ByteArrayOutputStream( );

        try
        {
            String captchaIdSound = request.getSession( ).getId( );

            // grab bean
            GenericManageableCaptchaService captcha = (GenericManageableCaptchaService) SpringContextService.getBean( SOUND_CAPTCHA_SERVICE_NAME );
            AppLogService.info( "captcha : " + captcha );

            AudioInputStream challengeSound = captcha.getSoundChallengeForID( captchaIdSound, request.getLocale( ) );
            AudioSystem.write( challengeSound, AudioFileFormat.Type.WAVE, soundOutputStream );
            soundOutputStream.flush( );
            soundOutputStream.close( );
        }
        catch( IllegalArgumentException e )
        {
            AppLogService.error( "exception : " + e.getMessage( ), e );
            response.sendError( HttpServletResponse.SC_NOT_FOUND );

            return;
        }
        catch( CaptchaServiceException e )
        {
            AppLogService.error( "exception :" + e.getMessage( ), e );
            response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );

            return;
        }

        // flush it in the response
        captchaChallengeSound = soundOutputStream.toByteArray( );
        response.setHeader( "cache-control", "no-cache, no-store,must-revalidate,max-age=0" );
        response.setHeader( "Content-Length", "" + captchaChallengeSound.length );
        response.setHeader( "expires", "1" );
        response.setContentType( "audio/x-wav" );

        ServletOutputStream responseOutputStream = response.getOutputStream( );
        responseOutputStream.write( captchaChallengeSound );
        responseOutputStream.flush( );
        responseOutputStream.close( );
        AppLogService.debug( LOGGER, "captcha challenge generation end" );
    }

    /**
     * Destroy the filter
     */
    public void destroy( )
    {
        // no-op
    }
}

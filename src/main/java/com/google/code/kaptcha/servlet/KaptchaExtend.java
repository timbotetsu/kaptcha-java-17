package com.google.code.kaptcha.servlet;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class KaptchaExtend {
    private Properties props = new Properties();

    private Producer kaptchaProducer;

    private String sessionKeyValue;

    private String sessionKeyDateValue;

    public KaptchaExtend() {
        // Switch off disk based caching.
        ImageIO.setUseCache(false);

        this.props.put("kaptcha.border", "no");
        this.props.put("kaptcha.textproducer.font.color", "black");
        this.props.put("kaptcha.textproducer.char.space", "5");

        Config config = new Config(this.props);
        this.kaptchaProducer = config.getProducerImpl();
        this.sessionKeyValue = config.getSessionKey();
        this.sessionKeyDateValue = config.getSessionDate();
    }

    /**
     * map it to the /url/captcha.jpg
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    public void captcha(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Set standard HTTP/1.1 no-cache headers.
        resp.setHeader("Cache-Control", "no-store, no-cache");

        // return a jpeg
        resp.setContentType("image/jpeg");

        // create the text for the image
        String capText = this.kaptchaProducer.createText();

        // store the text in the session
        req.getSession().setAttribute(this.sessionKeyValue, capText);

        // store the date in the session so that it can be compared
        // against to make sure someone hasn't taken too long to enter
        // their kaptcha
        req.getSession().setAttribute(this.sessionKeyDateValue, new Date());

        // create the image with the text
        BufferedImage bi = this.kaptchaProducer.createImage(capText);

        ServletOutputStream out = resp.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);

        // fixes issue #69: set the attributes after we write the image in case
        // the image writing fails.

        // store the text in the session
        req.getSession().setAttribute(this.sessionKeyValue, capText);

        // store the date in the session so that it can be compared
        // against to make sure someone hasn't taken too long to enter
        // their kaptcha
        req.getSession().setAttribute(this.sessionKeyDateValue, new Date());
    }

    public String getGeneratedKey(HttpServletRequest req) {
        HttpSession session = req.getSession();
        return (String) session.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
    }
}

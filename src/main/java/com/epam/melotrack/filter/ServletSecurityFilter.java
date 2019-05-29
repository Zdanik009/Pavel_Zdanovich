package com.epam.melotrack.filter;

import com.epam.melotrack.command.Command;
import com.epam.melotrack.command.CommandType;
import com.epam.melotrack.dao.impl.TourDaoImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.epam.melotrack.service.Service.*;

@WebFilter(urlPatterns = "/*")
public class ServletSecurityFilter implements Filter {

    private final static Logger logger = LogManager.getLogger();
    private final static List<String> allowedTransition = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) {
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CONTROLLER);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CONTROLLER + SLASH);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CSS + SLASH + TourDaoImpl.TEMPORARY_DIRECTORY_NAME + DOT + CSS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CSS + SLASH + LOGIN + DOT + CSS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CSS + SLASH + MAIN + DOT + CSS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CSS + SLASH + NAVIGATION + DOT + CSS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CSS + SLASH + UPLOAD + DOT + CSS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + EXAMPLE + SLASH + EXAMPLE_SONG);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + IMAGE + SLASH + LOGO_IMAGE);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + JS + SLASH + EXAMPLE + DOT + JS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + JS + SLASH + LOGIN + DOT + JS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + JS + SLASH + PLAYER + DOT + JS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + JS + SLASH + TIMER + DOT + JS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + JS + SLASH + LOADER + DOT + JS);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CONTROLLER + QUESTION_MARK + COMMAND + EQUALS_SIGN + CommandType.CHANGE_LANGUAGE);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CONTROLLER + QUESTION_MARK + COMMAND + EQUALS_SIGN + CommandType.SHOW_MAIN_PAGE);
        allowedTransition.add(SLASH + PAVEL_ZDANOVICH_WAR + SLASH + CONTROLLER + QUESTION_MARK + COMMAND + EQUALS_SIGN + CommandType.SHOW_ABOUT_PAGE);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String requestURI = httpServletRequest.getRequestURI();
        if (requestURI != null && !allowedTransition.contains(requestURI)) {
            logger.error("Unsupported request URI : " + requestURI);
            httpServletRequest.getRequestDispatcher(MAIN_PAGE).forward(httpServletRequest, httpServletResponse);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}

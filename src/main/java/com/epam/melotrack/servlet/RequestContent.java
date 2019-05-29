package com.epam.melotrack.servlet;

import com.epam.melotrack.converter.AudioFormat;
import com.epam.melotrack.converter.BrowserSupportedAudioFormats;
import com.epam.melotrack.entity.Song;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.epam.melotrack.converter.AudioFormat.MP3;
import static com.epam.melotrack.converter.BrowserSupportedAudioFormats.*;
import static com.epam.melotrack.service.Service.*;

public class RequestContent {

    private final static Logger logger = LogManager.getLogger();
    private static String servletContextRealPath;
    private static boolean isConstantsDefined = false;
    private Map<String, String[]> requestParameters;
    private Map<String, Object> requestAttributes;
    private Map<String, Object> sessionAttributes;
    private Collection<Part> requestParts;
    private Map<String, Object> servletContextAttributes;
    private List<String> removedRequestAttributes;
    private List<String> removedSessionAttributes;
    private List<String> removedServletContextAttributes;

    public RequestContent(HttpServletRequest httpServletRequest) {
        requestAttributes = new HashMap<>();
        sessionAttributes = new HashMap<>();
        servletContextAttributes = new HashMap<>();
        removedRequestAttributes = new ArrayList<>();
        removedSessionAttributes = new ArrayList<>();
        removedServletContextAttributes = new ArrayList<>();
        extractValues(httpServletRequest);
        defineConstants(httpServletRequest);
    }

    public void extractValues(HttpServletRequest httpServletRequest) {
        requestParameters = httpServletRequest.getParameterMap();
        Enumeration<String> requestAttributeNames = httpServletRequest.getAttributeNames();
        String currentRequestAttributeName;
        while (requestAttributeNames.hasMoreElements()) {
            currentRequestAttributeName = requestAttributeNames.nextElement();
            requestAttributes.put(currentRequestAttributeName, httpServletRequest.getAttribute(currentRequestAttributeName));
        }
        HttpSession httpSession = httpServletRequest.getSession(true);
        Enumeration<String> sessionAtrributeNames = httpSession.getAttributeNames();
        String currentSessionAtrributeName;
        while (sessionAtrributeNames.hasMoreElements()) {
            currentSessionAtrributeName = sessionAtrributeNames.nextElement();
            sessionAttributes.put(currentSessionAtrributeName, httpSession.getAttribute(currentSessionAtrributeName));
        }
        String contentType = httpServletRequest.getContentType();
        if (contentType != null && contentType.contains(MULTIPART_FORM_DATA)) {
            try {
                requestParts = httpServletRequest.getParts().stream().filter(part -> part.getContentType() != null).collect(Collectors.toList());
            } catch (ServletException e) {
                logger.error("Servlet getResultBySetOfToursId parts error", e);
            } catch (IOException e) {
                logger.error("Reading/Writing part error", e);
            }
        }
        ServletContext servletContext = httpServletRequest.getServletContext();
        Enumeration<String> servletContextAttributeNames = servletContext.getAttributeNames();
        String currentServletContextAttributeName;
        while (servletContextAttributeNames.hasMoreElements()) {
            currentServletContextAttributeName = servletContextAttributeNames.nextElement();
            servletContextAttributes.put(currentServletContextAttributeName, servletContext.getAttribute(currentServletContextAttributeName));
        }
    }

    public void insertValues(HttpServletRequest httpServletRequest) {
        requestAttributes.forEach((key, value) -> httpServletRequest.setAttribute(key, value));
        if (removedRequestAttributes != null && !removedRequestAttributes.isEmpty()) {
            removedRequestAttributes.forEach(attributeName -> httpServletRequest.removeAttribute(attributeName));
        }
        HttpSession session = httpServletRequest.getSession(true);
        sessionAttributes.forEach((key, value) -> session.setAttribute(key, value));
        if (removedSessionAttributes != null && !removedSessionAttributes.isEmpty()) {
            removedSessionAttributes.forEach(attributeName -> session.removeAttribute(attributeName));
        }
        ServletContext servletContext = httpServletRequest.getServletContext();
        servletContextAttributes.forEach((key, value) -> servletContext.setAttribute(key, value));
        if (removedServletContextAttributes != null && !removedServletContextAttributes.isEmpty()) {
            removedServletContextAttributes.forEach(attributeName -> servletContext.removeAttribute(attributeName));
        }
    }

    public Object getRequestParameter(String parameterName) {
        Object result = null;
        if (parameterName != null && requestParameters.containsKey(parameterName)) {
            String[] parameters = requestParameters.get(parameterName);
            if (parameters.length == 1) {
                result = parameters[0];
            } else {
                result = parameters;
            }
        }
        return result;
    }

    public Object getRequestAttribute(String attributeName) {
        Object result = null;
        if (attributeName != null && requestAttributes.containsKey(attributeName)) {
            result = requestAttributes.get(attributeName);
        }
        return result;
    }

    public Object getServletContent(String servletContextAttributeName) {
        Object result = null;
        if (servletContextAttributeName != null && servletContextAttributes.containsKey(servletContextAttributeName)) {
            result = servletContextAttributes.get(servletContextAttributeName);
        }
        return result;
    }

    public File getRequestPart(String requestPartName) {
        File result = null;
        if (requestPartName != null && requestParts != null && !requestParts.isEmpty() && requestParts.stream().anyMatch(part -> part.getName().equals(requestPartName))) {
            try {
                File file = File.createTempFile(TEMP, MP3.toString());
                requestParts.stream().filter(part -> {
                    String fileName = part.getHeader(CONTENT_DISPOSITION);
                    String fileExtension = fileName.substring(fileName.lastIndexOf(DOT)).replaceAll(QUOTE, EMPTY_STRING);
                    long fileSize = part.getSize();
                    return (fileExtension.equals(MP3.toString()) && fileSize <= Song.DEFAULT_MAX_FILE_SIZE);
                }).findFirst().ifPresent(part -> {
                    try (InputStream inputStream = part.getInputStream()) {
                        FileUtils.copyInputStreamToFile(inputStream, file);
                    } catch (IOException e) {
                        logger.error("Reading/Writing input stream error due to ", e);
                    }
                });
                if (FileUtils.sizeOf(file) != 0) {
                    result = file;
                }
            } catch (IOException e) {
                logger.error("Creating file error due to ", e);
            }
        }
        return result;
    }

    public boolean setRequestAttribute(String contentName, Object object) {
        boolean result = false;
        if (contentName != null && !contentName.isEmpty() && object != null) {
            result = (this.requestAttributes.put(contentName, object) != null);
        }
        return result;
    }

    public boolean setSessionAttribute(String contentName, Object object) {
        boolean result = false;
        if (contentName != null && !contentName.isEmpty() && object != null) {
            result = (this.sessionAttributes.put(contentName, object) != null);
        }
        return result;
    }

    public boolean setServletContent(String contentName, Object object) {
        boolean result = false;
        if (contentName != null && !contentName.isEmpty() && object != null) {
            result = (this.servletContextAttributes.put(contentName, object) != null);
        }
        return result;
    }

    public boolean removeServletContent(String servletContextAttributeName) {
        boolean result = false;
        if (servletContextAttributeName != null && servletContextAttributes.containsKey(servletContextAttributeName)) {
            removedServletContextAttributes.add(servletContextAttributeName);
            result = (servletContextAttributes.remove(servletContextAttributeName) != null);
        }
        return result;
    }

    public boolean removeRequestAttribute(String requestAttributeName) {
        boolean result = false;
        if (requestAttributeName != null && requestAttributes.containsKey(requestAttributeName)) {
            removedRequestAttributes.add(requestAttributeName);
            result = (requestAttributes.remove(requestAttributeName) != null);
        }
        return result;
    }

    public boolean removeSessionAttribute(String sessionAttributeName) {
        boolean result = false;
        if (sessionAttributeName != null && sessionAttributes.containsKey(sessionAttributeName)) {
            removedSessionAttributes.add(sessionAttributeName);
            result = (sessionAttributes.remove(sessionAttributeName) != null);
        }
        return result;
    }

    private void defineConstants(HttpServletRequest httpServletRequest) {
        if (!isConstantsDefined) {
            servletContextRealPath = httpServletRequest.getServletContext().getRealPath(SLASH);
            String userAgentDetails = httpServletRequest.getHeader(USER_AGENT);
            if (userAgentDetails.contains(FIREFOX) || userAgentDetails.contains(CHROME) || userAgentDetails.contains(OPERA)
                    || userAgentDetails.contains(INTERNET_EXPLORER) || userAgentDetails.contains(EDGE) || userAgentDetails.contains(SAFARI)) {
                AudioFormat audioFormat = MP3;
                try {
                    Parser parser = new Parser();
                    Client client = parser.parse(userAgentDetails);
                    audioFormat = BrowserSupportedAudioFormats.getInstance().defineSupportedAudioFromat(client.userAgent);
                    Song.setAudioFormat(audioFormat);
                } catch (IOException e) {
                    logger.error("Parsing browser details error due to ", e);
                }
                isConstantsDefined = true;
            }
        }
    }

    public static String getServletContextRealPath() {
        return servletContextRealPath;
    }

}

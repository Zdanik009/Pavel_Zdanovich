package com.epam.melotrack.servlet;

import com.epam.melotrack.command.*;
import com.epam.melotrack.dao.impl.SongDaoImpl;
import com.epam.melotrack.dao.impl.TourDaoImpl;
import com.epam.melotrack.entity.Game;
import com.epam.melotrack.entity.Statistic;
import com.epam.melotrack.entity.Tour;
import com.epam.melotrack.entity.User;
import com.epam.melotrack.logic.Logic;
import com.epam.melotrack.pool.ConnectionPool;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static com.epam.melotrack.service.Service.*;

@WebServlet(name = "controller", urlPatterns = "/controller")
@MultipartConfig(maxFileSize = 1024 * 1024 * 16, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FrontController extends HttpServlet {

    @Override
    public void init() {
        ConnectionPool.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String commandParameter = req.getParameter(COMMAND);
        CommandType commandType = CommandType.stringValueOf(commandParameter);
        CommandProvider commandProvider = CommandProvider.getInstance();
        Command command = commandProvider.takeCommand(commandType);
        RequestContent requestContent = new RequestContent(req);
        ResponseContent responseContent = command.execute(requestContent);
        requestContent.insertValues(req);
        if (responseContent != null) {
            if (responseContent.getRouter().getType().equals(Router.Type.REDIRECT)) {
                resp.sendRedirect(responseContent.getRouter().getRoute());
            } else {
                req.getRequestDispatcher(responseContent.getRouter().getRoute()).forward(req, resp);
            }
        }
    }

    @Override
    public void destroy() {
        ServletContext servletContext = getServletContext();
        User user = (User) servletContext.getAttribute(USER);
        Statistic statistic = (Statistic) servletContext.getAttribute(USER + UNDERSCORE + STATISTIC);
        Game game = (Game) servletContext.getAttribute(GAME);
        Tour tour = (Tour) servletContext.getAttribute(TOUR);
        String gameStatus = (String) servletContext.getAttribute(GAME + UNDERSCORE + STATUS);
        String tourStatus = (String) servletContext.getAttribute(TOUR + UNDERSCORE + STATUS);
        Logic.leavePlayground(user, statistic, game, tour, gameStatus,tourStatus);
        SongDaoImpl.getAudioTemporaryDirectoryPath().toFile().delete();
        TourDaoImpl.getBlankTemporaryDirectory().toFile().delete();
        ConnectionPool.getInstance().closeConnectionPool();
    }

}

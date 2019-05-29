package com.epam.melotrack.command;

import com.epam.melotrack.command.impl.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CommandProvider {

    private static CommandProvider instance = new CommandProvider();
    private final static Lock LOCK = new ReentrantLock();
    private static AtomicBoolean isPoolCreated = new AtomicBoolean(false);
    private Map<CommandType, Command> commandMap;

    public static CommandProvider getInstance() {
        if (!isPoolCreated.get()) {
            try {
                LOCK.lock();
                if (instance == null) {
                    instance = new CommandProvider();
                    isPoolCreated.set(true);
                }
            } finally {
                LOCK.unlock();
            }
            return instance;
        } else {
            return instance;
        }
    }

    private CommandProvider() {
        commandMap = new EnumMap<>(CommandType.class);
        commandMap.put(CommandType.PREPARE_PAGE, new PreparePageCommand());
        commandMap.put(CommandType.SHOW_MAIN_PAGE, new ShowMainPageCommand());
        commandMap.put(CommandType.SHOW_LOGIN_PAGE, new ShowLoginPageCommand());
        commandMap.put(CommandType.SHOW_UPLOAD_GAME_PAGE, new ShowUploadGamePageCommand());
        commandMap.put(CommandType.SHOW_UPLOAD_TOUR_PAGE, new ShowUploadTourPageCommand());
        commandMap.put(CommandType.SHOW_UPLOAD_SONG_PAGE, new ShowUploadSongPageCommand());
        commandMap.put(CommandType.SHOW_ABOUT_PAGE, new ShowAboutPageCommand());
        commandMap.put(CommandType.CHANGE_LANGUAGE, new ChangeLanguageCommand());
        commandMap.put(CommandType.SIGN_UP_USER, new SignUpUserCommand());
        commandMap.put(CommandType.SIGN_IN_USER, new SignInUserCommand());
        commandMap.put(CommandType.LOGOUT_USER, new LogoutUserCommand());
        commandMap.put(CommandType.LOAD_GAME, new LoadGameCommand());
        commandMap.put(CommandType.LOAD_TOUR, new LoadTourCommand());
        commandMap.put(CommandType.START_TOUR, new StartTourCommand());
        commandMap.put(CommandType.SUBMIT_TOUR, new SubmitTourCommand());
        commandMap.put(CommandType.NEXT_TOUR, new NextTourCommand());
        commandMap.put(CommandType.UPLOAD_GAME, new UploadGameCommand());
        commandMap.put(CommandType.UPLOAD_TOUR, new UploadTourCommand());
        commandMap.put(CommandType.UPLOAD_SONG, new UploadSongCommand());
        commandMap.put(CommandType.LEAVE_PLAYGROUND, new LeavePlaygroundCommand());
        commandMap.put(CommandType.LOAD_COMMON_STATISTIC, new LoadCommonStatisticCommand());
        commandMap.put(CommandType.LOAD_USER_STATISTIC, new LoadUserStatisticCommand());
    }

    public Command takeCommand(CommandType commandType) {
        return commandMap.get(commandType);
    }

}

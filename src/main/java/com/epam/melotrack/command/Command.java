package com.epam.melotrack.command;

import com.epam.melotrack.servlet.RequestContent;

public interface Command {

    ResponseContent execute(RequestContent requestContent);

}

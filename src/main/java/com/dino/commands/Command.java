package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public interface Command {
    void execute(Game game, Logger logge);
    // boolean validate();
    String toString();
}


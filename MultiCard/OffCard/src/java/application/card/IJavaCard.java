package application.card;

import helper.Result;

public interface IJavaCard {
    Result<Boolean> connect();

    Result<byte[]> sendCommand(McCmd cmd);

    void shutdown();

//    void setOnCardInserted(Action action);
//    void setOnCardRemoved(Action action);
}

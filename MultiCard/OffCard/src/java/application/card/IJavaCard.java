package application.card;

import helper.Result;

public interface IJavaCard {
    Result<Boolean> connect();

    Result<byte[]> sendCommand(Cmd cmd);

    void shutdown();
}

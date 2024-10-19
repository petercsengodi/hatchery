package hu.csega.connection.vs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class VSConsole {

    public static void main(String[] args) {
        System.out.println("Use commands like `get token` or `put token blablabla` or `exit` or `quit`:\n");
        String line;

        while(true) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                while((line = reader.readLine()) != null) {
                    line = line.trim();
                    if("quit".equals(line) || "exit".equals(line))
                        break; // user quits

                    try {

                        StringTokenizer mainCommands = new StringTokenizer(line, "|");
                        while(mainCommands.hasMoreTokens()) {
                            String mainCommand = mainCommands.nextToken();
                            List<String> params = new ArrayList<>();

                            StringTokenizer paramTokens = new StringTokenizer(mainCommand);

                            if(paramTokens.hasMoreTokens()) {
                                String cmd = paramTokens.nextToken();
                                while (paramTokens.hasMoreTokens()) {
                                    params.add(paramTokens.nextToken());
                                }

                                runCommand(cmd, params);
                            }
                        }
                    } catch(Throwable t) {
                        t.printStackTrace();
                        // continue work
                    }
                }

                break; // stream is no more
            } catch(IOException ex) {
                throw new RuntimeException(ex);
            } catch(Throwable t) {
                try {
                    t.printStackTrace();
                } catch(Throwable t2) {
                    // this should not happen, but in case...
                }
            }
        }
    }

    private static void runCommand(String cmd, List<String> params) {
        if("get".equals(cmd)) {
            if(params.isEmpty()) {
                System.out.println("\nDon't know, what to get!\n");
            } else {
                String subject = params.get(0);
                if ("token".equals(subject)) {
                    commandGetToken();
                } else {
                    System.out.println("\nDon't know, what to get!\n");
                }
            }
        } else if("put".equals(cmd)) {
            if(params.isEmpty()) {
                System.out.println("\nDon't know, what to put!\n");
            } else {
                String subject = params.get(0);
                if ("token".equals(subject)) {
                    if(params.size() < 2) {
                        System.out.println("\nToken is missing!\n");
                    } else {
                        commandPutToken(params.get(1));
                    }
                } else {
                    System.out.println("\nDon't know, what to put!\n");
                }
            }
        } else {
            System.out.println("\nUnrecognized command!\n");
        }
    }

    private static void commandGetToken() {
        System.out.println("\n");
        DecryptedContent loadedData = DataConnection.INSTANCE.load("token.txt");
        System.out.println("Got: " + loadedData.asString() + '\n');
    }

    private static void commandPutToken(String token) {
        System.out.println("\n");
        DataConnection.INSTANCE.store("token.txt", "text/plain; charset=utf-8", token);
        System.out.println("Putting complete!\n");
    }

}
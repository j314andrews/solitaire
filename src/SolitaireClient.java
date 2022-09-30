import java.io.*;
import java.net.*;

/*
 * Client for the Solitaire game.  This is a modified version of the
 * KnockKnockClient from the following client-server tutorial:
 * https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 * The original version came with the following notice:
 *
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class SolitaireClient {

    public static void main(String[] args) {

        try {
            Socket client = new Socket("192.168.50.42", 7);
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String serverOutput;
            while ((serverOutput = in.readLine()) != null) {
                if (serverOutput.length() == 104) {
                    boolean won = RussianSolitaire.playGameFromDealCode(serverOutput);
                    if (won) {
                        out.println("You won!");
                    } else {
                        out.println("You lost!");
                    }
                } else if (serverOutput.equals("playAgain")) {
                    System.out.println("Do you want to play again?");
                    String input = stdIn.readLine();
                    if (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("Yes")) {
                        out.println("Y");
                    }
                } else if (serverOutput.equals("newGame")) {
                    System.out.println("Dealing New Game...");
                    out.println("");
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Failed to connect to server.");
        } catch (IOException e) {
           System.out.println("File not found.");
        }
    }
}

/**
 * Protocol for the Solitaire game.  This is a modified variation of
 * KnockKnockProtocol from the following client-server tutorial:
 * https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 * The original version came with the following notice:
 *
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
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
public class SolitaireProtocol {

    private int state = START;
    private static final int START = 0;
    private static final int SENT_DEAL = 1;
    private static final int REQUESTED_SCORE = 2;
    private static final int PLAY_AGAIN = 3;

    public String response(String output) {
        System.out.println(state);
        if (state == START) {
            state = SENT_DEAL;
            return RussianSolitaire.randWinnableDealCode();
        } else if (state == SENT_DEAL) {
            if (output.equalsIgnoreCase("You won!") || output.equalsIgnoreCase("You lost!")) {
                state = PLAY_AGAIN;
                return "requestedScore";
            }
        } 
        else if (state == REQUESTED_SCORE) {
            if (output != null) {
                state = PLAY_AGAIN;
                return "playAgain";
            }
        }else if (state == PLAY_AGAIN) {
            if (output.equalsIgnoreCase("Y")) {
                state = START;
                return "newGame";
            } else if (output.equalsIgnoreCase("N")) {
                return "exitGame";
            }
        }
        return "";
    }
}

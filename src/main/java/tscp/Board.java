package tscp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

public class Board implements Constants {

    public Etat[] etats = new Etat[64];

    public int side;
    public int xside;
    public int castle;
    public int ep;
    public List<Move> pseudomoves = new ArrayList<>();
    public int halfMoveClock;
    public int plyNumber;
    public String[] piece_char_light = {"P", "N", "B", "R", "Q", "K"};
    public String[] piece_char_dark = {"p", "n", "b", "r", "q", "k"};
    BiFunction<Integer, Integer, Boolean> pion_side_blanc_attack = (sq, i) -> {
        if ((i & 7) != 0 && i - 9 == sq) return true;
        return (i & 7) != 7 && i - 7 == sq;
    };
    BiFunction<Integer, Integer, Boolean> pion_side_noir_attack = (sq, i) -> {
        if ((i & 7) != 0 && i + 7 == sq) return true;
        return (i & 7) != 7 && i + 9 == sq;
    };
    Consumer<Integer> pion_side_noir = (c) -> {
        if ((c & 7) != 0 && etats[c + 7].color == BLANC) {
            gen_push(c, c + 7, 17);
        }
        if ((c & 7) != 7 && etats[c + 9].color == BLANC) {
            gen_push(c, c + 9, 17);
        }
        if (etats[c + 8].color == EMPTY) {
            gen_push(c, c + 8, 16);
            if (c <= 15 && etats[c + 16].color == EMPTY) {
                gen_push(c, c + 16, 24);
            }
        }
    };
    Consumer<Integer> pion_side_blanc = (c) -> {
        if ((c & 7) != 0 && etats[c - 9].color == DARK) {
            gen_push(c, c - 9, 17);
        }
        if ((c & 7) != 7 && etats[c - 7].color == DARK) {
            gen_push(c, c - 7, 17);
        }
        if (etats[c - 8].color == EMPTY) {
            gen_push(c, c - 8, 16);
            if (c >= 48 && etats[c - 16].color == EMPTY) {
                gen_push(c, c - 16, 24);
            }
        }
    };
    private int fifty;
    private UndoMove um = new UndoMove();

    public Board() {
        range(0, 64)
                .forEach(c -> etats[c] = new Etat(0, 0));

    }

    public Board(Board board) {
        etats = board.etats;
//        color = board.color;
//        pieces = board.pieces;
        side = board.side;
        xside = board.xside;
        castle = board.castle;
        ep = board.ep;
        fifty = board.fifty;
        pseudomoves = new ArrayList<>();
        um = new UndoMove();
    }

//    Function<Integer, Predicate> fin_check = s -> {
//        stream(CASES).forEach(c -> {
//                    if (etats[c].type == KING && etats[c].color == s) return attack(c, s ^ 1);
//                }
//        );
//        return true; // shouldn't get here
//    };

    private boolean in_check(int s) {
        for (int i = 0; i < 64; ++i) {
            if (etats[i].type == KING && etats[i].color == s) {
                return attack(i, s ^ 1);
            }
        }
        return true; // shouldn't get here
    }

    private boolean attack(int sq, int s) {
        for (int c = 0; c < 64; ++c) {
            if (etats[c].color == s) {
                if (etats[c].type == PAWN) {
                    if (s == BLANC) {
                        if (pion_side_blanc_attack.apply(sq, c)) return true;
                    } else if (pion_side_noir_attack.apply(sq, c)) return true;
                } else {
                    // recursive ?
                    for (int dir = 0; dir < nb_dir[etats[c].type]; ++dir) {

                        int _c = c;
                        do {
                            _c = getMailbox(etats[c].type, dir, _c);
                            if (_c == sq) return true;
                        } while (_c != OUT && etats[_c].color == EMPTY && slide[etats[c].type]);

                    }
                    //
                }
            }
        }
        return false;
    }

    public void gen(int side) {
        stream(CASES).filter(c -> etats[c].color == side).forEach(c -> {
            int type = etats[c].type;
            if (type == PAWN) if (side == BLANC) pion_side_blanc.accept(c);
            else pion_side_noir.accept(c);
            else range(0, nb_dir[type]).forEach(dir -> {
                int _c = c;
                do _c = getMailbox(type, dir, _c);
                while (_c != OUT && !gen_part(c, _c) && slide[type]);
            });
        });

        /* generate castle moves */
        ep();
        /* generate en passant moves */
        roques();
    }

    private int getMailbox(int piece, int dir, int _c) {
        return mailbox[mailbox64[_c] + offset[piece][dir]];
    }

    private boolean gen_part(int c, int _c) {
        int color = etats[_c].color;
        if (color != EMPTY) {
            if (color == xside) gen_push(c, _c, 1);
            return true;
        }
        gen_push(c, _c, 0);
        return false;
    }

    private void roques() {
        if (ep != -1) {
            if (side == BLANC) {
                if ((ep & 7) != 0 && etats[ep + 7].color == BLANC && etats[ep + 7].type == PAWN) {
                    gen_push(ep + 7, ep, 21);
                }
                if ((ep & 7) != 7 && etats[ep + 9].color == BLANC && etats[ep + 9].type == PAWN) {
                    gen_push(ep + 9, ep, 21);
                }
            } else {
                if ((ep & 7) != 0 && etats[ep - 9].color == DARK && etats[ep - 9].type == PAWN) {
                    gen_push(ep - 9, ep, 21);
                }
                if ((ep & 7) != 7 && etats[ep - 7].color == DARK && etats[ep - 7].type == PAWN) {
                    gen_push(ep - 7, ep, 21);
                }
            }
        }
    }

    private void ep() {
        if (side == BLANC) {
            if ((castle & 1) != 0) {
                gen_push(E1, G1, 2);
            }
            if ((castle & 2) != 0) {
                gen_push(E1, C1, 2);
            }
        } else {
            if ((castle & 4) != 0) {
                gen_push(E8, G8, 2);
            }
            if ((castle & 8) != 0) {
                gen_push(E8, C8, 2);
            }
        }
    }

    private void gen_push(int from, int to, int bits) {
        if ((bits & 16) != 0) {
            if (side == BLANC) {
                if (to <= H8) {
                    gen_promote(from, to, bits);
                    return;
                }
            } else if (to >= A1) {
                gen_promote(from, to, bits);
                return;
            }
        }
        pseudomoves.add(new Move((byte) from, (byte) to, (byte) 0, (byte) bits));

    }

    private void gen_promote(int from, int to, int bits) {
        for (int i = KNIGHT; i <= QUEEN; ++i) {
            pseudomoves.add(new Move((byte) from, (byte) to, (byte) i, (byte) (bits | 32)));
        }
    }

    public boolean makemove(Move m) {
        if ((m.bits & 2) != 0) {
            int from;
            int to;

            if (in_check(side)) {
                return false;
            }
            switch (m.to) {
                case 62:
                    if (etats[F1].color != EMPTY || etats[G1].color != EMPTY || attack(F1, xside) || attack(G1, xside)) {
                        return false;
                    }
                    from = H1;
                    to = F1;
                    break;
                case 58:
                    if (etats[B1].color != EMPTY || etats[C1].color != EMPTY || etats[D1].color != EMPTY || attack(C1, xside) || attack(D1, xside)) {
                        return false;
                    }
                    from = A1;
                    to = D1;
                    break;
                case 6:
                    if (etats[F8].color != EMPTY || etats[G8].color != EMPTY || attack(F8, xside) || attack(G8, xside)) {
                        return false;
                    }
                    from = H8;
                    to = F8;
                    break;
                case 2:
                    if (etats[B8].color != EMPTY || etats[C8].color != EMPTY || etats[D8].color != EMPTY || attack(C8, xside) || attack(D8, xside)) {
                        return false;
                    }
                    from = A8;
                    to = D8;
                    break;
                default: // shouldn't get here
                    from = -1;
                    to = -1;
                    break;
            }
            etats[to].color = etats[from].color;
            etats[to].type = etats[from].type;
            etats[from].color = EMPTY;
            etats[from].type = EMPTY;
        }

        /* back up information, so we can take the move back later. */
        um.mov = m;
        um.capture = etats[m.to].type;
        um.castle = castle;
        um.ep = ep;
        um.fifty = fifty;

        castle &= castle_mask[m.from] & castle_mask[m.to];

        if ((m.bits & 8) != 0) {
            if (side == BLANC) {
                ep = m.to + 8;
            } else {
                ep = m.to - 8;
            }
        } else {
            ep = -1;
        }
        if ((m.bits & 17) != 0) {
            fifty = 0;
        } else {
            ++fifty;
        }

        /* move the piece */
        etats[m.to].color = side;
        if ((m.bits & 32) != 0) {
            etats[m.to].type = m.promote;
        } else {
            etats[m.to].type = etats[m.from].type;
        }
        etats[m.from].color = EMPTY;
        etats[m.from].type = EMPTY;

        /* erase the pawn if this is an en passant move */
        if ((m.bits & 4) != 0) {
            if (side == BLANC) {
                etats[m.to + 8].color = EMPTY;
                etats[m.to + 8].type = EMPTY;
            } else {
                etats[m.to - 8].color = EMPTY;
                etats[m.to - 8].type = EMPTY;
            }
        }

        side ^= 1;
        xside ^= 1;
        if (in_check(xside)) {
            takeback();
            return false;
        }

        return true;
    }

    public void takeback() {

        side ^= 1;
        xside ^= 1;

        Move m = um.mov;
        castle = um.castle;
        ep = um.ep;
        fifty = um.fifty;

        etats[m.from].color = side;
        if ((m.bits & 32) != 0) {
            etats[m.from].type = PAWN;
        } else {
            etats[m.from].type = etats[m.to].type;
        }
        if (um.capture == EMPTY) {
            etats[m.to].color = EMPTY;
            etats[m.to].type = EMPTY;
        } else {
            etats[m.to].color = xside;
            etats[m.to].type = um.capture;
        }
        if ((m.bits & 2) != 0) {
            int from;
            int to;

            switch (m.to) {
                case 62:
                    from = F1;
                    to = H1;
                    break;
                case 58:
                    from = D1;
                    to = A1;
                    break;
                case 6:
                    from = F8;
                    to = H8;
                    break;
                case 2:
                    from = D8;
                    to = A8;
                    break;
                default: // shouldn't get here
                    from = -1;
                    to = -1;
                    break;
            }
            etats[to].color = side;
            etats[to].type = ROOK;
            etats[from].color = EMPTY;
            etats[from].type = EMPTY;
        }
        if ((m.bits & 4) != 0) {
            if (side == BLANC) {
                etats[m.to + 8].color = xside;
                etats[m.to + 8].type = PAWN;
            } else {
                etats[m.to - 8].color = xside;
                etats[m.to - 8].type = PAWN;
            }
        }
    }

//
//    public void print_board() {
//        int i;
//
//        System.out.print("\n8 ");
//        for (i = 0; i < 64; ++i) {
//            switch (color[i]) {
//                case EMPTY:
//                    System.out.print(". ");
//                    break;
//                case BLANC:
//                    System.out.printf(piece_char_light[pieces[i]] + " ");
//                    break;
//                case DARK:
//                    System.out.printf(piece_char_dark[pieces[i]] + " ");
//                    break;
//            }
//            if ((i + 1) % 8 == 0 && i != 63) {
//                System.out.printf("\n%d ", 7 - (i >> 3));
//            }
//        }
//        System.out.print("\n\n   a b c d e f g h\n\n");
//    }

}

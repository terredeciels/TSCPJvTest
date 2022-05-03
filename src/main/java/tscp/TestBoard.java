package tscp;

public class TestBoard {

    public void gen(int[] color, int[] pieces, int side, int xside, int sq, Constants C) {
        for (int _case = 0; _case < 64; ++_case) {
            int piece = pieces[_case];
            if (color[_case] == side) {
                if (piece == C.PAWN) {
                    if (side == C.LIGHT) {
                        side_pawn_whitetoPlay();
                        //OU
                        boolean b = attaque_pion == true;//ou false
                    } else {
                        side_pawn_blacktoPlay();
                        //OU
                        boolean b = attaque_pion == true;//ou false
                    }
                } else {

                    for (int dir = 0; dir < C.nb_dir[piece]; ++dir) {
                        //for recursif
                        for (int c = _case; ; ) {
                            c = C.mailbox[C.mailbox64[c] + C.offset[piece][dir]];// _C + k*dir
                            if (c == C.OUT) break;
                            // .....
                            // .....
                            if (!C.slide[piece]) break;

                            //---------attaque-------------
                            if (c == sq)// return true;
                                if (color[c] != C.EMPTY) {
                                    break;
                                }

                            //-----------gen--------------
                            if (color[c] != C.EMPTY) {
                                if (color[c] == xside) {
                                    // gen_push(_case, n, 1);
                                }
                                break;
                            }
                            // gen_push(_case, n, 0);

                        }
                    }
                }
            }
        }
    }
}

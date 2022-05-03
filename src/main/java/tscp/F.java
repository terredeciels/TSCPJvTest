package tscp;

public class F {
//
//    BiFunction<Integer, Integer, Boolean> pion_side_blanc_attack = (sq, i) -> {
//        if ((i & 7) != 0 && i - 9 == sq) return true;
//        return (i & 7) != 7 && i - 7 == sq;
//    };
//    BiFunction<Integer, Integer, Boolean> pion_side_noir_attack = (sq, i) -> {
//        if ((i & 7) != 0 && i + 7 == sq) return true;
//        return (i & 7) != 7 && i + 9 == sq;
//    };
//    Consumer<Integer> pion_side_noir = (i) -> {
//        if ((i & 7) != 0 && color[i + 7] == BLANC) {
//            gen_push(i, i + 7, 17);
//        }
//        if ((i & 7) != 7 && color[i + 9] == BLANC) {
//            gen_push(i, i + 9, 17);
//        }
//        if (color[i + 8] == EMPTY) {
//            gen_push(i, i + 8, 16);
//            if (i <= 15 && color[i + 16] == EMPTY) {
//                gen_push(i, i + 16, 24);
//            }
//        }
//    };
//    Consumer<Integer> pion_side_blanc = (i) -> {
//        if ((i & 7) != 0 && color[i - 9] == DARK) {
//            gen_push(i, i - 9, 17);
//        }
//        if ((i & 7) != 7 && color[i - 7] == DARK) {
//            gen_push(i, i - 7, 17);
//        }
//        if (color[i - 8] == EMPTY) {
//            gen_push(i, i - 8, 16);
//            if (i >= 48 && color[i - 16] == EMPTY) {
//                gen_push(i, i - 16, 24);
//            }
//        }
//    };


}

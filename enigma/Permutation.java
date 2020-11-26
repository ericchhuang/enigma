package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Eric Huang
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles = _cycles + " " + cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char letter = _alphabet.toChar(p);
        return _alphabet.toInt(permute(letter));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char letter = _alphabet.toChar(c);
        return _alphabet.toInt(invert(letter));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int i = _cycles.indexOf(p);
        if (i == -1) {
            return p;
        } else if (_cycles.charAt(i + 1) == ')') {
            if (_cycles.charAt(i - 1) == '(') {
                return _cycles.charAt(i);
            }
            while (_cycles.charAt(i - 1) != '(') {
                i--;
            }
            return _cycles.charAt(i);
        } else {
            return _cycles.charAt(i + 1);
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int i = _cycles.indexOf(c);
        if (i == -1) {
            return c;
        }
        if (_cycles.charAt(i - 1) == '(') {
            if (_cycles.charAt(i + 1) == ')') {
                return _cycles.charAt(i);
            }
            while (_cycles.charAt(i + 1) != ')') {
                i++;
            }
            return _cycles.charAt(i);
        } else {
            return _cycles.charAt(i - 1);
        }
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < size(); i++) {
            if (_cycles.indexOf(_alphabet.toChar(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles for this permutation. */
    private String _cycles;
}

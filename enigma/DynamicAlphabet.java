package enigma;

/** Alphabet written as a sequence of letters.
 * @author Eric Huang
 */
public class DynamicAlphabet extends Alphabet {

    /** Initialize the alphabet using ALPHABET.
     */
    DynamicAlphabet(String alphabet) {
        _alphabet = alphabet;
    }

    @Override
    int size() {
        return _alphabet.length();
    }

    @Override
    boolean contains(char ch) {
        if (_alphabet.indexOf(ch) == -1) {
            return false;
        }
        return true;
    }

    @Override
    char toChar(int index) {
        return _alphabet.charAt(index);
    }

    @Override
    int toInt(char ch) {
        return _alphabet.indexOf(ch);
    }

    /** Sequence of letters for this alphabet. */
    private String _alphabet;
}

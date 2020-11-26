package enigma;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Eric Huang
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        set(0);
        _notches = notches;
    }

    @Override
    void advance() {
        if (setting() >= alphabet().size() - 1) {
            set(0);
        } else {
            set(setting() + 1);
        }
    }

    @Override
    boolean atNotch() {
        char notch = alphabet().toChar(setting());
        if (_notches.indexOf(notch) != -1) {
            return true;
        }
        return false;
    }

    @Override
    boolean rotates() {
        return true;
    }

    /** Notches for this rotor. */
    private String _notches;
}

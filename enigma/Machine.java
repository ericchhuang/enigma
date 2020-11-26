package enigma;

import java.util.Collection;
import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Eric Huang
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _plugboard = new Permutation("", _alphabet);
    }

    /** Returns my plugboard. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the rotors to this arraylist to NEWROTORS.
     */
    void setRotors(ArrayList<Rotor> newRotors) {
        _rotors = newRotors;
    }

    /** Returns the rotors I have. */
    ArrayList<Rotor> rotors() {
        return _rotors;
    }

    /** Return the number of pawls I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        int size = 0;
        for (int i = 0; i < _numRotors; i++) {
            for (Rotor element: _allRotors) {
                if (element.name().equals(rotors[i])) {
                    _rotors.add(element);
                }
            }
            size++;
            if (_rotors.size() != size) {
                throw error("Bad rotor name");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i++) {
            int setnumber = _alphabet.toInt(setting.charAt(i));
            _rotors.get(i + 1).set(setnumber);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine.
     */
    int convert(int c) {
        c = _plugboard.permute(c);
        for (int rotorno = _rotors.size() - 1; rotorno >= 0; rotorno--) {
            c = _rotors.get(rotorno).convertForward(c);
        }
        for (int rotorno = 1; rotorno < _rotors.size(); rotorno++) {
            c = _rotors.get(rotorno).convertBackward(c);
        }
        return _plugboard.permute(c);
    }

    /** Advances the left rotor to any rotor that is positioned at its notch.
     * Advances the rotor at the notch if it has not advanced yet */
    void advance() {
        ArrayList<Boolean> trigger = new ArrayList<Boolean>();
        for (int i = 0; i < _rotors.size(); i++) {
            trigger.add(false);
        }
        for (int i = 0; i < _rotors.size(); i++) {
            if (_rotors.get(i).atNotch() && _rotors.get(i - 1).rotates()) {
                trigger.set(i, true);
                trigger.set(i - 1, true);
            }
        }
        trigger.set(_rotors.size() - 1, true);
        for (int i = 0; i < _rotors.size(); i++) {
            if (trigger.get(i)) {
                _rotors.get(i).advance();
            }
        }
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String converted = "";
        for (int i = 0; i < msg.length(); i++) {
            char element = msg.charAt(i);
            if (element == ' ') {
                converted += ' ';
            } else {
                advance();
                int letterno = _alphabet.toInt(msg.charAt(i));
                converted += _alphabet.toChar(convert(letterno));
            }
        }
        return converted;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors for this machine. */
    private int _numRotors;

    /** Number of pawls for this machine. */
    private int _pawls;

    /** Collection of all rotors capabable of being used by this machine. */
    private Collection<Rotor> _allRotors;

    /** Arraylist of all rotors inserted into this machine in order,
     * with reflector being index 0.
     */
    private ArrayList<Rotor> _rotors = new ArrayList<Rotor>();

    /** Plugboard for this machine. */
    private Permutation _plugboard;
}

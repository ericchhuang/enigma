package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Eric Huang
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();
        if (!_input.hasNext("[*].*")) {
            throw error("Missing setting");
        }
        while (_input.hasNextLine()) {
            String line = _input.nextLine();
            if (line.matches("[*].*")) {
                setUp(M, line);
                if (!(M.rotors().get(0) instanceof Reflector)) {
                    throw error("First rotor isn't reflector");
                }
            } else {
                String translation = M.convert(line.toUpperCase());
                printMessageLine(translation);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        if (!_config.hasNext("[A-Za-z0-9].*")) {
            throw error("Bad alphabet");
        }
        try {
            if (_config.hasNext("[A-Z]-[A-Z]")) {
                String charRange = (_config.nextLine());
                char beg = charRange.charAt(0);
                char end = charRange.charAt(2);
                _alphabet = new CharacterRange(beg, end);
            } else {
                _alphabet = new DynamicAlphabet(_config.nextLine());
            }

            if (!_config.hasNext("[0-9]*")) {
                throw error("Bad rotor number");
            }
            int numRotors = Integer.parseInt(_config.next());

            if (!_config.hasNext("[0-9]*")) {
                throw error("Bad pawl number");
            }

            int pawls = Integer.parseInt(_config.next());

            ArrayList<Rotor> allRotors = new ArrayList<Rotor>();
            while (_config.hasNextLine() && _config.hasNext(".*")) {
                allRotors.add(readRotor());
            }

            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String details = _config.next();
            char type = details.charAt(0);
            String notches = details.substring(1);
            _config.skip("\\s*");

            String cycles = "";
            while (_config.hasNext("[(].*")) {
                _config.skip("\\s*");
                if (!_config.hasNext(".*[)]")) {
                    throw error("Incorrect cycles");
                }
                cycles += _config.next();
            }

            if (type == 'M') {
                return new MovingRotor(name.toUpperCase(),
                        new Permutation(cycles, _alphabet), notches);
            } else if (type == 'N') {
                return new FixedRotor(name.toUpperCase(),
                        new Permutation(cycles, _alphabet));
            } else {
                return new Reflector(name.toUpperCase(),
                        new Permutation(cycles, _alphabet));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        if (settings.charAt(0) != '*') {
            throw error("Missing setting");
        }
        M.setRotors(new ArrayList<Rotor>());
        M.setPlugboard(new Permutation("", _alphabet));
        String[] rotors = new String[M.numRotors()];
        String plugboard = "";
        String name = "";
        int count = 0;
        int i = 2;

        try {
            while (count != M.numRotors()) {
                if (settings.charAt(i) == ' ') {
                    rotors[count] = name;
                    count++;
                    name = "";
                } else {
                    name += settings.charAt(i);
                }
                i++;
            }
        } catch (StringIndexOutOfBoundsException excp) {
            throw error("Incorrect number of rotors");
        }


        while (settings.charAt(i) != ' ') {
            name += settings.charAt(i);
            i++;
            plugboard = settings.substring(i);
            if (i > (settings.length() - 1)) {
                break;
            }
        }

        M.insertRotors(rotors);
        int moving = 0;
        for (Rotor element : M.rotors()) {
            if (element instanceof MovingRotor) {
                moving++;
            }
        }
        if (moving != M.numPawls()) {
            throw error("Wrong number of moving rotors");
        }
        M.setRotors(name);
        for (int j = 0; j < plugboard.length(); j++) {
            char item = plugboard.charAt(j);
            if (item != '(' && item != ')' && item != ' ') {
                String temp = plugboard.substring(j + 1);
                if (temp.indexOf(plugboard.charAt(j)) != -1) {
                    throw error("Plugboard repeats");
                }
            }
        }
        M.setPlugboard(new Permutation(plugboard, _alphabet));
    }


    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String result = "";
        int counter = 0;
        msg = msg.trim();
        for (int i = 0; msg.length() > i; i++) {
            if (counter % 5 == 0 && counter != 0
                    && result.charAt(result.length() - 1) != ' ') {
                result += ' ';
            }
            if (msg.charAt(i) != ' ') {
                result += msg.charAt(i);
                counter++;
            }
        }
        _output.println(result);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}


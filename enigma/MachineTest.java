package enigma;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

import static enigma.TestUtils.*;

public class MachineTest {

    Rotor b = new Reflector("B",
            new Permutation(NAVALA.get("B"), UPPER));
    Rotor beta = new FixedRotor("Beta",
            new Permutation(NAVALA.get("Beta"), UPPER));
    Rotor i = new MovingRotor("I",
            new Permutation(NAVALA.get("I"), UPPER), "Q");
    Rotor ii = new MovingRotor("II",
            new Permutation(NAVALA.get("II"), UPPER), "E");
    Rotor iii = new MovingRotor("III",
            new Permutation(NAVALA.get("III"), UPPER), "V");

    @Test
    public void insertRotorsTest() {
        ArrayList<Rotor> allRotors =  new ArrayList<Rotor>();
        allRotors.add(b); allRotors.add(beta); allRotors.add(i);
        allRotors.add(ii); allRotors.add(iii);

        String[] rotors = {"B", "Beta", "I", "III"};

        Machine testmachine = new Machine(UPPER, 4, 2, allRotors);
        testmachine.insertRotors(rotors);

        ArrayList<Rotor> expected = new ArrayList<Rotor>();
        expected.add(b); expected.add(beta);
        expected.add(i); expected.add(iii);
        assertEquals(expected, testmachine.rotors());
    }

    @Test
    public void setRotorsTest() {
        ArrayList<Rotor> allRotors =  new ArrayList<Rotor>();
        allRotors.add(b); allRotors.add(beta); allRotors.add(i);
        allRotors.add(ii); allRotors.add(iii);

        String[] rotors = {"B", "Beta", "I", "III"};

        Machine testmachine = new Machine(UPPER, 4, 2, allRotors);
        testmachine.insertRotors(rotors);
        testmachine.setRotors("JFK");

        assertEquals("Reflector should not change setting",
                0, testmachine.rotors().get(0).setting());
        assertEquals("Wrong setting for FixedRotor Beta",
                9, testmachine.rotors().get(1).setting());
        assertEquals("Wrong setting for Rotor I",
                5, testmachine.rotors().get(2).setting());
        assertEquals("Wrong setting for Rotor III",
                10, testmachine.rotors().get(3).setting());
    }

    @Test
    public void convertTest() {
        ArrayList<Rotor> allRotors =  new ArrayList<Rotor>();
        allRotors.add(b); allRotors.add(beta); allRotors.add(i);
        allRotors.add(ii); allRotors.add(iii);

        String[] rotors = {"B", "Beta", "I", "III"};

        Machine testmachine = new Machine(UPPER, 4, 2, allRotors);
        testmachine.insertRotors(rotors);
        assertEquals("Incorrect rotors have been inserted",
                20, testmachine.convert(0));

        testmachine.setPlugboard(new Permutation("(EA) (JU)", UPPER));
        assertEquals("Incorrect plugboard", 9, testmachine.convert(4));

        testmachine.setRotors("JFK");
        assertEquals("One or more rotors have incorrect setting",
                18, testmachine.convert(4));

        String[] rotorstwo = {"B", "Beta", "I", "II", "III"};

        Machine testmachinetwo = new Machine(UPPER, 5, 3, allRotors);
        testmachinetwo.insertRotors(rotorstwo);
        testmachinetwo.setPlugboard(new Permutation("(AQ) (EP)", UPPER));
        testmachinetwo.setRotors("AAAB");

        assertEquals("Wrong conversion of H",
                8, testmachinetwo.convert(7));
    }

    @Test
    public void encryptionTest() {
        ArrayList<Rotor> allRotors =  new ArrayList<Rotor>();
        allRotors.add(b); allRotors.add(beta); allRotors.add(i);
        allRotors.add(ii); allRotors.add(iii);

        String[] rotors = {"B", "Beta", "I", "II", "III"};

        Machine testmachine = new Machine(UPPER, 5, 3, allRotors);
        testmachine.insertRotors(rotors);
        testmachine.setPlugboard(new Permutation("(AQ) (EP)", UPPER));
        testmachine.setRotors("AAAA");
        assertEquals("Wrong string encryption",
                "IHBDQ QMTQZ", testmachine.convert("HELLO WORLD"));
    }

    @Test
    public void decryptionTest() {
        ArrayList<Rotor> allRotors =  new ArrayList<Rotor>();
        allRotors.add(b); allRotors.add(beta); allRotors.add(i);
        allRotors.add(ii); allRotors.add(iii);

        String[] rotors = {"B", "Beta", "I", "II", "III"};

        Machine testmachine = new Machine(UPPER, 5, 3, allRotors);
        testmachine.insertRotors(rotors);
        testmachine.setPlugboard(new Permutation("(AQ) (EP)", UPPER));
        testmachine.setRotors("AAAA");
        assertEquals("Wrong string decryption",
                "HELLO WORLD", testmachine.convert("IHBDQ QMTQZ"));
    }
}

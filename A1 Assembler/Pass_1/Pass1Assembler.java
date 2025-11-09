import java.io.*;
import java.util.*;

public class Pass1Assembler {
    private final Map<String, String> opcodeTable = new HashMap<>(); // (Mnemonic, (class,opcode))
    private final List<String[]> symtab = new ArrayList<>(); // Array: Symbol, Address (as String)
    private int LC = 0;

    public Pass1Assembler() {
        // Add Imperative Statements (IS)
        opcodeTable.put("STOP", "IS,00");
        opcodeTable.put("ADD", "IS,01");
        opcodeTable.put("SUB", "IS,02");
        opcodeTable.put("MULT", "IS,03");
        opcodeTable.put("MOVER", "IS,04");
        opcodeTable.put("MOVEM", "IS,05");
        opcodeTable.put("COMP", "IS,06");
        opcodeTable.put("BC", "IS,07");
        opcodeTable.put("DIV", "IS,08");
        opcodeTable.put("READ", "IS,09");
        opcodeTable.put("PRINT", "IS,10");
        // Add Declarative Statements (DL)
        opcodeTable.put("DC", "DL,02");
        opcodeTable.put("DS", "DL,01");
        // Add Assembler Directives (AD)
        opcodeTable.put("START", "AD,01");
        opcodeTable.put("END", "AD,02");
        opcodeTable.put("ORIGIN", "AD,03");
        opcodeTable.put("EQU", "AD,04");
        opcodeTable.put("LTORG", "AD,05");
    }

    // Returns (index + 1) for symtab, creates if needed
    private int getSymbolIndex(String sym) {
        for (int i = 0; i < symtab.size(); i++) {
            if (symtab.get(i)[0].equals(sym)) return i;
        }
        symtab.add(new String[]{sym, "-1"});
        return symtab.size() - 1;
    }

    private int updateSymbol(String sym, String addr) {
        int idx = getSymbolIndex(sym);
        symtab.get(idx)[1] = addr;
        return idx;
    }

    public void generate() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("input.asm"));
        BufferedWriter ic = new BufferedWriter(new FileWriter("ic.txt"));
        BufferedWriter st = new BufferedWriter(new FileWriter("sym.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split("[\\s,]+");

            int index = 0;
            String label = "";
            if (!opcodeTable.containsKey(tokens[index].toUpperCase())) {
                label = tokens[index];
                updateSymbol(label, String.valueOf(LC));
                index++;
            }

            if (index >= tokens.length) continue; // Only label

            String mnemonic = tokens[index].toUpperCase();
            String codeCls = opcodeTable.getOrDefault(mnemonic, "");
            String[] codeParts = codeCls.isEmpty() ? new String[]{"", ""} : codeCls.split(",");

            // Handle directives
            if (mnemonic.equals("START")) {
                LC = Integer.parseInt(tokens[index + 1]);
                ic.write("(AD,01)(C," + LC + ")\n");
            } else if (mnemonic.equals("END")) {
                ic.write("(AD,02)\n");
            } else if (mnemonic.equals("ORIGIN")) {
                // Origin = Symbol (+/-) constant or constant
                String target = tokens[index + 1];
                if (target.matches("\\d+")) LC = Integer.parseInt(target);
                else if (target.contains("+")) {
                    String[] parts = target.split("\\+");
                    int addr = Integer.parseInt(symtab.get(getSymbolIndex(parts[0]))[1]);
                    LC = addr + Integer.parseInt(parts[1]);
                } else if (target.contains("-")) {
                    String[] parts = target.split("-");
                    int addr = Integer.parseInt(symtab.get(getSymbolIndex(parts[0]))[1]);
                    LC = addr - Integer.parseInt(parts[1]);
                }
                ic.write("(AD,03)(C," + LC + ")\n");
            } else if (mnemonic.equals("EQU")) {
                // Label EQU symbol (+/-) constant or symbol
                String value = tokens[index + 1];
                int res;
                if (value.matches("\\d+")) res = Integer.parseInt(value);
                else if (value.contains("+")) {
                    String[] parts = value.split("\\+");
                    int addr = Integer.parseInt(symtab.get(getSymbolIndex(parts[0]))[1]);
                    res = addr + Integer.parseInt(parts[1]);
                } else if (value.contains("-")) {
                    String[] parts = value.split("-");
                    int addr = Integer.parseInt(symtab.get(getSymbolIndex(parts[0]))[1]);
                    res = addr - Integer.parseInt(parts[1]);
                } else res = Integer.parseInt(symtab.get(getSymbolIndex(value))[1]);
                updateSymbol(label, String.valueOf(res));
                ic.write("(AD,04)(C," + res + ")\n");
            }
            // Declarative
            else if (mnemonic.equals("DC")) {
                updateSymbol(label, String.valueOf(LC));
                ic.write("(DL,02)(C," + tokens[index + 1].replaceAll("'", "" ) + ")\n");
                LC++;
            } else if (mnemonic.equals("DS")) {
                updateSymbol(label, String.valueOf(LC));
                ic.write("(DL,01)(C," + tokens[index + 1] + ")\n");
                LC += Integer.parseInt(tokens[index + 1]);
            }
            // Imperative
            else if (codeCls.startsWith("IS")) {
                StringBuilder lineIC = new StringBuilder();
                lineIC.append("(").append(codeCls).append(") ");
                // Register/Condition handling
                int t = index + 1;
                String regmap = "AREG,1 BREG,2 CREG,3 DREG,4".toUpperCase();
                if (t < tokens.length && regmap.contains(tokens[t].toUpperCase())) {
                    String reg = tokens[t].toUpperCase();
                    lineIC.append("(").append(regmap.split(reg + ",")[1].charAt(0)).append(") ");
                    t++;
                }
                // Operand: symbol or constant
                if (t < tokens.length) {
                    String operand = tokens[t];
                    if (operand.matches("\\d+")) { // constant
                        lineIC.append("(C,").append(operand).append(")");
                    } else {
                        int symidx = getSymbolIndex(operand.replaceAll("[,]", ""));
                        lineIC.append("(S,").append(symidx).append(")");
                    }
                }
                ic.write(lineIC.toString() + "\n");
                LC++;
            }
        }
        ic.close();
        // Print symtab
        for (String[] s : symtab)
            st.write(s[0] + " " + (s[1].equals("-1") ? "0" : s[1]) + "\n");
        st.close();
    }

    public static void main(String[] args) throws Exception {
        new Pass1Assembler().generate();
        System.out.println("Pass 1 completed. Symbol Table: sym.txt and Intermediate Code: ic.txt generated.");
    }
}

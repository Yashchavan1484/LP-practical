import java.io.*;
import java.util.*;

public class Pass2Assembler {
    public static void main(String[] args) throws Exception {
        // Read Symbol Table (handles with/without header)
        List<Integer> symAddr = new ArrayList<>();
        BufferedReader brsym = new BufferedReader(new FileReader("sym.txt"));
        String line;
        boolean headerSkipped = false;
        while ((line = brsym.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            // Heuristically skip header if present
            if (!headerSkipped && (parts[0].equalsIgnoreCase("Symbol") || parts[0].equalsIgnoreCase("Sym") || !parts[1].matches("\\d+"))) {
                headerSkipped = true; // skip first line if not address
                continue;
            }
            if (parts.length >= 2 && parts[1].matches("\\d+"))
                symAddr.add(Integer.parseInt(parts[1]));
        }
        brsym.close();

        // Read Intermediate Code and write object code
        BufferedReader bric = new BufferedReader(new FileReader("ic.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("object.txt"));

        while ((line = bric.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.contains("Intermediate")) continue;

            // Remove parenthesis and split
            line = line.replace("(", " ").replace(")", " ").replace(",", " ");
            String[] parts = line.trim().split("\\s+");

            // Only IS instructions processed
            if (parts.length >= 2 && parts[0].equals("IS")) {
                String opcode = parts[1];
                String reg = (parts.length > 2 && parts[2].matches("\\d")) ? parts[2] : "0";
                String addr = "000";
                for (int i = 3; i < parts.length; i++) {
                    if (parts[i].equals("S") && i + 1 < parts.length) {
                        int idx = Integer.parseInt(parts[i + 1]);
                        if (idx >= 0 && idx < symAddr.size())
                            addr = String.format("%03d", symAddr.get(idx));
                        else
                            addr = "000";
                    }
                    if (parts[i].equals("C") && i + 1 < parts.length) {
                        addr = String.format("%03d", Integer.parseInt(parts[i + 1]));
                    }
                }
                bw.write(opcode + " " + reg + " " + addr + "\n");
            }
        }
        bric.close();
        bw.close();
        System.out.println("Object code generated in object.txt");
    }
}
